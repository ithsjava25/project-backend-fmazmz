package org.fmazmz.casemanager.ticket.dto;

import org.fmazmz.casemanager.ticket.model.TicketAction;

import java.time.Instant;
import java.util.UUID;

public record LogJournal(
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
