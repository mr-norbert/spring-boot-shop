package bnorbert.onlineshop.steps;

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
import bnorbert.onlineshop.repository.ImageRepository;
import bnorbert.onlineshop.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

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
            BufferedImage bufferedImage = ImageIO.read(modelIn);
            ai.djl.modality.cv.Image img = ImageFactory.getInstance().fromImage(bufferedImage);

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


}
