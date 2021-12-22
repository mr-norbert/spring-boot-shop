package bnorbert.onlineshop.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "customer_orders")
@EntityListeners(AuditingEntityListener.class)
@org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed

public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericField(name = "id", sortable = Sortable.YES, projectable = Projectable.YES)
    private Long id;

    @GenericField
    private LocalDateTime createdDate;
    @CreatedBy
    private String createdBy;

    private String shippingMethod;

    @KeywordField(name = "status", projectable = Projectable.YES)
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    private double grandTotal;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String address2;
    private String state;
    private String city;
    private String zipCode;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy="order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setOrder(this);
    }

}
