package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Comment;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByCommentAndUserOrderByIdDesc(Comment comment, User currentUser);
}
