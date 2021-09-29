package bnorbert.onlineshop.steps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceIntegrationTests {
    @Autowired
    private OrderSteps orderSteps;

    @Test
    public void testCreateOrder_whenValidRequest_thenReturnCreatedOrder() {
        orderSteps.createOrder();
    }

}
