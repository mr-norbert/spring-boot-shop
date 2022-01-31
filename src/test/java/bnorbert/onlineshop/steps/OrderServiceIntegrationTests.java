package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.domain.OrderTypeEnum;
import bnorbert.onlineshop.service.OrderService;
import bnorbert.onlineshop.transfer.order.OrderRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceIntegrationTests {
    @Autowired
    private OrderSteps orderSteps;

    @Autowired
    private OrderService orderService;

    @Test
    public void firstStep() {
        orderSteps.create();
    }

    @Test
    public void testGetGodViewOverCarts_whenValidRequest_thenReturnCartsForAdmin(){
        orderSteps.getViewOverCarts();
    }

    @Test
    public void testGetViewOverCarts_whenValidRequest_thenReturnCartsForAdmin(){
        String categoryName = "category_field";
        double lowerBound = 1;
        double upperBound = 2700;
        orderService.getGodViewOverCarts(categoryName, lowerBound, upperBound);
    }

    @Test
    public void testCreateOrder_whenValidRequest_thenReturnCreatedOrder() {
        orderSteps.createOrder();
    }

    @Test
    public void testGetOrders_paramCity_whenExistingEntities_thenReturnOrders() {
        OrderRequest request = new OrderRequest();
        String query = "London";
        int pageNumber = 0;
        orderService.getOrders(request, query, Pageable.unpaged(), OrderTypeEnum.TEST, pageNumber);
    }

    @Test
    public void testGetOrders_paramId_whenExistingEntities_thenReturnOrders() {
        OrderRequest request = new OrderRequest();
        String query = "1";
        int pageNumber = 0;
        orderService.getOrders(request, query, Pageable.unpaged(), OrderTypeEnum.TEST, pageNumber) ;
    }

    @Test
    public void testGetOrders_paramCategoryName_whenExistingEntities_thenReturnOrders() {
        OrderRequest request = new OrderRequest();
        String query = "category_field";
        int pageNumber = 0;
        orderService.getOrders(request, query, Pageable.unpaged(), OrderTypeEnum.TEST, pageNumber) ;
    }

    @Test
    public void testGetOrders_paramBrandName_whenExistingEntities_thenReturnOrderId1() {
        OrderRequest request = new OrderRequest();
        String query = "string2";
        int pageNumber = 0;
        orderService.getOrders(request, query, Pageable.unpaged(), OrderTypeEnum.TEST, pageNumber);
    }

    @Test
    public void testGetOrders_paramPageNumber_thenThrowNotFoundException (){
        OrderRequest request = new OrderRequest();
        String query = "London";
        int pageNumber = 992;
        orderService.getOrders(request, query, Pageable.unpaged(), OrderTypeEnum.TEST, pageNumber);
    }

    @Test
    public void testGetOrders_localDateTimeBetween_thenReturnOrders(){
        orderSteps.getOrdersValidRequest();
    }

    @Test
    public void testGetOrders_localDateTimeBetween_thenReturnAssertionFailedError(){
        orderSteps.getOrdersNotValidRequest();
    }

}
