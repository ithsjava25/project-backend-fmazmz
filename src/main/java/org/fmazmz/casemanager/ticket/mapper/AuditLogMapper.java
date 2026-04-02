package org.fmazmz.casemanager.ticket.mapper;

import org.fmazmz.casemanager.ticket.dto.LogJournal;
import org.fmazmz.casemanager.ticket.model.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {
    public static LogJournal toDto(AuditLog log) {
        return new LogJournal(
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
