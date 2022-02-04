package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BundleRepository extends JpaRepository<Bundle, Long> {
    Optional<Bundle> findTop1ByNameAndProductId(String name, Long productId);
}
