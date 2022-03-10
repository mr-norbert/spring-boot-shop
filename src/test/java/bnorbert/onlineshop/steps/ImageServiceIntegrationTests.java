package bnorbert.onlineshop.steps;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import bnorbert.onlineshop.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ImageServiceIntegrationTests {

    @Autowired
    private ImageSteps imageSteps;

    ImageSteps service = mock(ImageSteps.class);
    ImageService imageService = mock(ImageService.class);

    @Test
    public void testCreateImage() throws IOException, TranslateException, ModelNotFoundException, MalformedModelException {
        service.createImage();
        imageSteps.createImage();
        verify(service, times(1)).createImage();
    }

    @Test
    public void testOCR() {
        List<String> response = imageSteps.opticalCharacterRecognition();
        assertThat(response).isNotNull();
        assertThat(response.size()).isPositive();
        log.info(String.valueOf(response));
    }

    @Test
    public void testNamesDetection() {
        String name = "John Doe";
        imageSteps.detectRealNames(name);
        service.detectRealNames(name);
        verify(service, times(1)).detectRealNames(name);
    }

    @Test
    public void testSearch() {
        imageService.getImages("car");
        verify(imageService, times(1)).getImages("car");
    }

}
