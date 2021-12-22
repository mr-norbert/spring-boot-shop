package bnorbert.onlineshop.config;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.security.AbstractAuthorizationAuditListener;
import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExposeAttemptedPathAuthorizationAuditListener extends AbstractAuthorizationAuditListener {

    public static final String AUTHORIZATION_FAILURE = "AUTHORIZATION_FAILURE";

    @Override
    public void onApplicationEvent(AbstractAuthorizationEvent event) {
        if (event instanceof AuthorizationFailureEvent) {
            onAuthorizationFailureEvent((AuthorizationFailureEvent) event);
        }
    }

    private void onAuthorizationFailureEvent(AuthorizationFailureEvent event) {
        Map<String, Object> data = new HashMap<>();

        data.put(AUTHORIZATION_FAILURE, event.getAccessDeniedException().getMessage());
        if (event.getAuthentication().getDetails() != null) {
            data.put("details", event.getAuthentication().getDetails());
        }
        publish(new AuditEvent(event.getAuthentication().getName(), AUTHORIZATION_FAILURE,
                data));

    }



}
