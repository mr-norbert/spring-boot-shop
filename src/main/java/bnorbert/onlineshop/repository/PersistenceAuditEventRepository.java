package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.PersistentAuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface PersistenceAuditEventRepository extends JpaRepository<PersistentAuditEvent, Long> {

    Page<PersistentAuditEvent> findAllByPrincipal(String email, Pageable pageable);
    Page<PersistentAuditEvent> findAllByAuditEventDateBetween(LocalDateTime fromAuditEventDate, LocalDateTime toAuditEventDate, Pageable pageable);
    Page<PersistentAuditEvent> findAllByLocalDateBetween(LocalDate fromLocalDate, LocalDate toLocalDate, Pageable pageable);


    //@Query(value = "select a from PersistentAuditEvent a join a.metadata meta where (KEY(meta) = :name )")
    //List<PersistentAuditEvent> findPersistentAuditEventByMetadata(String name);

    @Query(value = "select a from PersistentAuditEvent a join a.metadata meta where (KEY(meta) = :name and meta = :value)")
    List<PersistentAuditEvent> findPersistentAuditEventByMetadata(String name, String value);


}
