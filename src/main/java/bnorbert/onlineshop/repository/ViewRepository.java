package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ViewRepository extends JpaRepository<View, Long> {

    Optional<View> findTopByProductAndUserOrderByIdDesc(Product product, User user);
    Optional<View> findTop1ByProductAndUserOrderByIdDesc(Product product, User user);
}
