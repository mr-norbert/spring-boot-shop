package bnorbert.onlineshop.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "orders_words")
@AllArgsConstructor
@NoArgsConstructor
public class OrderWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String word;
    private long docId;
    private int indexOfWord;
    private int length;
}
