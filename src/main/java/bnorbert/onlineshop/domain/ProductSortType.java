package bnorbert.onlineshop.domain;

import lombok.Getter;
import org.apache.lucene.search.SortField;

@Getter
public enum ProductSortType {

    ID_ASC("id", "asc", SortField.Type.LONG),
    ID_DESC("id", "desc", SortField.Type.LONG),
    PRICE_ASC("price", "asc", SortField.Type.DOUBLE),
    PRICE_DESC("price", "desc", SortField.Type.DOUBLE),
    NAME_ASC("name", "asc", SortField.Type.STRING),
    NAME_DESC("name", "desc", SortField.Type.STRING);

    String field;
    String sortType;
    SortField.Type sortFieldType;

    ProductSortType(String field, String sortType, SortField.Type sortFieldType) {
        this.field = field;
        this.sortType = sortType;
        this.sortFieldType = sortFieldType;
    }
}
