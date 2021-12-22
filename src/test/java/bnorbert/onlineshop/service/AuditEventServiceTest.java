package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.PersistentAuditEvent;
import bnorbert.onlineshop.mapper.AuditMapper;
import bnorbert.onlineshop.repository.PersistenceAuditEventRepository;
import bnorbert.onlineshop.transfer.audit.AuditResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuditEventServiceTest {

    @Mock
    private PersistenceAuditEventRepository mockPersistenceAuditEventRepository;
    @Mock
    private AuditMapper mockAuditMapper;

    @Mock
    private EntityManager entityManager;
    @Mock
    private HttpServletRequest request;

    private AuditEventService auditEventServiceUnderTest;



    @BeforeEach
    void setUp() {
        auditEventServiceUnderTest = new AuditEventService(mockPersistenceAuditEventRepository, mockAuditMapper, entityManager, request);
    }


    @Test
    void testFindMetadata() {

        PersistentAuditEvent identifier = new PersistentAuditEvent();
        identifier.setId(1L);
        identifier.setPrincipal("principal");
        identifier.setFingerprints("Apache-HttpClient  4.5");
        identifier.setAuditEventType("AUTHENTICATION_SUCCESS");
        //identifier.setMetadata(new HashMap<>());
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

}
