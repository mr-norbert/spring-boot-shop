package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.PersistentAuditEvent;
import bnorbert.onlineshop.transfer.audit.AuditResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class AuditMapper {

    public abstract List<AuditResponse> entitiesToEntityDTOs(List<PersistentAuditEvent> persistentAuditEvents);
}
