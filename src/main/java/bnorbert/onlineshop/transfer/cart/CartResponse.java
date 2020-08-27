package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
public class CartResponse {
    private long id;
    private double grandTotal;
    private int numberOfProducts;
    private Long userId;
    private String userEmail;
    private Set<ProductPresentationResponse> products = new HashSet<>();

/*
    public Double getGrandTotal() {
        double sum = 0D;
        Set<ProductPresentationResponse> products = getProducts();
        for (ProductPresentationResponse product : products){
            sum += product.getProductTotal();
        }
        return sum;
    }

    public int getNumberOfProducts() {
        return this.products.size();
    }

     */
}
