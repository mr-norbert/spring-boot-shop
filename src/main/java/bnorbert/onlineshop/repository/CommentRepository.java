package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
