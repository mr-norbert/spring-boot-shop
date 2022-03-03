package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.service.PantryService;
import org.apache.mahout.cf.taste.common.TasteException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PantryServiceIntegrationTests {

    @Autowired
    private PantryService service;

    PantryService pantryService = mock(PantryService.class);

    @Test
    public void firstStep(){
        service.generateIds();
        pantryService.generateIds();
        verify(pantryService, times(1)).generateIds();
    }

    @Test
    public void testWipe(){
        service.deleteAll();
        pantryService.deleteAll();
        verify(pantryService, times(1)).deleteAll();
    }

    @Test
    public void testFindSimilarProducts() throws SQLException, TasteException {
        service.findSimilarProducts();
        pantryService.findSimilarProducts();
        verify(pantryService, times(1)).findSimilarProducts();
    }
}
