package bnorbert.onlineshop.transfer.search;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {

    private String categoryName;
    private String brandName;
    private String color;
    private String query;
    private Double price;
    private Double priceMax;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPriceMax() {
        if (priceMax < price){
            throw new IllegalArgumentException();
        }else return priceMax;
    }

    public void setPriceMax(Double priceMax) {
        this.priceMax = priceMax;
    }



}
