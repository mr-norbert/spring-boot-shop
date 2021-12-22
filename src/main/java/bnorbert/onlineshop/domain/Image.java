package bnorbert.onlineshop.domain;

import bnorbert.onlineshop.binder.ImageWordsBinder;
import lombok.*;
import org.hibernate.annotations.SortNatural;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.PropertyBinderRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.PropertyBinding;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

@Indexed
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String originalFilename;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photo;

    private long size;

    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @ElementCollection
    @SortNatural
    @PropertyBinding(binder = @PropertyBinderRef(type = ImageWordsBinder.class))
    private Map<String, String> words = new TreeMap<>();

}
