package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionsRepository extends JpaRepository<Question, Long> {
}
