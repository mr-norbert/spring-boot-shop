package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @org.springframework.data.jpa.repository.Query(value = "SELECT created_date FROM customer_orders WHERE id = ?1", nativeQuery = true)
    Instant getCreatedDate(long id);
}
