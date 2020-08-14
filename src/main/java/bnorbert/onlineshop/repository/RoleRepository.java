package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
