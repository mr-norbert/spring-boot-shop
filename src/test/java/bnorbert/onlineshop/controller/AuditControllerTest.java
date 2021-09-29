package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.AuditEventService;
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
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

    @Mock
    private AuditEventService mockAuditEventService;

    private AuditController auditControllerUnderTest;

    @BeforeEach
    void setUp() {
        auditControllerUnderTest = new AuditController(mockAuditEventService);
    }

    @Test
    void testGetAll() {

        final Page<AuditEvent> auditEvents = new PageImpl<>
                (Collections.singletonList(new AuditEvent("email@gmail.com", "AUTHENTICATION_SUCCESS", new HashMap<>())));
        when(mockAuditEventService.findAll(any(Pageable.class))).thenReturn(auditEvents);

        final ResponseEntity<List<AuditEvent>> result = auditControllerUnderTest.findAll(PageRequest.of(0, 1));

    }

    @Test
    void testFindByPrincipal() {

        final Page<AuditResponse> auditResponses = new PageImpl<>(Collections.singletonList(new AuditResponse()));
        when(mockAuditEventService.findAllByEmail(eq("email@gmail.com"), any(Pageable.class))).thenReturn(auditResponses);

        final ResponseEntity<Page<AuditResponse>> result = auditControllerUnderTest.findAllByEmail("email@gmail.com", PageRequest.of(0, 1));
    }

    @Test
    void testGetAuditEvent() {
        final Optional<AuditEvent> auditEvent = Optional.of
                (new AuditEvent("email@gmail.com", "AUTHENTICATION_SUCCESS", new HashMap<>()));
        when(mockAuditEventService.findById(1L)).thenReturn(auditEvent);

        final Optional<AuditEvent> result = auditControllerUnderTest.findById(1L);
    }


}
