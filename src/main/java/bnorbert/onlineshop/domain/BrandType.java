package bnorbert.onlineshop.domain;

import lombok.Getter;

@Getter
public enum BrandType {

    BRAND(1, "Brand"),
    ANOTHER_BRAND(2, "Brand2");

    private long id;
    private String name;

    BrandType(long id, String name) {
        this.id = id;
        this.name = name;
    }


}
