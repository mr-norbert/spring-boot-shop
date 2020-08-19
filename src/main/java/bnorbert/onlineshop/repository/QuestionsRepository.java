package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionsRepository extends JpaRepository<Question, Long> {
    Page<Question> findByProduct(Product product, Pageable pageable);
}
