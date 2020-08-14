package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContaining(String partialName, Pageable pageable);
    Page<Product> findProductsByCategory_Id(Long category_id, Pageable pageable);
}
