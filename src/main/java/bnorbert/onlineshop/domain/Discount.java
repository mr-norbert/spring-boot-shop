package bnorbert.onlineshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Discount {

    @Id
    private String id;
    private Instant createdDate;
    private Instant expirationDate;
    private double percentOff;

    @OneToMany(mappedBy = "discount", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Cart> carts = new HashSet<>();

    public void addCart(Cart cart) {
        carts.add(cart);
        cart.setDiscount(this);
    }

    public void removeCart(Cart cart) {
        carts.remove(cart);
        cart.setDiscount(this);
    }
}
