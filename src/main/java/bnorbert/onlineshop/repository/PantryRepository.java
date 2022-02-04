package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Pantry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PantryRepository extends JpaRepository<Pantry, Long> {

    Page<Pantry> findById(Long productId, Pageable pageable);
}
