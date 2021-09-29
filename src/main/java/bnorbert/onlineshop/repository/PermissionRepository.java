package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
