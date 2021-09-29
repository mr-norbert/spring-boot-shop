package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findOrdersByCreatedDateBefore(Instant createdDate);
}
