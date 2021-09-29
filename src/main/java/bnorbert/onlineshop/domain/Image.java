package bnorbert.onlineshop.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photo;



}
