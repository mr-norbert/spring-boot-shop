package bnorbert.onlineshop.config;

import bnorbert.onlineshop.domain.PersistentAuditEvent;
import bnorbert.onlineshop.repository.PersistenceAuditEventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua_parser.Client;
import ua_parser.Parser;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Component
@AllArgsConstructor
@Slf4j
public class LoginAttemptsLogger implements AuditEventRepository {

    private final PersistenceAuditEventRepository persistenceAuditEventRepository;
    private final HttpServletRequest request;
    private static final int EVENT_DATA_COLUMN_MAX_LENGTH = 255;

    /*
    @EventListener
    public void trace(AuditApplicationEvent auditApplicationEvent) {
        AuditEvent auditEvent = auditApplicationEvent.getAuditEvent();
        log.info(auditEvent.getPrincipal() + " event : " + auditEvent.getType());

        WebAuthenticationDetails details = (WebAuthenticationDetails) auditEvent.getData().get("details");
        if (auditEvent.getData().get("details") != null) {
            log.info("  Remote IP address: " + details.getRemoteAddress());
            log.info("  Session Id: " + details.getSessionId());
        }

    }

     */

    public Map<String, Object> convertDataToObjects(Map<String, String> map) {
        Map<String, Object> results = new HashMap<>();

        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                results.put(entry.getKey(), entry.getValue());
            }
        }
        return results;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void add(AuditEvent event) {

        String AUTHENTICATION_SUCCESS = "AUTHENTICATION_SUCCESS";
        String AUTHENTICATION_FAILURE = "AUTHENTICATION_FAILURE";
        String AUTHORIZATION_FAILURE = "AUTHORIZATION_FAILURE";
        String deviceDetails = getDeviceDetails(request.getHeader("user-agent"));

        Instant instant = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        LocalDate ld = LocalDate.now();

        PersistentAuditEvent identifier = new PersistentAuditEvent();
        identifier.setPrincipal(event.getPrincipal());
        identifier.setAuditEventDate(ldt);
        identifier.setLocalDate(ld);
        identifier.setFingerprints(deviceDetails);
        identifier.setAuditEventType("STORED");
        //identifier.setAuditEventType(event.getType());
        //identifier.setLocation();

        //Map<String, String> data = convertDataToStrings(event.getData());
        //System.err.println(truncate(data));
        Map<String, String> metadata = new LinkedHashMap<>();

        if (event.getType().equals(AUTHORIZATION_FAILURE)){
            metadata.put(AUTHORIZATION_FAILURE, getClientIP());
            identifier.setMetadata(metadata);
        }
        if (event.getType().equals(AUTHENTICATION_SUCCESS)){
            metadata.put(AUTHENTICATION_SUCCESS, getClientIP());
            identifier.setMetadata(metadata);
        }
        if (event.getType().equals(AUTHENTICATION_FAILURE)){
            metadata.put(AUTHENTICATION_FAILURE, getClientIP());
            identifier.setMetadata(metadata);
        }

        persistenceAuditEventRepository.save(identifier);
    }

    @Override
    public List<AuditEvent> find(String principal, Instant after, String type) {
        return null;
    }


    private String getDeviceDetails(String userAgent) {
        String deviceDetails = "UNKNOWN";

        Parser parser = new Parser();
        Client client = parser.parse(userAgent);
        if (Objects.nonNull(client)) {
            deviceDetails = client.userAgent.family + "  " + client.userAgent.major + "." + client.userAgent.minor +
                    " - " + client.os.family + "  " + client.os.major; //+ "." + client.os.minor ;
                    //" - " + client.device.family;
        }

        return deviceDetails;
    }


    public Map<String, String> convertDataToStrings(Map<String, Object> map) {
        String AUTHORIZATION_FAILURE = "AUTHORIZATION_FAILURE";
        Map<String, String> results = new HashMap<>();

        if (map != null) {

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof WebAuthenticationDetails) {
                    WebAuthenticationDetails details = (WebAuthenticationDetails) entry.getValue();
                    //results.put("remoteAddress", details.getRemoteAddress());
                    results.put("sessionId", details.getSessionId());
                    results.put(AUTHORIZATION_FAILURE, details.getRemoteAddress());
                }

            }

        }
        return results;
    }


    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }


    private Map<String, String> truncate(Map<String, String> map) {
        Map<String, String> results = new HashMap<>();

        if (map != null) {

            for (Map.Entry<String, String> entry : map.entrySet()) {
                String value = entry.getValue();

                if (value != null) {
                    int length = value.length();
                    if (length > EVENT_DATA_COLUMN_MAX_LENGTH) {

                        value = value.substring(0, EVENT_DATA_COLUMN_MAX_LENGTH);
                        log.warn("Event data for {} too long ({}) has been truncated to {}. Consider increasing column width.",
                                entry.getKey(), length, EVENT_DATA_COLUMN_MAX_LENGTH);
                    }

                }

                results.put(entry.getKey(), value);
            }
        }
        return results;
    }





}
