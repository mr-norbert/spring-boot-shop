package bnorbert.onlineshop.transfer.search;

import bnorbert.onlineshop.transfer.product.ProductResponse;

import java.util.List;

public class SearchResponse {

    public final List<SearchFacet> categoryFacet;
    public final List<SearchFacet> brandFacet;
    public final List<SearchFacet> colorFacet;
    public final List<SearchFacet> priceFacet;
    public final List<ProductResponse> products;

    public SearchResponse(List<SearchFacet> categoryFacet, List<SearchFacet> brandFacet, List<SearchFacet> colorFacet,
                          List<SearchFacet> priceFacet, List<ProductResponse> products) {
        this.categoryFacet = categoryFacet;
        this.brandFacet = brandFacet;
        this.colorFacet = colorFacet;
        this.priceFacet = priceFacet;
        this.products = products;
    }
}
