package bnorbert.onlineshop.transfer.audit;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class AuditResponse {
    private Long id;
    private String principal;
    private Instant auditEventDate;
    private String auditEventType;
    private Map<String, String> data = new HashMap<>();
}
