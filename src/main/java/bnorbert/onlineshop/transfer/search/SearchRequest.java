package bnorbert.onlineshop.transfer.search;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {

    private String categoryName;
    private String brandName;
    private String color;
    private String searchWord;
    private Double price;
    private Double priceMax;
    private Integer page;

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

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }


    @Override
    public String toString() {
        return "SearchRequest{" +
                "categoryName='" + categoryName + '\'' +
                ", brandName='" + brandName + '\'' +
                ", color='" + color + '\'' +
                ", searchWord='" + searchWord + '\'' +
                ", price=" + price +
                ", priceMax=" + priceMax +
                ", page=" + page +
                '}';
    }
}
