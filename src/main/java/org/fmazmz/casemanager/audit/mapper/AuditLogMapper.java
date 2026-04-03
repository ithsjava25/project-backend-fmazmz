package org.fmazmz.casemanager.audit.mapper;

import org.fmazmz.casemanager.audit.dto.AuditLogEntry;
import org.fmazmz.casemanager.audit.domain.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {
    public static AuditLogEntry toDto(AuditLog log) {
        return new AuditLogEntry(
                log.getId(),
                log.getTicket().getId(),
                log.getUser().getEmail(),
                log.getAction(),
                log.getField(),
                log.getOldValue(),
                log.getNewValue(),
                log.getCreatedAt()
        );
    }
}
