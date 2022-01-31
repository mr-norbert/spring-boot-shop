package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.Order;
import bnorbert.onlineshop.domain.OrderTypeEnum;
import bnorbert.onlineshop.service.OrderService;
import bnorbert.onlineshop.transfer.address.CreateAddressRequest;
import bnorbert.onlineshop.transfer.cart.CartResponse;
import bnorbert.onlineshop.transfer.order.OrderRequest;
import bnorbert.onlineshop.transfer.order.OrderResponse;
import bnorbert.onlineshop.transfer.order.OrdersResponses;
import org.springframework.data.domain.Pageable;
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

    @PostMapping("/createOrder")
    public ResponseEntity<Order> createOrder(CreateAddressRequest request) {
        orderService.createOrder(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getOrder/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return status(HttpStatus.OK).body(orderService.getOrderId(id));
    }

    @GetMapping("/getGodViewOverCarts")
    public ResponseEntity<List<CartResponse>> getGodViewOverCarts(
            @RequestParam(value = "categoryName", required = false) String categoryName,
            @RequestParam(value = "lowerBound", required = false) double lowerBound,
            @RequestParam(value = "upperBound", required = false) double upperBound
    ) {
        return status(HttpStatus.OK).body(orderService.getGodViewOverCarts(categoryName, lowerBound, upperBound));
    }

    @GetMapping("/orders")
    public OrdersResponses getOrders(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "day", required = false) Integer day,
            @RequestParam(value = "toYear", required = false) Integer _year,
            @RequestParam(value = "toMonth", required = false) Integer _month,
            @RequestParam(value = "toDay", required = false) Integer _day,
            @RequestParam(value = "query") String query, Pageable pageable,
            OrderTypeEnum type,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber
    ){
        return orderService.getOrders(new OrderRequest(year, month, day, _year, _month, _day), query, pageable, type, pageNumber);
    }
}
