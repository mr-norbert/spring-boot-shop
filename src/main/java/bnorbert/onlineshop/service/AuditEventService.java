package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.PersistentAuditEvent;
import bnorbert.onlineshop.mapper.AuditMapper;
import bnorbert.onlineshop.repository.PersistenceAuditEventRepository;
import bnorbert.onlineshop.transfer.audit.AuditResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@Slf4j
@Transactional
public class AuditEventService {

    private static final String PATH_FIELD_AUTHORIZATION_FAILURE = "userMetadata.AUTHORIZATION_FAILURE";
    private static final String PATH_FIELD_AUTHENTICATION_SUCCESS = "userMetadata.AUTHENTICATION_SUCCESS";
    private static final String PATH_FIELD_AUTHENTICATION_FAILURE = "userMetadata.AUTHENTICATION_FAILURE";

    private final String[] pathFields = new String[]{
            PATH_FIELD_AUTHORIZATION_FAILURE, PATH_FIELD_AUTHENTICATION_SUCCESS, PATH_FIELD_AUTHENTICATION_FAILURE
    };

    private final PersistenceAuditEventRepository eventRepository;
    private final AuditMapper auditMapper;
    private final EntityManager entityManager;
    private final HttpServletRequest request;

    public AuditEventService(PersistenceAuditEventRepository eventRepository, AuditMapper auditMapper,
                             EntityManager entityManager, HttpServletRequest request) {
        this.eventRepository = eventRepository;
        this.auditMapper = auditMapper;
        this.entityManager = entityManager;
        this.request = request;
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    public List<AuditResponse> findMetadata(String name, String value) {
        log.info("Retrieving event {}, {}", name, value);
        List<PersistentAuditEvent> events = eventRepository.findPersistentAuditEventByMetadata(name, value);
        return auditMapper.entitiesToEntityDTOs(events);
    }

    public List<AuditResponse> getMetadata(){
        log.info("Retrieving events");
        SearchSession searchSession = org.hibernate.search.mapper.orm.Search.session(entityManager);
        SearchResult<PersistentAuditEvent> result = searchSession.search(PersistentAuditEvent.class)
                .where(f -> f.match()
                        .fields(pathFields)
                        .matching(getClientIP()))//prototype
                .fetch(20);
        List<PersistentAuditEvent> response = result.hits();

        return auditMapper.entitiesToEntityDTOs(response);
    }


}
