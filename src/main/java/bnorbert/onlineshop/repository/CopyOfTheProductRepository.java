package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.CopyOfTheProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CopyOfTheProductRepository extends JpaRepository<CopyOfTheProduct, Long> {
    Page<CopyOfTheProduct> findById(Long product_id, Pageable pageable);
}
