package bnorbert.onlineshop.transfer.audit;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter

public class AuditResponse {
    private Long id;
    private String principal;
    private LocalDate localDate;
    private LocalDateTime auditEventDate;
    private String auditEventType;
    private Map<String, String> metadata = new HashMap<>();
}
