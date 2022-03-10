package bnorbert.onlineshop.steps;

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
import bnorbert.onlineshop.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Component
@Slf4j
@Transactional
public class ImageSteps {

    @Autowired
    private ProductService productService;
    @Autowired
    private ImageRepository imageRepository;


    public void createImage() throws IOException, TranslateException, ModelNotFoundException, MalformedModelException {

        //Path path = Paths.get("src/main/resources/your-image.jpeg");
        //ai.djl.modality.cv.Image img = ImageFactory.getInstance().fromFile(path);

        File initialFile = new File("src/main/resources/your-image.jpeg");
        try(InputStream modelIn = new FileInputStream(initialFile)) {
            ImageFactory factory = ImageFactory.getInstance();
            ai.djl.modality.cv.Image img = factory.fromInputStream(modelIn);
            //BufferedImage bufferedImage = ImageIO.read(modelIn);
            //ai.djl.modality.cv.Image img = ImageFactory.getInstance().fromImage(bufferedImage);

            MultipartFile mockFile = new MockMultipartFile("test", "test", MediaType.ALL_VALUE, "test".getBytes());
            Image image = new Image();
            image.setPhoto(mockFile.getBytes());
            image.setOriginalFilename(mockFile.getOriginalFilename());
            image.setCreatedDate(LocalDateTime.now());

            Product product = productService.getProduct(1L);
            image.setProduct(product);

            Criteria<ai.djl.modality.cv.Image, DetectedObjects> criteria =
                    Criteria.builder()
                            .optApplication(Application.CV.OBJECT_DETECTION)
                            .setTypes(ai.djl.modality.cv.Image.class, DetectedObjects.class)
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
            assertThat(product).isNotNull();
            assertThat(mockFile).isNotNull();
        }

    }

    private static void saveBoundingBoxImage(ai.djl.modality.cv.Image img, DetectedObjects detection) throws IOException {
        Path outputDir = Paths.get("build/output");
        Files.createDirectories(outputDir);
        img.drawBoundingBoxes(detection);
        Path imagePath = outputDir.resolve("detected-object.png");
        img.save(Files.newOutputStream(imagePath), "png");
        log.info("Detected objects image has been saved in: {}", imagePath);
    }


    public List<String> opticalCharacterRecognition() {
        log.info("Detecting words");
        List<String> names = new ArrayList<>();

        File initialFile = new File("src/main/resources/your-image.jpeg");
        try(InputStream modelIn = new FileInputStream(initialFile)) {
            ImageFactory factory = ImageFactory.getInstance();
            ai.djl.modality.cv.Image img = factory.fromInputStream(modelIn);

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
                String name = recognizer.predict(subImg);
                names.add(name);
            }

        } catch (ModelException | TranslateException | IOException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }

        return names;
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

    public void detectRealNames(String name) {
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


}
