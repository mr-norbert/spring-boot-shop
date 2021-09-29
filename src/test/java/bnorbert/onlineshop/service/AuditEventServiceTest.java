package bnorbert.onlineshop.service;

import bnorbert.onlineshop.config.LoginAttemptsLogger;
import bnorbert.onlineshop.domain.PersistentAuditEvent;
import bnorbert.onlineshop.mapper.AuditMapper;
import bnorbert.onlineshop.repository.PersistenceAuditEventRepository;
import bnorbert.onlineshop.transfer.audit.AuditResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuditEventServiceTest {

    @Mock
    private PersistenceAuditEventRepository mockPersistenceAuditEventRepository;
    @Mock
    private AuditMapper mockAuditMapper;
    @Mock
    private LoginAttemptsLogger mockLoginAttemptsLogger;

    private AuditEventService auditEventServiceUnderTest;

    @BeforeEach
    void setUp() {
        auditEventServiceUnderTest = new AuditEventService(mockPersistenceAuditEventRepository, mockAuditMapper, mockLoginAttemptsLogger);
    }

    @Test
    void testFindAll() {

        PersistentAuditEvent identifier = new PersistentAuditEvent();
        identifier.setId(1L);
        identifier.setIp("127.0.0.1");
        identifier.setPrincipal("principal");
        identifier.setFingerprints("Apache-HttpClient  4.5");
        identifier.setAuditEventType("AUTHENTICATION_SUCCESS");

        Page<PersistentAuditEvent> persistentAuditEvents = new PageImpl<>(Collections.singletonList(identifier));

        when(mockPersistenceAuditEventRepository.findAll(any(Pageable.class))).thenReturn(persistentAuditEvents);

        AuditEvent auditEvent = new AuditEvent("principal", "AUTHENTICATION_SUCCESS", new HashMap<>());
        when(mockLoginAttemptsLogger.mapToAuditEvent(identifier)).thenReturn(auditEvent);

        final Page<AuditEvent> result = auditEventServiceUnderTest.findAll(PageRequest.of(0, 1));

    }


    @Test
    void testFindMetadata() {

        PersistentAuditEvent identifier = new PersistentAuditEvent();
        identifier.setId(1L);
        identifier.setIp("127.0.0.1");
        identifier.setPrincipal("principal");
        identifier.setFingerprints("Apache-HttpClient  4.5");
        identifier.setAuditEventType("AUTHENTICATION_SUCCESS");
        identifier.setMetadata(new HashMap<>());
        List<PersistentAuditEvent> events = Collections.singletonList(identifier);
        when(mockPersistenceAuditEventRepository.findPersistentAuditEventByMetadata("name", "127.0.0.1")).thenReturn(events);

        AuditResponse auditResponse = new AuditResponse();
        auditResponse.setId(1L);
        auditResponse.setPrincipal("principal");
        auditResponse.setAuditEventType("AUTHENTICATION_SUCCESS");
        auditResponse.setMetadata(new HashMap<>());
        List<AuditResponse> auditResponses = Collections.singletonList(auditResponse);
        when(mockAuditMapper.entitiesToEntityDTOs(Collections.singletonList(identifier))).thenReturn(auditResponses);

        List<AuditResponse> result = auditEventServiceUnderTest.findMetadata("name", "127.0.0.1");
    }

    @Test
    void testFindById() {

        PersistentAuditEvent identifier = new PersistentAuditEvent();
        identifier.setId(1L);
        identifier.setIp("127.0.0.1");
        identifier.setPrincipal("principal");
        identifier.setFingerprints("Apache-HttpClient  4.5");
        identifier.setAuditEventType("AUTHENTICATION_SUCCESS");
        identifier.setMetadata(new HashMap<>());
        Optional<PersistentAuditEvent> persistentAuditEvent = Optional.of(identifier);
        when(mockPersistenceAuditEventRepository.findById(1L)).thenReturn(persistentAuditEvent);

        AuditEvent auditEvent = new AuditEvent("principal", "AUTHENTICATION_SUCCESS", new HashMap<>());
        when(mockLoginAttemptsLogger.mapToAuditEvent(identifier)).thenReturn(auditEvent);

        Optional<AuditEvent> result = auditEventServiceUnderTest.findById(1L);
    }

    @Test
    void testFindAllByEmail() {

        PersistentAuditEvent identifier = new PersistentAuditEvent();
        identifier.setPrincipal("principal");
        Page<PersistentAuditEvent> persistentAuditEvents = new PageImpl<>(Collections.singletonList(identifier));
        when(mockPersistenceAuditEventRepository.findAllByPrincipal(eq("principal"), any(Pageable.class))).thenReturn(persistentAuditEvents);

        Page<AuditResponse> result = auditEventServiceUnderTest.findAllByEmail("principal", PageRequest.of(0, 1));
    }


}
