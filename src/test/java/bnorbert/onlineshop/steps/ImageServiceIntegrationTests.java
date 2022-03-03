package bnorbert.onlineshop.steps;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ImageServiceIntegrationTests {

    @Autowired
    private ImageSteps imageSteps;

    ImageSteps service = mock(ImageSteps.class);

    @Test
    public void testCreateImage() throws IOException, TranslateException, ModelNotFoundException, MalformedModelException {
        service.createImage();
        imageSteps.createImage();
        verify(service, times(1)).createImage();
    }

}
