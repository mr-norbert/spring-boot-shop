package bnorbert.onlineshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
public class Pantry {

    @Id
    private long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private Product product;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(name = "window_shopping",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "recommended_product_id"))
    private Set<Product> products = new LinkedHashSet<>();

    public void addProduct(Product product) {
        products.add(product);
        product.getPantries().add(this);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.getPantries().add(this);
    }
}
