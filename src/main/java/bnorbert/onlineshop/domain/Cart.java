package bnorbert.onlineshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Cart {

    @Id
    private long id;
    private double grandTotal;
    private int numberOfProducts;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(name = "cart_products",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products = new HashSet<>();

    public void addToCart(Product product) {
        products.add(product);
        product.getCarts().add(this);
    }

    public void removeFromCart(Product product) {
        products.remove(product);
        product.getCarts().remove(this);
    }

    public Double getGrandTotal() {
        double sum = 0D;
        Set<Product> products = getProducts();
        for (Product product : products){
            sum += product.getProductTotal();
        }
        return sum;
    }

    public int getNumberOfProducts() {
        return this.products.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return id == cart.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
