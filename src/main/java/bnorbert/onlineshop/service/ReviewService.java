package bnorbert.onlineshop.service;

import ai.djl.Application;
import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.nlp.DefaultVocabulary;
import ai.djl.modality.nlp.Vocabulary;
import ai.djl.modality.nlp.bert.BertFullTokenizer;
import ai.djl.modality.nlp.bert.BertTokenizer;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.Block;
import ai.djl.nn.LambdaBlock;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.nn.norm.Dropout;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.ProductMapper;
import bnorbert.onlineshop.mapper.ReviewMapper;
import bnorbert.onlineshop.repository.ProductRepository;
import bnorbert.onlineshop.repository.ReviewRepository;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.review.CreateReviewRequest;
import bnorbert.onlineshop.transfer.review.GetReviewsRequest;
import bnorbert.onlineshop.transfer.review.ReviewResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Service
@Slf4j
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final UserService userService;
    private final ReviewMapper reviewMapper;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final EntityManager entityManager;

    public ReviewService(ReviewRepository reviewRepository, ProductService productService, UserService userService,
                         ReviewMapper reviewMapper, ProductRepository productRepository, ProductMapper productMapper, EntityManager entityManager) {
        this.reviewRepository = reviewRepository;
        this.productService = productService;
        this.userService = userService;
        this.reviewMapper = reviewMapper;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.entityManager = entityManager;
    }

    public void createReview(CreateReviewRequest request) throws MalformedModelException, IOException, ModelNotFoundException, TranslateException {
        log.info("Creating review: {}", request);
        if (userService.isLoggedIn()) {
            Product product = productService.getProduct(request.getProductId());

            Optional<Review> productAndUser = reviewRepository
                    .findTopByProductAndUserOrderByIdDesc(product, userService.getCurrentUser());
            if (productAndUser.isPresent()) {
                throw new ResourceNotFoundException("You have already reviewed this product: " + request.getProductId());
            }

            Review review = reviewMapper.map(request, product, userService.getCurrentUser());

            String modelUrls = "https://resources.djl.ai/test-models/distilbert.zip";
            Criteria<NDList, NDList> criteria =
                    Criteria.builder()
                            .optApplication(Application.NLP.WORD_EMBEDDING)
                            .setTypes(NDList.class, NDList.class)
                            .optModelUrls(modelUrls)
                            .optEngine("MXNet")
                            .optProgress(new ProgressBar())
                            .build();

            try (Model m = Model.newInstance("AmazonReviewRatingClassification");
                 ZooModel<NDList, NDList> embedding = criteria.loadModel()) {
                DefaultVocabulary vocabulary =
                        DefaultVocabulary.builder()
                                .addFromTextFile(embedding.getArtifact("vocab.txt"))
                                .optUnknownToken("[UNK]")
                                .build();
                BertFullTokenizer tokenizer = new BertFullTokenizer(vocabulary, true);
                m.setBlock(getBlock(embedding.newPredictor()));

                Path modelPath = Paths.get("src/main/resources/trained");
                try (Model model = Model.newInstance("Mundi")) {
                    model.setBlock(getBlock(embedding.newPredictor()));
                    model.load(modelPath, "amazon-review.param");

                    try (Predictor<String, Classifications> predictor = model.newPredictor(new MyTranslator(tokenizer))) {
                        Classifications classifications = predictor.predict(request.getContent().toLowerCase());

                        String best = classifications.best().getClassName();
                        int number;
                        try {
                            number = Integer.parseInt(best);
                            review.setPredictedRating(number);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        review.setRatingProbability(classifications.best().getProbability() * 100d);
                        sentimentAnalysis(request, product, review, classifications);

                    }

                }
            }
            reviewRepository.save(review);
        }

    }

    private void sentimentAnalysis(CreateReviewRequest request, Product product, Review review, Classifications classifications) throws TranslateException, ModelNotFoundException, MalformedModelException, IOException {

        Criteria<String, Classifications> criteria =
                Criteria.builder()
                        .optApplication(Application.NLP.SENTIMENT_ANALYSIS)
                        .setTypes(String.class, Classifications.class)
                        .optEngine("PyTorch")
                        .optDevice(Device.cpu())
                        .optProgress(new ProgressBar())
                        .build();

        try (ZooModel<String, Classifications> model = criteria.loadModel();
            Predictor<String, Classifications> predictor = model.newPredictor(new Translator<String, Classifications>() {
                private Vocabulary vocabulary;
                private BertTokenizer tokenizer;

                @Override
                public void prepare(TranslatorContext ctx) throws IOException {
                    Model model = ctx.getModel();
                    URL url = model.getArtifact("distilbert-base-uncased-finetuned-sst-2-english-vocab.txt");
                    vocabulary =
                            DefaultVocabulary.builder().addFromTextFile(url).optUnknownToken("[UNK]").build();
                    tokenizer = new BertTokenizer();
                }

                @Override
                public Classifications processOutput(TranslatorContext ctx, NDList list) {
                    NDArray raw = list.singletonOrThrow();
                    NDArray computed = raw.exp().div(raw.exp().sum(new int[]{0}, true));
                    return new Classifications(Arrays.asList("Negative", "Positive"), computed);
                }

                @Override
                public NDList processInput(TranslatorContext ctx, String input) {
                    List<String> tokens = tokenizer.tokenize(input);
                    long[] indices = tokens.stream().mapToLong(vocabulary::getIndex).toArray();
                    long[] attentionMask = new long[tokens.size()];
                    Arrays.fill(attentionMask, 1);
                    NDManager manager = ctx.getNDManager();
                    NDArray indicesArray = manager.create(indices);
                    NDArray attentionMaskArray = manager.create(attentionMask);
                    return new NDList(indicesArray, attentionMaskArray);
                }
            })) {

            Classifications newClassifications = predictor.predict(request.getIntent().toLowerCase());//title
            bindThem(request, product, review, classifications, newClassifications);

        }

    }

    private void bindThem(CreateReviewRequest request, Product product, Review review, Classifications classifications, Classifications newClassifications) {

        Map<String, Serializable> binder = new TreeMap<>();
        binder.put("comment", request.getContent());
        binder.put("rating_int", classifications.best().getClassName());
        binder.put("category", product.getCategoryName());
        binder.put("product", product.getName());
        binder.put("sentiment_analysis" , newClassifications.toJson());
        binder.put("review_rating_classification", classifications.toString());
        review.setMultiTypeReviewMetadata(binder);
        log.info("Binder : {}", binder);
    }

    public List<ReviewResponse> retrieveReviews(String rating){
        log.info("Retrieving reviews");
        SearchSession searchSession = Search.session(entityManager);

        List<Review> hits = searchSession.search(Review.class)
                .where(f -> f.bool(b -> {
                    b.must(f.match().field( "multiTypeReviewMetadata.rating_int")
                            .matching(rating));
                    b.must(f.range().field( "rating_probability")
                            .between( 83D, 100D));
                }))
                //.sort( f -> f.field( "multiTypeReviewMetadata.rating_int" ).desc() )
                .fetchHits( 100);

        return reviewMapper.entitiesToEntityDTOs(hits);
    }


    private static class MyTranslator implements Translator<String, Classifications> {

        private final BertFullTokenizer tokenizer;
        private final Vocabulary vocab;
        private final List<String> ranks;

        public MyTranslator(BertFullTokenizer tokenizer) {
            this.tokenizer = tokenizer;
            vocab = tokenizer.getVocabulary();
            ranks = Arrays.asList("1", "2", "3", "4", "5");
        }

        @Override
        public Batchifier getBatchifier() { return Batchifier.STACK; }

        @Override
        public NDList processInput(TranslatorContext ctx, String input) {
            List<String> tokens = tokenizer.tokenize(input);
            float[] indices = new float[tokens.size() + 2];
            indices[0] = vocab.getIndex("[CLS]");
            for (int i = 0; i < tokens.size(); i++) {
                indices[i+1] = vocab.getIndex(tokens.get(i));
            }
            indices[indices.length - 1] = vocab.getIndex("[SEP]");
            return new NDList(ctx.getNDManager().create(indices));
        }

        @Override
        public Classifications processOutput(TranslatorContext ctx, NDList list) {
            return new Classifications(ranks, list.singletonOrThrow().softmax(0));
        }
    }

    private static Block getBlock(Predictor<NDList, NDList> embedder) {
        return new SequentialBlock()
                // text embedding layer
                .add(addFreezeLayer(embedder))
                // Classification layers
                .add(Linear.builder().setUnits(768).build()) // pre classifier
                .add(Activation::relu)
                .add(Dropout.builder().optRate(0.2f).build())
                .add(Linear.builder().setUnits(5).build()) // 5 star rating
                .addSingleton(nd -> nd.get(":,0")); // follow HF classifier
    }

    private static Block addFreezeLayer(Predictor<NDList, NDList> embedder) {
        if ("PyTorch".equals(Engine.getDefaultEngineName())) {
            return new LambdaBlock(
                    ndList -> {
                        NDArray data = ndList.singletonOrThrow();
                        try {
                            return embedder.predict(
                                    new NDList(
                                            data.toType(DataType.INT64, false),
                                            data.getManager()
                                                    .full(data.getShape(), 1, DataType.INT64),
                                            data.getManager()
                                                    .arange(data.getShape().get(1))
                                                    .toType(DataType.INT64, false)
                                                    .broadcast(data.getShape())));
                        } catch (TranslateException e) {
                            throw new IllegalArgumentException("embedding error", e);
                        }
                    });
        } else {
            // MXNet
            return new LambdaBlock(
                    ndList -> {
                        NDArray data = ndList.singletonOrThrow();
                        long batchSize = data.getShape().get(0);
                        float maxLength = data.getShape().get(1);
                        try {
                            return embedder.predict(
                                    new NDList(
                                            data,
                                            data.getManager()
                                                    .full(new Shape(batchSize), maxLength)));
                        } catch (TranslateException e) {
                            throw new IllegalArgumentException("embedding error", e);
                        }
                    });
        }
    }



    public Set<ProductResponse> findMatches(
            //CategoryEnum categoryEnum
    ) {
        List<Review> reviews = reviewRepository.findAll();

        User currentUser = userService.getUser(userService.getCurrentUser().getId());
        long currentUserId = currentUser.getId();
        double myTasteAverage;
        double cosineSim;
        double cosineDist;

        List<Review> reviewsByUserId = reviewRepository.findReviewsByUser_Id(currentUserId);
        List<Product> products = productRepository.findAll();

        myTasteAverage = reviewsByUserId
                .stream()
                .mapToDouble(Review::getRating)
                .average().orElse(Double.NaN);
        log.info(String.valueOf(myTasteAverage));
        log.info(String.valueOf(reviewsByUserId));

        Map<Long, Map<Long, Double>> trifecta = new HashMap<>();
        Map<Long, Double> sumOfValues = new HashMap<>();
        Map<Long, Map<Long, Double>> myTaste = new HashMap<>();
        Map<Long, String> userIds = new HashMap<>();
        Map<Long, List<Double>> customerReviews = new HashMap<>();
        Map<Long, Double> predictions = new HashMap<>();

        Map<Long, Double> cosineSimilarity = new HashMap<>();
        Map<Long, Double> cosineDistance = new HashMap<>();

        int size = reviews.size();
        for(int i = 0; i < size ; i++) {
            Review review = reviews.get(i);

            sumOfValues.merge(review.getUser().getId(), (double)review.getRating(), Double::sum);

            trifecta.computeIfAbsent(review.getUser().getId(), k -> new HashMap<>())
                    .put(review.getProduct().getId(), (double) review.getRating());

            myTaste.computeIfAbsent(currentUserId, k -> new HashMap<>())
                    .put(review.getProduct().getId(), (double) review.getRating());

            if (trifecta.containsKey(review.getUser().getId())) {
                userIds.put(review.getUser().getId(), review.getUser().getId().toString());
            }


        }

        sumOfValues.entrySet().parallelStream()
                .forEach(entry ->
                        entry.setValue(entry.getValue() / trifecta.get(entry.getKey()).size()));

        Iterator<Map.Entry<Long, Map<Long, Double>>> iterator = trifecta.entrySet().iterator();
        while (iterator.hasNext()) {

            Map.Entry<Long, Map<Long, Double>> trifectaMapEntry = iterator.next();

            String values = trifectaMapEntry.getValue().toString();
            ArrayList<Long> result = userIds.entrySet().stream()
                    .filter(entry -> values.contains(entry.getValue()))
                    //.sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(ArrayList::new));

            AtomicReference<Double> a = new AtomicReference<>((double) 0);
            AtomicReference<Double> b = new AtomicReference<>((double) 0);
            AtomicReference<Double> c = new AtomicReference<>((double) 0);

            result.parallelStream()
                    .forEach(productId -> {

                        Map<Long, Double> map = myTaste.get(currentUserId);
                        Double rating = map.get(productId);
                        if (rating != null) {

                            Map<Long, Double> innerMap = trifecta.get(trifectaMapEntry.getKey());
                            Double value = innerMap.get(productId);
                            if (value != null) {

                                customerReviews.computeIfAbsent(trifectaMapEntry.getKey(), k -> new ArrayList<>()) .add(value);
                                a.updateAndGet(v1 -> v1 + rating * value);
                                b.updateAndGet(v1 -> v1 + Math.pow(rating, 2));
                                c.updateAndGet(v1 -> v1 + Math.pow(value, 2));

                            }

                        }
                    });


            cosineSim = a.get() / (Math.sqrt(b.get()) * Math.sqrt(c.get()));
            cosineDist = 1.0 - cosineSim;

            cosineSimilarity.put(trifectaMapEntry.getKey(), cosineSim);
            cosineDistance.put(trifectaMapEntry.getKey(), cosineDist);

        }

        findMedian(customerReviews);

        recommendProducts(products, trifecta, sumOfValues, predictions, cosineDistance);

        Map<Long, Double> sortedPredictions = sortByValueReversed(predictions);
        Map<Long, Double> limited = sortByValueReverseOrder(predictions);
        Map<Long, Double> sortedAscending = sortByValue(predictions);
        log.info("Sorted predictions " + sortedPredictions );
        log.info("Sorted .asc " + sortedAscending );
        log.info(String.valueOf(limited));

        Set<Long> idSet = new LinkedHashSet<>(sortedPredictions.keySet());
        Set<Product> response = new LinkedHashSet<>();

        for (Long productId : idSet) {
            Product product = productService.getProduct(productId);
            if(product.getId().equals(productId)){
                response.add(product);
            }
        }

        return productMapper.entitiesToDTOs(response);
    }

    private void findMedian(Map<Long, List<Double>> customerReviews) {
        for(Map.Entry<Long, List<Double>> entry : customerReviews.entrySet()){

            MedianOfDoubleStream medianOfDoubleStream = new MedianOfDoubleStream();
            Map<Long, Double> median = new HashMap<>();

            List<Double> listOfValues = entry.getValue();

            DoubleStream doubleStream = listOfValues
                    .stream()
                    .mapToDouble(Double::doubleValue)
                    .sorted();

            double otherMedian = listOfValues.size() % 2 == 0 ?
                    doubleStream
                            .skip(listOfValues.size() / 2L - 1)
                            .limit(2)
                            .average()
                            .orElse(Double.NaN):
                    doubleStream
                            .skip(listOfValues.size() / 2)
                            .findFirst()
                            .orElse(Double.NaN);

            for (Double d : listOfValues){
                medianOfDoubleStream.add(d);
            }

            median.put(entry.getKey(), medianOfDoubleStream.getMedian());
            log.debug(String.valueOf(otherMedian));
            log.debug(String.valueOf(median));
        }
    }

    private void recommendProducts(List<Product> products,
                           Map<Long, Map<Long, Double>> trifecta,
                           Map<Long, Double> sumOfValues,
                           Map<Long, Double> predictions,
                           Map<Long, Double> cosineDistance) {
        int s = products.size();
        for(int j = 0; j < s ; j++) {
            Product product = products.get(j);


            AtomicReference<Double> numerator = new AtomicReference<>((double) 0);
            AtomicReference<Double> denominator = new AtomicReference<>((double) 0);

            Iterator<Map.Entry<Long, Double>> iter = cosineDistance.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Long, Double> entry = iter.next();

                if (trifecta.get(entry.getKey()).containsKey(product.getId())) {
                    double values = cosineDistance.get(entry.getKey());

                    Map<Long, Double> innerMap = trifecta.get(entry.getKey());
                    Double rating = innerMap.get(product.getId());
                    if(rating != null) {

                        numerator.updateAndGet(v1 -> v1 + values * (rating - sumOfValues.get(entry.getKey())));
                        denominator.updateAndGet(v1 -> v1 + cosineDistance.get(entry.getKey()));
                    }

                    if (denominator.get() > 0) {
                        double prediction;
                        prediction = sumOfValues.get(entry.getKey()) + numerator.get() / denominator.get();
                        predictions.put(product.getId(), prediction);
                    }

                }

            }

        }
    }

    private Map<Long, Double> sortByValue(Map<Long, Double> map) {

        return map.entrySet()
                .stream()
                //.sorted((i1, i2)
                //        -> i1.getValue().compareTo(
                //        i2.getValue()))

                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap
                        (Map.Entry::getKey, Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue,
                                LinkedHashMap::new));
    }

    private Map<Long, Double> sortByValueReversed(Map<Long, Double> map) {

        return map.entrySet()
                .stream()

                .sorted(Map.Entry.<Long, Double> comparingByValue().reversed())
                .collect(Collectors.toMap
                        (Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
    }

    private Map<Long, Double> sortByValueReverseOrder(Map<Long, Double> map) {
        int elementsToReturn = 5;

        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(elementsToReturn)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));

    }

    public ReviewResponse getReviewId(Long id) {
        log.info("Retrieving review {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));

        return reviewMapper.mapToReviewResponse(review);
    }

    public Review getReview(long id){
        log.info("Retrieving review {}", id);
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review" + id + "not found"));
    }

    public void deleteReview(long id){
        log.info("Deleting review {}", id);
        reviewRepository.deleteById(id);
    }

    public Page<ReviewResponse> getReviews(GetReviewsRequest request, Pageable pageable){
        log.info("Retrieving reviews: {}", request);
        Page<Review> reviews = null;

        if (request != null && request.getProductId() != null &&
                request.getRating() != null) {
            reviews = reviewRepository.findReviewsByProductIdAndRatingOrderByIdDesc
                    (request.getProductId(), request.getRating(), pageable);

        } else if (request != null && request.getProductId() != null) {
            reviews = reviewRepository.findReviewsByProductId
                    (request.getProductId(), pageable);
        }

        assert reviews != null;
        List<ReviewResponse> reviewResponses = reviewMapper.entitiesToEntityDTOs(reviews.getContent());
        return new PageImpl<>(reviewResponses, pageable, reviews.getTotalElements());
    }



}

class MedianOfDoubleStream {

    private final Queue<Double> minHeap;
    private final Queue<Double> maxHeap;

    MedianOfDoubleStream() {
        minHeap = new PriorityQueue<>();
        maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
    }

    void add(double num) {
        if (!minHeap.isEmpty() && num < minHeap.peek()) {
            maxHeap.offer(num);
            if (maxHeap.size() > minHeap.size() + 1) {
                minHeap.offer(maxHeap.poll());
            }
        } else {
            minHeap.offer(num);
            if (minHeap.size() > maxHeap.size() + 1) {
                maxHeap.offer(minHeap.poll());
            }
        }
    }

    double getMedian() {
        double median;
        if (minHeap.size() < maxHeap.size()) {
            median = maxHeap.peek();
        } else if (minHeap.size() > maxHeap.size()) {
            median = minHeap.peek();
        } else {
            median = (minHeap.peek() + maxHeap.peek()) / 2;
        }
        return median;
    }
}





