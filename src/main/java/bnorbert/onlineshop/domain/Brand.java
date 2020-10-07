package bnorbert.onlineshop.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.search.annotations.Field;

import javax.persistence.*;

@Getter
@Setter
@Entity

public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Field
    private String name;

}
