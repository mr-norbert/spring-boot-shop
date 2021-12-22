package bnorbert.onlineshop.transfer.order;

import org.springframework.data.domain.Page;

public class OrdersResponses {

    public final Page<OrderResponse> orderResponses;

    public OrdersResponses(Page<OrderResponse> orderResponses) {
        this.orderResponses = orderResponses;
    }
}
