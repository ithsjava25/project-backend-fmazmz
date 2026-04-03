package org.fmazmz.casemanager.ticket.dto;

import org.fmazmz.casemanager.ticket.domain.Comment;
import org.fmazmz.casemanager.ticket.domain.Priority;
import org.fmazmz.casemanager.ticket.domain.TicketStatus;
import org.fmazmz.casemanager.ticket.domain.TicketType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        String number,
        TicketType type,
        String title,
        String description,
        List<Comment> comments,
        String resolutionNotes,
        UUID requesterId,
        UUID assigneeId,
        TicketStatus status,
        Priority priority,
        Instant createdAt,
        Instant updatedAt
) {
}
