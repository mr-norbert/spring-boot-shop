package bnorbert.onlineshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;
    private String intent;
    //@ColumnTransformer(read = "AES_DECRYPT(UNHEX(content), 'body')", write = "HEX(AES_ENCRYPT(?, 'body'))")
    private String content;
    private Instant createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id.equals(review.id);
    }

    @Override
    public int hashCode() {
        int result;
        result = (int) (id ^ (id >>> 32));
        result = (int) (rating ^ (rating >>> 32));
        return result;
    }



}
