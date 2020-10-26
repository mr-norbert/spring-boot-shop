package bnorbert.onlineshop.transfer.search;

import lombok.Getter;

@Getter
public class SearchFacet {

    private String value;
    private Integer count;

    public SearchFacet(String value, Integer count) {
        this.value = value;
        this.count = count;
    }
}
