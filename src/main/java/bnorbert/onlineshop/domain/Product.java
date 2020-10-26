package bnorbert.onlineshop.domain;

import lombok.*;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Parameter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@Indexed
@Builder
@AllArgsConstructor
@NoArgsConstructor

@AnalyzerDef(name = "textanalyzer",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = EdgeNGramFilterFactory.class,
                        params = {
                                @Parameter(name = "minGramSize", value = "1"),
                                @Parameter(name = "maxGramSize", value = "20")})
        })

@EntityListeners(AuditingEntityListener.class)

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Field(name = "id_sort", index = Index.NO)
    @SortableField(forField = "id_sort")
    private Long id;

    @Field(analyzer = @Analyzer(definition = "textanalyzer"))
    private String name;

    @Field(analyze = Analyze.NO)
    @Facet(name = "price")
    private double price;

    private String description;

    @Field(analyze = Analyze.NO)
    @Facet(name = "color")
    private String color;

    private String imagePath;

    private int unitInStock;

    @Field(analyze = Analyze.NO)
    @Facet(name = "categoryName")
    private String categoryName;

    @Field(analyze = Analyze.NO)
    @Facet(name = "brandName")
    private String brandName;

    @Field(name = "view_count_sort", index = Index.NO)
    @SortableField(forField = "view_count_sort")
    private Integer viewCount = 0;

    @Field
    private Boolean isAvailable;

    private Instant createdDate;
    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private Instant lastModifiedDate = Instant.now();

    //@ManyToOne(fetch = FetchType.LAZY)
    //@IndexedEmbedded
    //private Brand brand;

    //@ManyToOne(fetch = FetchType.LAZY)
    //@IndexedEmbedded
    //private Category category;

//@Override
    //public boolean equals(Object o) {
    //    if (this == o) return true;
    //    if (o == null || getClass() != o.getClass()) return false;
    //    Product product = (Product) o;
    //    if (!id.equals(product.id)) return false;
    //    return (name != null ? !name.equals(product.name) : product.name != null);
    //}
//
    //@Override
    //public int hashCode() {
    //    int result;
    //    result = (int) (id ^ (id >>> 32));
    //    result = 31 * result + (name != null ? name.hashCode() : 0);
    //    return result;
    //}
}
