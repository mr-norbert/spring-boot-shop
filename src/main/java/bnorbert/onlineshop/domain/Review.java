package bnorbert.onlineshop.domain;

import bnorbert.onlineshop.binder.MultiTypeReviewMetadataBinder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SortNatural;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.PropertyBinderRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.PropertyBinding;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Entity
@Getter
@Setter
@Indexed
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;
    private String intent;
    //@ColumnTransformer(read = "AES_DECRYPT(UNHEX(content), 'body')", write = "HEX(AES_ENCRYPT(?, 'body'))")
    private String content;
    private Instant createdDate;
    private int predictedRating;
    @GenericField(name = "rating_probability", sortable = Sortable.YES, projectable = Projectable.YES,
            searchable = Searchable.YES, aggregable = Aggregable.YES)
    private double ratingProbability;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    @ElementCollection
    @SortNatural
    @PropertyBinding(binder = @PropertyBinderRef(type = MultiTypeReviewMetadataBinder.class))
    private Map<String, Serializable> multiTypeReviewMetadata = new TreeMap<>();

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
