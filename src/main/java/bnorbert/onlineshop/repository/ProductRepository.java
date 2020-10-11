package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>  {
    Page<Product> findByNameContaining(String partialName, Pageable pageable);
    Page<Product> findProductsByCategory_IdAndBrand_Id(Long category_id, Long brand_id, Pageable pageable);
    Page<Product> findProductsByCategory_Id(Long category_id, Pageable pageable);
    Page<Product> findProductsByCategory_IdAndPriceBetween(Long category_id, Double priceFrom, Double priceMax, Pageable pageable);
    Page<Product> findProductsByCategory_IdAndBrand_IdAndPriceBetween(Long category_id, Long brand_id, Double priceFrom, Double priceMax, Pageable pageable);
}
