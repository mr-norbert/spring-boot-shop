package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.Order;
import bnorbert.onlineshop.service.OrderService;
import bnorbert.onlineshop.transfer.address.CreateAddressRequest;
import bnorbert.onlineshop.transfer.order.OrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.status;

@CrossOrigin
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/createOrder")
    public ResponseEntity<Order> createOrder(CreateAddressRequest request) {
        orderService.createOrder(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return status(HttpStatus.OK).body(orderService.getOrderId(id));
    }
}
