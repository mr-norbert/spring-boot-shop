package bnorbert.onlineshop.service;

import ai.djl.Application;
import ai.djl.MalformedModelException;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import bnorbert.onlineshop.domain.Image;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@Transactional
public class ImageService implements FileStorageService{

    private final ImageRepository imageRepository;
    private final EntityManager entityManager;
    private final ProductService productService;

    private static final String PATH_FIELD_NAME = "words.name";
    private static final String PATH_FIELD_CATEGORY = "words.category";
    private static final String PATH_FIELD_BRAND = "words.brand";
    private static final String PATH_FIELD_DETECTION = "words.detection";

    private final String[] searchFields = new String[]{
            PATH_FIELD_NAME, PATH_FIELD_CATEGORY, PATH_FIELD_BRAND, PATH_FIELD_DETECTION
    };

    public ImageService(ImageRepository imageRepository, EntityManager entityManager, ProductService productService) {
        this.imageRepository = imageRepository;
        this.entityManager = entityManager;
        this.productService = productService;
    }

    public void getImages(String query){
        SearchSession searchSession = org.hibernate.search.mapper.orm.Search.session(entityManager);
        SearchResult<Image> hits = searchSession.search(Image.class)
                .where(f -> f.match()
                        .fields(searchFields)
                        .matching(query))
                .fetch(20);
        List<Image> result = hits.hits();

        log.debug(String.valueOf(result.stream()
                .map(Image::getId)
                //.distinct()
                .collect(Collectors.toList())));
    }

    private final Path rootLocation = Paths.get("src/main/resources/images");

    private boolean imageExists(long id, byte[] bytes){
        return imageRepository.findByProductIdAndPhoto(id, bytes).isPresent();
    }

