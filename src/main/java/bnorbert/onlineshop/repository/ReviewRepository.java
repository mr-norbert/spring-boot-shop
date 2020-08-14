package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findTopByProductAndUserOrderByIdDesc(Product product, User currentUser);
    Page<Review> findReviewsByProductIdAndRatingOrderByIdDesc(long productId, int rating, Pageable pageable);
    Page<Review> findReviewsByProductId(long productId, Pageable pageable);
}
