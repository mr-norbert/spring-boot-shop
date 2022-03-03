package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.domain.Cart;
import bnorbert.onlineshop.domain.Order;
import bnorbert.onlineshop.domain.OrderTypeEnum;
import bnorbert.onlineshop.service.OrderService;
import bnorbert.onlineshop.transfer.cart.CartResponse;
import bnorbert.onlineshop.transfer.order.OrderRequest;
import bnorbert.onlineshop.transfer.order.OrderResponse;
import bnorbert.onlineshop.transfer.order.OrdersResponses;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceIntegrationTests {
    @Autowired
    private OrderSteps orderSteps;

    @Autowired
    private OrderService orderService;

    @Test
    public void firstStep() {
        String test = orderSteps.create();
        assertThat(test).isEqualTo("done");
    }

    @Test
    public void testCreateOrder_whenValidRequest_thenReturnCreatedOrder() {
        Order order = orderSteps.createOrder();
        assertThat(order).isNotNull();
    }

    @Test
    public void testGetGodViewOverCarts_whenValidRequest_thenReturnCartsForAdmin() {
        List<Cart> hits = orderSteps.getViewOverCarts();
        assertThat(hits).isNotNull();
    }

    @Test
    public void testGetViewOverCarts_whenValidRequest_thenReturnCartsForAdmin() {
        String categoryName = "category_field";
        double lowerBound = 1;
        double upperBound = 2700;
        List<CartResponse> hits = orderService.getGodViewOverCarts(categoryName, lowerBound, upperBound);
        assertThat(hits.size()).isPositive();
        assertThat(hits).isNotEmpty();
    }

    @Test
    public void testGetOrders_paramCity_whenExistingEntities_thenReturnOrders() {
        OrderRequest request = new OrderRequest();
        String query = "London";
        int pageNumber = 0;
        OrdersResponses hits = orderService.getOrders(request, query, OrderTypeEnum.TEST, pageNumber);
        assertThat(hits.orderResponses.stream().map(OrderResponse::getId).collect(Collectors.toList())).isNotEmpty();
    }

    @Test
    public void testGetOrders_paramPlusParam_whenExistingEntities_thenReturnOrders() {
        OrderRequest request = new OrderRequest();
        String query = "London category_field";
        int pageNumber = 0;
        OrdersResponses hits = orderService.getOrders(request, query, OrderTypeEnum.TEST, pageNumber);
        assertThat(hits.orderResponses.stream().map(OrderResponse::getId).collect(Collectors.toList())).isNotEmpty();
        assertThat(hits.orderResponses.size()).isPositive();
    }

    @Test
    public void testGetOrders_paramId_whenExistingEntities_thenReturnOrders() {
        OrderRequest request = new OrderRequest();
        String query = "1";
        int pageNumber = 0;
        OrdersResponses hits = orderService.getOrders(request, query, OrderTypeEnum.TEST, pageNumber);
        assertThat(hits.orderResponses.stream().map(OrderResponse::getId).collect(Collectors.toList())).isNotEmpty();
    }

    @Test
    public void testGetOrders_paramCategoryName_whenExistingEntities_thenReturnOrders() {
        OrderRequest request = new OrderRequest();
        String query = "category_field";
        int pageNumber = 0;
        OrdersResponses hits = orderService.getOrders(request, query, OrderTypeEnum.TEST, pageNumber);
        assertThat(hits.orderResponses.stream().map(OrderResponse::getId).collect(Collectors.toList())).isNotEmpty();
        assertThat(hits.orderResponses.size()).isPositive();
    }

    @Test
    public void testGetOrders_paramBrandName_whenExistingEntities_thenReturnOrderId() {
        OrderRequest request = new OrderRequest();
        String query = "string2";
        int pageNumber = 0;
        OrdersResponses hits = orderService.getOrders(request, query, OrderTypeEnum.TEST, pageNumber);
        assertThat(hits.orderResponses.stream().map(OrderResponse::getId).collect(Collectors.toList())).isNotEmpty();
    }

    @Test
    public void testGetOrders_paramPageNumber_thenThrowNotFoundException() {
        OrderRequest request = new OrderRequest();
        String query = "London";
        int pageNumber = 992;
        OrdersResponses hits = orderService.getOrders(request, query, OrderTypeEnum.TEST, pageNumber);
        assertThat(hits.orderResponses.stream().map(OrderResponse::getId).collect(Collectors.toList())).isNotEmpty();
    }

    @Test
    public void testGetOrders_localDateTimeBetween_thenReturnOrders() {
        List<Order> hits = orderSteps.getOrdersValidRequest();
        assertThat(hits).isNotEmpty();
    }

    @Test
    public void testGetOrders_localDateTimeBetween_thenReturnAssertionFailedError() {
        List<Order> hits = orderSteps.getOrdersNotValidRequest();
        assertThat(hits).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "London", "string2", "category_field", "email@.com"})
    void testGetOrders(String query){
        OrderRequest request = new OrderRequest();
        int pageNumber = 0;
        OrdersResponses hits = orderService.getOrders(request, query, OrderTypeEnum.TEST, pageNumber);
        Assertions.assertNotNull(query);
        assertThat(hits.orderResponses.stream().map(OrderResponse::getId).collect(Collectors.toList())).isNotEmpty();

    }

}