    public void createImage(MultipartFile file, long productId) {
        log.info("Creating image");
        Image image = new Image();
        try {
            image.setPhoto(file.getBytes());
        } catch (IOException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
        image.setOriginalFilename(file.getOriginalFilename());
        image.setSize(file.getSize());
        image.setCreatedDate(LocalDateTime.now());
        try {
            if (imageExists(productId, file.getBytes())) {
                throw new ResourceNotFoundException("Image is present");
            }
        } catch (IOException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
        Product product = productService.getProduct(productId);
        image.setProduct(product);

        try {
            InputStream inputStream = new ByteArrayInputStream(file.getBytes());
            //BufferedImage bufferedImage = ImageIO.read(inputStream);
            //ai.djl.modality.cv.Image img = ImageFactory.getInstance().fromImage(bufferedImage);
            ImageFactory factory = ImageFactory.getInstance();
            ai.djl.modality.cv.Image img = factory.fromInputStream(inputStream);

            Criteria<ai.djl.modality.cv.Image, DetectedObjects> criteria =
                    Criteria.builder()
                            .optApplication(Application.CV.OBJECT_DETECTION)
                            .setTypes(ai.djl.modality.cv.Image.class, DetectedObjects.class)
                            //.optModelUrls(modelUrl)
                            //.optModelName("ssd_mobilenet_v2_320x320_coco17_tpu-8/saved_model")
                            //.optTranslator(new MyTranslator())
                            .optFilter("backbone", "mobilenet_v2")
                            .optEngine("TensorFlow")
                            .optProgress(new ProgressBar())
                            .build();

            try (ZooModel<ai.djl.modality.cv.Image, DetectedObjects> model = criteria.loadModel();
                 Predictor<ai.djl.modality.cv.Image, DetectedObjects> predictor = model.newPredictor()) {

                DetectedObjects detection = predictor.predict(img);
                //log.info(detection.best().getClassName().toLowerCase());
                //log.info(String.valueOf(detection.best().getProbability()));
                Map<String, String> words = new TreeMap<>();
                words.put("name", product.getName());
                words.put("category", product.getCategoryName());
                words.put("brand", product.getBrandName());
                if(detection.best().getClassName() != null) {
                    words.put("detection", detection.best().getClassName().toLowerCase());
                }
                image.setWords(words);
                log.info("Binder: {}", words);
                saveBoundingBoxImage(img, detection);
            }
        } catch (IOException | TranslateException | ModelNotFoundException | MalformedModelException | NoSuchElementException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }

        imageRepository.save(image);
    }

    private static void saveBoundingBoxImage(ai.djl.modality.cv.Image img, DetectedObjects detection) throws IOException {
        Path outputDir = Paths.get("build/output");
        Files.createDirectories(outputDir);
        img.drawBoundingBoxes(detection);
        Path imagePath = outputDir.resolve("detected-object.png");
        img.save(Files.newOutputStream(imagePath), "png");
        log.info("Detected objects image has been saved in: {}", imagePath);
    }

    public List<String> opticalCharacterRecognition(MultipartFile file) {
        log.info("Detecting words");
        List<String> words = new ArrayList<>();

        try {
            InputStream inputStream = new ByteArrayInputStream(file.getBytes());
            //BufferedImage bufferedImage = ImageIO.read(inputStream);
            //ai.djl.modality.cv.Image img = ImageFactory.getInstance().fromImage(bufferedImage);
            ImageFactory factory = ImageFactory.getInstance();
            ai.djl.modality.cv.Image img = factory.fromInputStream(inputStream);

            List<DetectedObjects.DetectedObject> boxes = detectWords(img).items();
            Predictor<ai.djl.modality.cv.Image, String> recognizer = getRecognizer();
            Predictor<ai.djl.modality.cv.Image, Classifications> rotator = getRotateClassifier();

            for (DetectedObjects.DetectedObject box : boxes) {
                ai.djl.modality.cv.Image subImg = getSubImage(img, box.getBoundingBox());
                if (subImg.getHeight() * 1.0 / subImg.getWidth() > 1.5) {
                    subImg = rotateImg(subImg);
                }
                Classifications.Classification result = rotator.predict(subImg).best();
                if ("Rotate".equals(result.getClassName()) && result.getProbability() > 0.8) {
                    subImg = rotateImg(subImg);
                }
                String word = recognizer.predict(subImg);
                detectRealNames(word);
                words.add(word);
            }

        } catch (ModelException | TranslateException | IOException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }

        return words;
    }

    private DetectedObjects detectWords(ai.djl.modality.cv.Image img)
            throws ModelException, IOException, TranslateException {
        Criteria<ai.djl.modality.cv.Image, DetectedObjects> criteria =
                Criteria.builder()
                        .setTypes(ai.djl.modality.cv.Image.class, DetectedObjects.class)
                        .optArtifactId("ai.djl.paddlepaddle:word_detection")
                        .optFilter("flavor", "mobile")
                        .build();
        try (ZooModel<ai.djl.modality.cv.Image, DetectedObjects> model = criteria.loadModel();
             Predictor<ai.djl.modality.cv.Image, DetectedObjects> predictor = model.newPredictor()) {
            return predictor.predict(img);
        }
    }

    private Predictor<ai.djl.modality.cv.Image, String> getRecognizer()
            throws MalformedModelException, ModelNotFoundException, IOException {
        Criteria<ai.djl.modality.cv.Image, String> criteria =
                Criteria.builder()
                        .setTypes(ai.djl.modality.cv.Image.class, String.class)
                        .optArtifactId("ai.djl.paddlepaddle:word_recognition")
                        .optFilter("flavor", "mobile")
                        .build();

        ZooModel<ai.djl.modality.cv.Image, String> model = criteria.loadModel();
        return model.newPredictor();
    }

    private Predictor<ai.djl.modality.cv.Image, Classifications> getRotateClassifier()
            throws MalformedModelException, ModelNotFoundException, IOException {
        Criteria<ai.djl.modality.cv.Image, Classifications> criteria =
                Criteria.builder()
                        .setTypes(ai.djl.modality.cv.Image.class, Classifications.class)
                        .optArtifactId("ai.djl.paddlepaddle:word_rotation")
                        .optFilter("flavor", "mobile")
                        .build();
        ZooModel<ai.djl.modality.cv.Image, Classifications> model = criteria.loadModel();
        return model.newPredictor();
    }

    private ai.djl.modality.cv.Image rotateImg(ai.djl.modality.cv.Image image) {
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray rotated = NDImageUtils.rotate90(image.toNDArray(manager), 1);
            return ImageFactory.getInstance().fromNDArray(rotated);
        }
    }

    private ai.djl.modality.cv.Image getSubImage(ai.djl.modality.cv.Image img, BoundingBox box) {
        Rectangle rect = box.getBounds();
        double[] extended = extendRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        int width = img.getWidth();
        int height = img.getHeight();
        int[] recovered = {
                (int) (extended[0] * width),
                (int) (extended[1] * height),
                (int) (extended[2] * width),
                (int) (extended[3] * height)
        };
        return img.getSubImage(recovered[0], recovered[1], recovered[2], recovered[3]);
    }

    private double[] extendRect(double xmin, double ymin, double width, double height) {
        double centerx = xmin + width / 2;
        double centery = ymin + height / 2;
        if (width > height) {
            width += height * 2.0;
            height *= 3.0;
        } else {
            height += width * 2.0;
            width *= 3.0;
        }
        double newX = centerx - width / 2 < 0 ? 0 : centerx - width / 2;
        double newY = centery - height / 2 < 0 ? 0 : centery - height / 2;
        double newWidth = newX + width > 1 ? 1 - newX : width;
        double newHeight = newY + height > 1 ? 1 - newY : height;
        return new double[] {newX, newY, newWidth, newHeight};
    }


    private void detectRealNames(String name) {
        File initialFile = new File("src/main/resources/en-ner-person.bin");
        try(InputStream modelIn = new FileInputStream(initialFile)){
            List<String> names = new ArrayList<>();
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn) ;
            NameFinderME nameFinder = new NameFinderME(model);

            SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
            String[] tokens = tokenizer.tokenize(name);
            log.info(Arrays.toString(tokens));

            Span[] namSpans = nameFinder.find(tokens);
            for (Span span : namSpans) {
                StringBuilder builder = new StringBuilder();
                for (int i = span.getStart(); i < span.getEnd(); i++) {
                    builder.append(tokens[i]).append("+");
                }
                names.add(builder.toString());
            }
            log.info("Pearson : " + names);

        } catch (IOException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public void copy(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new ResourceNotFoundException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(Objects.requireNonNull(file.getOriginalFilename())))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {

                throw new ResourceNotFoundException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new ResourceNotFoundException("Failed to store file.");
        }
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new ResourceNotFoundException("Could not initialize folder for upload! " + e.getMessage());
        }
    }
    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new ResourceNotFoundException("Could not load the files!" + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public Resource load(String name) {
        log.info("Retrieving image: {}", name);
        try {
            Path file = rootLocation.resolve(name);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Error: " + e.getMessage());
        }
    }

    public byte[] loadImage(long imageId) {
        log.info("Retrieving image {}", imageId);

        Optional<Image> image = imageRepository.findById(imageId);
        byte[] imageBytes = null;
        if (image.isPresent()) {
            imageBytes = image.get().getPhoto();
        }
        return imageBytes;
    }

    public Image getImageId(long id){
        log.info("Retrieving image {}", id);
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image" + id + "not found"));
    }

}
