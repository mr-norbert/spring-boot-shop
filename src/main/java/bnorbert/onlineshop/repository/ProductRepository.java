package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findProductsByCategoryNameAndBrandNameAndIsAvailableIsTrue(String categoryName, String brandName, Pageable pageable);
    List<Product> findProductsByCategoryNameContaining(String categoryName);

}
