package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.Order;
import bnorbert.onlineshop.service.OrderService;
import bnorbert.onlineshop.transfer.address.CreateAddressRequest;
import bnorbert.onlineshop.transfer.order.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService mockOrderService;

    private OrderController orderControllerUnderTest;

    @BeforeEach
    void setUp() {
        orderControllerUnderTest = new OrderController(mockOrderService);
    }

    @Test
    void testCreateOrder() {

        CreateAddressRequest request = new CreateAddressRequest();
        request.setAddress("address");
        ResponseEntity<Order> result = orderControllerUnderTest.createOrder(request);

        verify(mockOrderService).createOrder(any(CreateAddressRequest.class));
    }

    @Test
    void testGetOrder() {

        when(mockOrderService.getOrderId(1L)).thenReturn(new OrderResponse());

        final ResponseEntity<OrderResponse> result = orderControllerUnderTest.getOrder(1L);

    }


}
