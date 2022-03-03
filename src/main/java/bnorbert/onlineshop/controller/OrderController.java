package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.Order;
import bnorbert.onlineshop.domain.OrderTypeEnum;
import bnorbert.onlineshop.service.OrderService;
import bnorbert.onlineshop.transfer.address.CreateAddressRequest;
import bnorbert.onlineshop.transfer.cart.CartResponse;
import bnorbert.onlineshop.transfer.order.OrderRequest;
import bnorbert.onlineshop.transfer.order.OrderResponse;
import bnorbert.onlineshop.transfer.order.OrdersResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@CrossOrigin
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateAddressRequest request) {
        orderService.createOrder(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return status(HttpStatus.OK).body(orderService.getOrderId(id));
    }

    @GetMapping("/internals/perspective")
    public ResponseEntity<List<CartResponse>> getGodViewOverCarts(
            @RequestParam(value = "category", required = false) String categoryName,
            @RequestParam(value = "price-minimum", required = false) double lowerBound,
            @RequestParam(value = "price-maximum", required = false) double upperBound
    ) {
        return status(HttpStatus.OK).body(orderService.getGodViewOverCarts(categoryName, lowerBound, upperBound));
    }

    @GetMapping("/internals/search")
    public OrdersResponses getOrders(
            @RequestParam(value = "from-year", required = false) Integer year,
            @RequestParam(value = "from-month", required = false) Integer month,
            @RequestParam(value = "from-day", required = false) Integer day,
            @RequestParam(value = "to-year", required = false) Integer _year,
            @RequestParam(value = "to-month", required = false) Integer _month,
            @RequestParam(value = "to-day", required = false) Integer _day,
            @RequestParam(value = "query") String query,
            OrderTypeEnum type,
            @RequestParam(name = "page-number", required = false, defaultValue = "0") int pageNumber
    ){
        return orderService.getOrders(new OrderRequest(year, month, day, _year, _month, _day), query, type, pageNumber);
    }
}
