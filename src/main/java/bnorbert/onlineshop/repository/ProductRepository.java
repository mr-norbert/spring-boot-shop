package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findProductsByCategoryNameContaining(String categoryName);

    @org.springframework.data.jpa.repository.Query(value = "SELECT name FROM (SELECT name, SUM(hits) AS totalHits FROM product WHERE name REGEXP ?1 GROUP BY name ORDER BY totalHits DESC LIMIT ?2) as Q", nativeQuery = true)
    List<String> findSuggestions(String pattern, int limit);

    @org.springframework.data.jpa.repository.Query(value = "SELECT brand_name FROM (SELECT brand_name, SUM(hits) AS totalHits FROM product WHERE brand_name REGEXP ?1 GROUP BY name ORDER BY totalHits DESC LIMIT ?2) as Q", nativeQuery = true)
    List<String> getSuggestions(String pattern, int limit);

    //@org.springframework.data.jpa.repository.Query(value = "SELECT created_date FROM product WHERE id = ?1", nativeQuery = true)
    //LocalDateTime getCreatedDate(long id);

}
