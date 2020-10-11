package bnorbert.onlineshop.transfer.category;

import bnorbert.onlineshop.transfer.product.ProductResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
public class CategoryResponse {

    private Long id;
    private String name;
    //optional
    //private Set<ProductResponse> products = new HashSet<>();
}
