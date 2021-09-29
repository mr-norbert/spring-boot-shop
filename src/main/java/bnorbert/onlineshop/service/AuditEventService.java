package bnorbert.onlineshop.service;

import bnorbert.onlineshop.config.LoginAttemptsLogger;
import bnorbert.onlineshop.domain.PersistentAuditEvent;
import bnorbert.onlineshop.mapper.AuditMapper;
import bnorbert.onlineshop.repository.PersistenceAuditEventRepository;
import bnorbert.onlineshop.transfer.audit.AuditResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class AuditEventService {

    private final PersistenceAuditEventRepository persistenceAuditEventRepository;
    private final AuditMapper auditMapper;
    private final LoginAttemptsLogger loginAttemptsLogger;

    public Page<AuditEvent> findAll(Pageable pageable) {
        log.info("Retrieving events");
        return persistenceAuditEventRepository.findAll(pageable)
            .map(loginAttemptsLogger::mapToAuditEvent);
    }

    public Page<AuditResponse> findAllByEmail(String email, Pageable pageable){
        log.info("Retrieving events {}", email);
        Page<PersistentAuditEvent> persistentAuditEvents =
                persistenceAuditEventRepository.findAllByPrincipal
                        (email, pageable);
        List<AuditResponse> auditResponses = auditMapper.entitiesToEntityDTOs(persistentAuditEvents.getContent());
        return new PageImpl<>(auditResponses, pageable, persistentAuditEvents.getTotalElements());
    }

    public Page<AuditResponse> findAllByAuditEventDateBetween(LocalDateTime fromDateTime, LocalDateTime toDateTime, Pageable pageable) {
        log.info("Retrieving events {} {}", fromDateTime, toDateTime);
        Page<PersistentAuditEvent> persistentAuditEvents =
                persistenceAuditEventRepository.findAllByAuditEventDateBetween
                        (fromDateTime, toDateTime, pageable);

        List<AuditResponse> auditResponses = auditMapper.entitiesToEntityDTOs(persistentAuditEvents.getContent());
        return new PageImpl<>(auditResponses, pageable, persistentAuditEvents.getTotalElements());
    }


    public Page<AuditResponse> findAllByLocalDateBetween(String fromDate, LocalDate toDate, Pageable pageable){
        log.info("Retrieving events: {} {} ", fromDate, toDate);
        LocalDate date = LocalDate.parse(fromDate);
        toDate = LocalDate.now();

        Page<PersistentAuditEvent> persistentAuditEvents =
                persistenceAuditEventRepository.findAllByLocalDateBetween
                        (date, toDate, pageable);

        List<AuditResponse> auditResponses = auditMapper.entitiesToEntityDTOs(persistentAuditEvents.getContent());
        return new PageImpl<>(auditResponses, pageable, persistentAuditEvents.getTotalElements());
    }


    public Optional<AuditEvent> findById(Long id) {
        log.info("Retrieving event {}", id);
        return persistenceAuditEventRepository.findById(id)
            .map(loginAttemptsLogger::mapToAuditEvent);
    }


    public List<AuditResponse> findMetadata(String name, String value) {
        log.info("Retrieving event {}, {}", name, value);
        List<PersistentAuditEvent> events = persistenceAuditEventRepository.findPersistentAuditEventByMetadata
                (name, value);
        List<AuditResponse> auditResponses =
                        auditMapper.entitiesToEntityDTOs(events);
        return auditResponses;

    }


}
