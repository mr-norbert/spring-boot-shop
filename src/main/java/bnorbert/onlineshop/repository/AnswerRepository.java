package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Answer;
import bnorbert.onlineshop.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Page<Answer> findByQuestion(Question question, Pageable pageable);
}
