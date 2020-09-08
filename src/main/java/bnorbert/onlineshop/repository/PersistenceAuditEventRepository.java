package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.PersistentAuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;


public interface PersistenceAuditEventRepository extends JpaRepository<PersistentAuditEvent, Long> {

    List<PersistentAuditEvent> findByPrincipalAndAuditEventDateAfterAndAuditEventType(String principal, Instant after, String type);

    Page<PersistentAuditEvent> findAllByPrincipal(String email, Pageable pageable);

}
