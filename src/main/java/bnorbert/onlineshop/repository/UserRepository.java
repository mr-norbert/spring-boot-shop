package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findAllByEnabledIsFalseAndCreatedDateBefore(Instant createdDate);
}
