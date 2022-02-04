package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findTop1ByProductIdAndCart_Id(Long productId, long cartId);
    List<CartItem> findTopByProduct_IdAndCart_Id(Long productId, long cartId);

}
