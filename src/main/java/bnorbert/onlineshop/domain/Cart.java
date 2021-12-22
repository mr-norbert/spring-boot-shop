package bnorbert.onlineshop.domain;

import bnorbert.onlineshop.binder.CartLineItemsDetailBinder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.PropertyBinderRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.PropertyBinding;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Indexed
public class Cart {

    @Id
    private Long id;
    private double grandTotal;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @PropertyBinding(binder = @PropertyBinderRef(type = CartLineItemsDetailBinder.class))
    //@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @OneToMany(mappedBy="cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartItem.setCart(null);
    }
    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setCart(this);
    }

    public double getSum() {
        double sum;
        //Set<CartItem> cartItemSet = getCartItems();

        //for (CartItem cartItem : cartItemSet) {
        //   sum += cartItem.getSubTotal();
        //}

        sum = cartItems.stream()
                .mapToDouble(CartItem::getSubTotal)
                .sum();

        return sum;

        //Set<Double> numbers = cartItemList.stream().map(CartItem::getSubTotal).collect(Collectors.toSet());
        //return sum = numbers.stream().reduce(0.0, Double::sum);
    }

    public Integer getSumForStripe() {
        int sum = 0;
        Set<CartItem> cartItemSet = getCartItems();
        for (CartItem cartItem : cartItemSet){
            sum += cartItem.getSubTotal();
        }
        return sum;
    }

/*
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

 */

}
