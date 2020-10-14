package bnorbert.onlineshop.domain;

import lombok.Getter;

@Getter
public enum CategoryType {

    HOME_APPLIANCES(1, "Home Appliances"),
    FASHION(2, "Fashion"),
    TV(3, "TV"),
    HOME_GARDEN(4, "Home Garden & DIY"),
    PERSONAL_CARE(5, "Personal Care");

    private long id;
    private String name;

    CategoryType(long id, String name) {
        this.id = id;
        this.name = name;
    }


}
