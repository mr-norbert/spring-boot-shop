package bnorbert.onlineshop.repository;


import bnorbert.onlineshop.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
