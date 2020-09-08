package bnorbert.onlineshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter

public class PersistentAuditEvent  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String principal;
    private Instant auditEventDate;
    private String auditEventType;

    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "persistent_audit_event_data", joinColumns=@JoinColumn(name="event_id"))
    private Map<String, String> data = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)  return true;
        if (!(o instanceof PersistentAuditEvent)) { return false; }
        return id != null && id.equals(((PersistentAuditEvent) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }


}
