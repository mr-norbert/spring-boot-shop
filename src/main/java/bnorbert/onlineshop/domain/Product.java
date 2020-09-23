package bnorbert.onlineshop.domain;

import lombok.Getter;
import lombok.Setter;
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

@EntityListeners(AuditingEntityListener.class)

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String description;
    private String imagePath;
    private int unitInStock;
    private Instant createdDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String lastModifiedBy;
    @LastModifiedDate
    private Instant lastModifiedDate = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @ManyToMany(mappedBy = "products")
    private Set<CopyOfTheProduct> copyOfTheProducts = new HashSet<>();


//@Override
    //public boolean equals(Object o) {
    //    if (this == o) return true;
    //    if (o == null || getClass() != o.getClass()) return false;
    //    Product product = (Product) o;
    //    if (!id.equals(product.id)) return false;
    //    return (name != null ? !name.equals(product.name) : product.name != null);
    //}
//
    //@Override
    //public int hashCode() {
    //    int result;
    //    result = (int) (id ^ (id >>> 32));
    //    result = 31 * result + (name != null ? name.hashCode() : 0);
    //    return result;
    //}
}
