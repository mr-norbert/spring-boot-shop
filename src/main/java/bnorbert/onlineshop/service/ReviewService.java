package bnorbert.onlineshop.service;

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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final UserService userService;
    private final ReviewMapper reviewMapper;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    public void createReview(CreateReviewRequest request){
        log.info("Creating review: {}", request);
        if (userService.isLoggedIn()) {
            Product product = productService.getProduct(request.getProductId());

            Optional<Review> productAndUser = reviewRepository
                    .findTopByProductAndUserOrderByIdDesc(product, userService.getCurrentUser());
            if (productAndUser.isPresent()) {
                throw new ResourceNotFoundException("You have already reviewed this product: " + request.getProductId());
            }
            reviewRepository.save(reviewMapper.map(request, product, userService.getCurrentUser()));
        }
    }



    public Set<ProductResponse> findMatches(
            //CategoryEnum categoryEnum
    ) {

        List<Review> reviews = reviewRepository.findAll();
        /*
        List<Review> reviews = reviewRepository.findReviewsByProductCategoryName(categoryEnum.name().toLowerCase());

        switch (categoryEnum) {
            case TEST:
                System.out.println("TEST");
                break;

            case _TEST:
                System.out.println("_TEST");
                break;

            case STRING:
                System.out.println("String");
                break;

            default:
                System.out.println("Default");
                break;
        }

         */

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
                //.sum();
        System.out.println(myTasteAverage);

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

            //sumOfValues.put(review.getUser().getId(),
            //                sumOfValues.getOrDefault(review.getUser().getId(), 0.0) + (double)review.getRating());

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
                        entry.setValue(entry.getValue() / (double) trifecta.get(entry.getKey()).size()));

        Iterator<Map.Entry<Long, Map<Long, Double>>> iterator = trifecta.entrySet().iterator();
        while (iterator.hasNext()) {

            Map.Entry<Long, Map<Long, Double>> _entry = iterator.next();

            String values = _entry.getValue().toString();
            ArrayList<Long> result = userIds.entrySet().stream()
                    .filter(entry -> values.contains(entry.getValue()))
                    //.sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(ArrayList::new));

            AtomicReference<Double> a = new AtomicReference<>((double) 0);
            AtomicReference<Double> b = new AtomicReference<>((double) 0);
            AtomicReference<Double> c = new AtomicReference<>((double) 0);

            result.parallelStream()
                    .forEach((productId) -> {

                        Map<Long, Double> map = myTaste.get(currentUserId);
                        Double _value = map.get(productId);
                        if (_value != null) {

                            Map<Long, Double> innerMap = trifecta.get(_entry.getKey());
                            Double value = innerMap.get(productId);
                            if (value != null) {

                                customerReviews.computeIfAbsent(_entry.getKey(), k -> new ArrayList<>()) .add(value);
                                a.updateAndGet(v1 -> v1 + _value * value);
                                b.updateAndGet(v1 -> v1 + Math.pow(_value, 2));
                                c.updateAndGet(v1 -> v1 + Math.pow(value, 2));

                            }

                        }
                    });


            cosineSim = a.get() / (Math.sqrt(b.get()) * Math.sqrt(c.get()));
            cosineDist = 1.0 - cosineSim;

            cosineSimilarity.put(_entry.getKey(), cosineSim);
            cosineDistance.put(_entry.getKey(), cosineDist);


        }


        for(Map.Entry<Long, List<Double>> entry : customerReviews.entrySet()){

            MedianOfDoubleStream medianOfDoubleStream = new MedianOfDoubleStream();
            Map<Long, Double> median = new HashMap<>();

            List<Double> listOfValues = entry.getValue();

            DoubleStream doubleStream = listOfValues
                    .stream()
                    .mapToDouble(Double::doubleValue)
                    .sorted();

            double _median = listOfValues.size() % 2 == 0 ?
                    doubleStream
                            .skip(listOfValues.size() / 2 - 1)
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

        }


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

        Map<Long, Double> sortedPredictions = sortByValueReversed(predictions);
        Map<Long, Double> limited = sortByValueReverseOrder(predictions);
        System.err.println(sortedPredictions + " ****");
        System.err.println(limited + " ****");

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

        //.forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

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

    private Queue<Double> minHeap, maxHeap;

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





