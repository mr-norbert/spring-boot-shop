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
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private String name;
    private double price;
    private String description;
    private String imagePath;
    private int unitInStock;
    private Instant createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @ManyToMany(mappedBy = "products")
    private Set<Cart> carts = new HashSet<>();

    @ManyToMany(mappedBy = "products")
    private Set<CopyOfTheProduct> copyOfTheProducts = new HashSet<>();

    public double getProductTotal() {
        return price * quantity;
    }

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
