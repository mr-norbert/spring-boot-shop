package bnorbert.onlineshop.service;

import bnorbert.onlineshop.config.AuditEventConverter;
import bnorbert.onlineshop.domain.PersistentAuditEvent;
import bnorbert.onlineshop.mapper.AuditMapper;
import bnorbert.onlineshop.repository.PersistenceAuditEventRepository;
import bnorbert.onlineshop.transfer.audit.AuditResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class AuditEventServiceTest {

    @Mock
    private PersistenceAuditEventRepository mockPersistenceAuditEventRepository;
    @Mock
    private AuditEventConverter mockAuditEventConverter;
    @Mock
    private AuditMapper mockAuditMapper;

    private AuditEventService auditEventServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        auditEventServiceUnderTest = new AuditEventService(mockPersistenceAuditEventRepository, mockAuditEventConverter, mockAuditMapper);
    }

    @Test
    void testFindAll() {

        final Page<PersistentAuditEvent> persistentAuditEvents = new PageImpl<>(Collections.singletonList(new PersistentAuditEvent()));
        when(mockPersistenceAuditEventRepository.findAll(any(Pageable.class))).thenReturn(persistentAuditEvents);

        final AuditEvent auditEvent = new AuditEvent("email", "AUTHENTICATION_SUCCESS", new HashMap<>());
        when(mockAuditEventConverter.convertToAuditEvent(new PersistentAuditEvent())).thenReturn(auditEvent);

        final Page<AuditEvent> result = auditEventServiceUnderTest.findAll(PageRequest.of(0, 1));

    }

    @Test
    void testFindAllByPrincipal() {

        final Page<PersistentAuditEvent> persistentAuditEvents = new PageImpl<>(Collections.singletonList(new PersistentAuditEvent()));
        when(mockPersistenceAuditEventRepository.findAllByPrincipal(eq("email"), any(Pageable.class))).thenReturn(persistentAuditEvents);

        when(mockAuditMapper.entitiesToEntityDTOs(Collections.singletonList(new PersistentAuditEvent()))).thenReturn(Collections.singletonList(new AuditResponse()));

        final Page<AuditResponse> result = auditEventServiceUnderTest.findAllByPrincipal("email", PageRequest.of(0, 1));
    }

    @Test
    void testFind() {

        when(mockPersistenceAuditEventRepository.findById(1L)).thenReturn(Optional.of(new PersistentAuditEvent()));

        final AuditEvent auditEvent = new AuditEvent("email", "AUTHENTICATION_FAILURE", new HashMap<>());
        when(mockAuditEventConverter.convertToAuditEvent(new PersistentAuditEvent())).thenReturn(auditEvent);

        final Optional<AuditEvent> result = auditEventServiceUnderTest.find(1L);
    }
}
