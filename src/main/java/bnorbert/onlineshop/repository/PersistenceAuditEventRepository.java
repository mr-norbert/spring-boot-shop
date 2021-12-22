package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.PersistentAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersistenceAuditEventRepository extends JpaRepository<PersistentAuditEvent, Long> {

    //@Query(value = "select a from PersistentAuditEvent a join a.metadata meta where (KEY(meta) = :name )")
    //List<PersistentAuditEvent> findPersistentAuditEventByMetadata(String name);

    @Query(value = "select a from PersistentAuditEvent a join a.metadata meta where (KEY(meta) = :name and meta = :value)")
    List<PersistentAuditEvent> findPersistentAuditEventByMetadata(String name, String value);


}
