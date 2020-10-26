package bnorbert.onlineshop.transfer.search;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SearchRequest {

    private String categoryName;
    private String brandName;
    private String color;
    private String searchWord;
    private Double price;
    private Double priceMax;
    private Integer page;

    public SearchRequest(String categoryName, String brandName, String color, String searchWord, Double price, Double priceMax, Integer page) {
        this.categoryName = categoryName;
        this.brandName = brandName;
        this.color = color;
        this.searchWord = searchWord;
        this.price = price;
        this.priceMax = priceMax;
        this.page = page;
    }

    public static SearchRequest searchAll(){
        return new SearchRequest(null, null, null, null, null, null, null);
    }

    public SearchRequest searchBrand(String brandName) {
        return new SearchRequest(brandName, this.categoryName, this.color, this.searchWord, this.price, this.priceMax, this.page);
    }

    public SearchRequest searchCategory(String categoryName) {
        return new SearchRequest(categoryName, this.brandName, this.color, this.searchWord, this.price, this.priceMax,  this.page);
    }

    public SearchRequest searchColor(String color) {
        return new SearchRequest(color, this.categoryName, this.brandName, this.searchWord, this.price, this.priceMax,  this.page);
    }

    public SearchRequest searchWord(String searchWord) {
        return new SearchRequest(searchWord, this.categoryName, this.brandName, this.color, this.price,  this.priceMax, this.page);
    }

    public SearchRequest searchPrice(Double price) {
        return new SearchRequest(this.searchWord, this.categoryName, this.brandName, this.color, price, this.priceMax, this.page);
    }

}
