package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter

public class CartResponse {
    private Long id;
    private double grandTotal;
    private Set<CartItemsResponse> cartItems = new HashSet<>();
    private Long userId;
    private String userEmail;

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

    @Override
    public String toString() {
        return "CartResponse{" +
                "id=" + id +
                ", grandTotal=" + grandTotal +
                ", cartItems=" + cartItems +
                ", userId=" + userId +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
