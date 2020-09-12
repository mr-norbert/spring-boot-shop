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
    private Long id;
    private double grandTotal;
    private double savedAmount;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Discount discount;

    @OneToMany(mappedBy="cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartItem.setCart(null);
    }

    public double getSum() {
        double sum = 0D;
        Set<CartItem> cartItemSet = getCartItems();
        for (CartItem cartItem : cartItemSet){
            sum += cartItem.getSubTotal();
        }
        return sum;
    }

    public Integer getSumForStripe() {
        int sum = 0;
        Set<CartItem> cartItemSet = getCartItems();
        for (CartItem cartItem : cartItemSet){
            sum += cartItem.getSubTotal();
        }
        return sum;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return id.equals(cart.id);
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
