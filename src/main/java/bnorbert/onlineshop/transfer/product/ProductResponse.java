package bnorbert.onlineshop.transfer.product;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ProductResponse {
    private Long id;
    private String name;
    private double price;
    private String description;
    private String imagePath;
    private int unitInStock;
    private LocalDateTime createdDate;
    private String brandName;
    private String categoryName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductResponse that = (ProductResponse) o;

        if (!id.equals(that.id)) return false;
        if (Double.compare(that.price, price) != 0) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        return imagePath != null ? imagePath.equals(that.imagePath) : that.imagePath == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (imagePath != null ? imagePath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", unitInStock=" + unitInStock +
                ", createdDate=" + createdDate +
                ", brandName='" + brandName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
