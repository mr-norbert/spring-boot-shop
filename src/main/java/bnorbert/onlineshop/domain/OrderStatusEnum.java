package bnorbert.onlineshop.domain;

public enum OrderStatusEnum {

    TEST("test"),
    COMPLETED("complete"),
    CANCELED("canceled"),
    REFUNDED("refunded");

    String value;

    OrderStatusEnum(String value) {
        this.value = value;
    }

}
