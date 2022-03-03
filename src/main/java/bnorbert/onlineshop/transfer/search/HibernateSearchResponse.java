package bnorbert.onlineshop.transfer.search;

import bnorbert.onlineshop.transfer.product.ProductResponse;
import org.hibernate.search.util.common.data.Range;

import java.util.List;
import java.util.Map;

public class HibernateSearchResponse {

    public final Map<Range<Double>, Long> countByPriceRange;
    public final Map<String, Long> countsByColor;
    public final Map<String, Long> countsByCategory;
    public final Map<String, Long> countsByBrand;
    public final List<ProductResponse> productResponses;

    public HibernateSearchResponse(Map<Range<Double>, Long> countByPriceRange, Map<String, Long> countsByColor, Map<String, Long> countsByCategory,
                                   Map<String, Long> countsByBrand, List<ProductResponse> productResponses) {
        this.countByPriceRange = countByPriceRange;
        this.countsByColor = countsByColor;
        this.countsByCategory = countsByCategory;
        this.countsByBrand = countsByBrand;
        this.productResponses = productResponses;
    }
}
