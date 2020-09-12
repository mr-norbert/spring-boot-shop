package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
