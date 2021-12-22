package bnorbert.onlineshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "queries")
@Getter
@Setter
public class Query {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userIp;
    //@ColumnTransformer(read = "AES_DECRYPT(UNHEX(query), 'body')", write = "HEX(AES_ENCRYPT(?, 'body'))")
    private String query;
    private int hits;

    public void incrementHits(){
        hits++;
    }
}
