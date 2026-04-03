package org.fmazmz.casemanager.audit.dto;

import org.fmazmz.casemanager.ticket.domain.TicketAction;

import java.time.Instant;
import java.util.UUID;

public record AuditLogEntry(
        UUID id,
        UUID ticketId,
        String actorEmail,
        TicketAction ticketAction,
        String field,
        String oldValue,
        String newValue,
        Instant createdAt
) {
}
