package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.Order;
import bnorbert.onlineshop.service.OrderService;
import bnorbert.onlineshop.transfer.order.OrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class OrderControllerTest {

    @Mock
    private OrderService mockOrderService;

    private OrderController orderControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        orderControllerUnderTest = new OrderController(mockOrderService);
    }

    @Test
    void testCreateOrder() {

        final OrderDto request = new OrderDto();
        request.setAddressId(1L);

        final ResponseEntity<Order> result = orderControllerUnderTest.createOrder(request);

        verify(mockOrderService).createOrder(any(OrderDto.class));
    }
}
