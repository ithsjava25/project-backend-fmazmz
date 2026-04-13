package org.fmazmz.casemanager.ticket.dto;

import org.fmazmz.casemanager.ticket.domain.CommentVisibility;

import java.time.Instant;
import java.util.UUID;

public record TicketCommentResponse(
        UUID id,
        CommentVisibility visibility,
        UUID ticketId,
        UUID authorId,
        String message,
        Instant createdAt
) {
}
