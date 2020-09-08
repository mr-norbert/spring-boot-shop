package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.AuditEventService;
import bnorbert.onlineshop.transfer.audit.AuditResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class AuditControllerTest {

    @Mock
    private AuditEventService mockAuditEventService;

    private AuditController auditControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        auditControllerUnderTest = new AuditController(mockAuditEventService);
    }

    @Test
    void testGetAll() {

        final Page<AuditEvent> auditEvents = new PageImpl<>(Collections.singletonList(new AuditEvent("email", "AUTHENTICATION_SUCCESS", new HashMap<>())));
        when(mockAuditEventService.findAll(any(Pageable.class))).thenReturn(auditEvents);

        final ResponseEntity<List<AuditEvent>> result = auditControllerUnderTest.getAll(PageRequest.of(0, 1));

    }

    @Test
    void testFindByPrincipal() {

        final Page<AuditResponse> auditResponses = new PageImpl<>(Collections.singletonList(new AuditResponse()));
        when(mockAuditEventService.findAllByPrincipal(eq("email"), any(Pageable.class))).thenReturn(auditResponses);

        final ResponseEntity<Page<AuditResponse>> result = auditControllerUnderTest.findByPrincipal("email", PageRequest.of(0, 1));
    }

    @Test
    void testGet() {

        final Optional<AuditEvent> auditEvent = Optional.of(new AuditEvent("email", "AUTHENTICATION_SUCCESS", new HashMap<>()));
        when(mockAuditEventService.find(1L)).thenReturn(auditEvent);

        final Optional<AuditEvent> result = auditControllerUnderTest.get(1L);
    }
}
