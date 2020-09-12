package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.Order;
import bnorbert.onlineshop.service.OrderService;
import bnorbert.onlineshop.transfer.order.OrderDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/createOrder")
    public ResponseEntity<Order> createOrder(@RequestBody @Valid OrderDto request) {
        orderService.createOrder(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
