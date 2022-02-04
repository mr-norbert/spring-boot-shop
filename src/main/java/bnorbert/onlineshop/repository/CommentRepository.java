package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Comment;
import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByReview_Id(Long reviewId, Pageable pageable);
    Page<Comment> findByReview(Review review, Pageable pageable);
    Page<Comment> findByUser(User user, Pageable pageable);
}
