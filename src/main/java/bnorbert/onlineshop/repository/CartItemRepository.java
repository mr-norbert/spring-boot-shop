package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findTopByProductAndCartOrderByIdDesc(Product product, Cart cart);
    Optional<CartItem> findTop1ByProductIdAndCartOrderByIdDesc(Long product_id, Cart cart);
}
