package bnorbert.onlineshop.service;

import bnorbert.onlineshop.config.AuditEventConverter;
import bnorbert.onlineshop.domain.PersistentAuditEvent;
import bnorbert.onlineshop.mapper.AuditMapper;
import bnorbert.onlineshop.repository.PersistenceAuditEventRepository;
import bnorbert.onlineshop.transfer.audit.AuditResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class AuditEventService {

    private final PersistenceAuditEventRepository persistenceAuditEventRepository;
    private final AuditEventConverter auditEventConverter;
    private final AuditMapper auditMapper;

    public AuditEventService(
            PersistenceAuditEventRepository persistenceAuditEventRepository, AuditEventConverter auditEventConverter, AuditMapper auditMapper) {
        this.persistenceAuditEventRepository = persistenceAuditEventRepository;
        this.auditEventConverter = auditEventConverter;
        this.auditMapper = auditMapper;
    }

    public Page<AuditEvent> findAll(Pageable pageable) {
        log.info("Retrieving events");
        return persistenceAuditEventRepository.findAll(pageable)
            .map(auditEventConverter::convertToAuditEvent);
    }


    public Page<AuditResponse> findAllByPrincipal(String email, Pageable pageable){
        log.info("Retrieving events");
        Page<PersistentAuditEvent> persistentAuditEvents =
                persistenceAuditEventRepository.findAllByPrincipal(email, pageable);
        List<AuditResponse> auditResponses = auditMapper.entitiesToEntityDTOs(persistentAuditEvents.getContent());
        return new PageImpl<>(auditResponses, pageable, persistentAuditEvents.getTotalElements());
    }


    public Optional<AuditEvent> find(Long id) {
        log.info("Retrieving event");
        return persistenceAuditEventRepository.findById(id)
            .map(auditEventConverter::convertToAuditEvent);
    }
}
