package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findProductsByCategoryNameAndBrandNameAndIsAvailableIsTrue(String categoryName, String brandName, Pageable pageable);
    Page<Product> findProductsByCategoryNameAndIsAvailableIsTrue(String categoryName, Pageable pageable);
    Page<Product> findProductsByCategoryNameAndPriceBetweenAndIsAvailableIsTrue(String categoryName, Double priceFrom, Double priceMax, Pageable pageable);
    Page<Product> findProductsByCategoryNameAndBrandNameAndPriceBetweenAndIsAvailableIsTrue(String categoryName, String brandName, Double priceFrom, Double priceMax, Pageable pageable);

}
