package bnorbert.onlineshop.domain;

import lombok.*;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
@Builder
@AllArgsConstructor
@NoArgsConstructor

@EntityListeners(AuditingEntityListener.class)

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericField(name = "id", sortable = Sortable.YES, projectable = Projectable.YES)
    private Long id;

    @FullTextField(name = "name", analyzer = "custom", projectable = Projectable.YES, searchable = Searchable.YES)
    private String name;

    @GenericField(name = "price", sortable = Sortable.YES, projectable = Projectable.YES,
            searchable = Searchable.YES, aggregable = Aggregable.YES)
    private double price;

    //@KeywordField(name = "description", projectable = Projectable.YES)
    private String description;

    @KeywordField(name = "color", aggregable = Aggregable.YES)
    private String color;

    private String imagePath;

    //@GenericField(name = "unitInStock", projectable = Projectable.YES)
    private int unitInStock;

    @KeywordField(name = "categoryName", projectable = Projectable.YES, aggregable = Aggregable.YES)
    private String categoryName;

    @KeywordField(name = "brandName", projectable = Projectable.YES, aggregable = Aggregable.YES)
    private String brandName;

    @GenericField(name = "view_count_sort", sortable = Sortable.YES)
    private Integer viewCount = 0;

    private int specification;

    private int secondSpec;

    @GenericField
    private Boolean isAvailable;

    private Instant createdDate;
    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private Instant lastModifiedDate = Instant.now();

    //@ManyToOne(fetch = FetchType.LAZY)
    //@IndexedEmbedded
    //private Brand brand;

    //@ManyToOne(fetch = FetchType.LAZY)
    //@IndexedEmbedded
    //private CategoryEnum category;


    @ManyToMany(mappedBy = "products")
    private Set<Pantry> pantries = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        if (!id.equals(product.id)) return false;
        return (name != null ? !name.equals(product.name) : product.name != null);
    }


    @Override
    public int hashCode() {
        int result;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
