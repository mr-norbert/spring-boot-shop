package bnorbert.onlineshop.service;

import ai.djl.Application;
import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
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
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public void createImage(MultipartFile file, long productId) throws IOException, ModelNotFoundException, MalformedModelException, TranslateException {
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
        if (imageExists(productId, file.getBytes())) {
            throw new IllegalArgumentException("Image is present");
        }
        Product product = productService.getProduct(productId);
        image.setProduct(product);

        InputStream inputStream = new ByteArrayInputStream(file.getBytes());
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        ai.djl.modality.cv.Image img = ImageFactory.getInstance().fromImage(bufferedImage);

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
            log.info(detection.best().getClassName().toLowerCase());
            log.info(String.valueOf(detection.best().getProbability()));
            saveBoundingBoxImage(img, detection);

            Map<String, String> words = new TreeMap<>();
            words.put("name", product.getName());
            words.put("category", product.getCategoryName());
            words.put("brand", product.getBrandName());
            words.put("detection", detection.best().getClassName().toLowerCase());
            image.setWords(words);
            log.info("Binder: {}", words);
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
