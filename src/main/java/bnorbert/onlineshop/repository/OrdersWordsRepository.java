package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.OrderWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersWordsRepository extends JpaRepository<OrderWord, Long> {

    @org.springframework.data.jpa.repository.Query(value = "SELECT DISTINCT doc_id FROM orders_words WHERE word = ?1", nativeQuery = true)
    List<Long> findDocIdContainingWord(String word);

    @org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM orders_words WHERE word = ?1 AND doc_id = ?2", nativeQuery = true)
    int getWordCountInDoc(String word, long doc);

    @org.springframework.data.jpa.repository.Query(value = "SELECT length FROM orders_words WHERE word = ?1 AND doc_id = ?2", nativeQuery = true)
    List<Integer> getProductDescriptionLength(String word, long doc);
}
