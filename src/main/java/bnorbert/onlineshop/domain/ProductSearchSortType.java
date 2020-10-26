package bnorbert.onlineshop.domain;

import lombok.Getter;
import org.apache.lucene.search.SortField;

@Getter
public enum ProductSearchSortType {

    SORT_TYPE("view_count", "desc", SortField.Type.INT);
    //ID_ASC("id", "asc", SortField.Type.LONG);
    //ID_DESC("id", "desc", SortField.Type.LONG);

    String field;
    String sortType;
    SortField.Type sortFieldType;

    ProductSearchSortType(String field, String sortType, SortField.Type sortFieldType) {
        this.field = field;
        this.sortType = sortType;
        this.sortFieldType = sortFieldType;
    }
}
