package bnorbert.onlineshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private Instant createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    //@ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
}
