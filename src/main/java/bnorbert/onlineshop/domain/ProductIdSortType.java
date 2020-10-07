package bnorbert.onlineshop.domain;

import lombok.Getter;
import org.apache.lucene.search.SortField;

@Getter
public enum ProductIdSortType {

    ID_ASC("id", "asc", SortField.Type.LONG),
    ID_DESC("id", "desc", SortField.Type.LONG);

    String field;
    String sortType;
    SortField.Type sortFieldType;

    ProductIdSortType(String field, String sortType, SortField.Type sortFieldType) {
        this.field = field;
        this.sortType = sortType;
        this.sortFieldType = sortFieldType;
    }
}
