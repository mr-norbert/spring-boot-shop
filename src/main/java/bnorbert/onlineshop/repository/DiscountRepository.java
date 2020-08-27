package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, String> {
}
