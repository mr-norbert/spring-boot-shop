package bnorbert.onlineshop.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter

@EntityListeners(AuditingEntityListener.class)

public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer qty;
    private double subTotal;
    @CreatedDate
    private Instant createdDate;
    @CreatedBy
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

}
