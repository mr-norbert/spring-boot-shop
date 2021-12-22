package bnorbert.onlineshop.domain;

import bnorbert.onlineshop.binder.UserMetadataBinder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.PropertyBinderRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.PropertyBinding;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Getter
@Setter
@Indexed
public class PersistentAuditEvent  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String principal;
    private LocalDate localDate;
    private LocalDateTime auditEventDate;
    private String auditEventType;
    private String fingerprints;

    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "persistent_audit_event_metadata", joinColumns = @JoinColumn(name="id"))
    @PropertyBinding(binder = @PropertyBinderRef(type = UserMetadataBinder.class))
    private Map<String, String> metadata = new LinkedHashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PersistentAuditEvent that = (PersistentAuditEvent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
