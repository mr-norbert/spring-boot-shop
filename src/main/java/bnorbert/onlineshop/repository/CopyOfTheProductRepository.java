package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.CopyOfTheProduct;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CopyOfTheProductRepository extends JpaRepository<CopyOfTheProduct, Long> {
}
