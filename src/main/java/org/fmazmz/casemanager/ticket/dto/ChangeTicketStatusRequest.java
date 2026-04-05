package org.fmazmz.casemanager.ticket.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.fmazmz.casemanager.ticket.domain.TicketStatus;

import java.util.UUID;

public record ChangeTicketStatusRequest(
        @NotNull
        TicketStatus status,
        UUID assignmentGroup,
        UUID assignee,
        @Size(max = 5000, message = "Public comment must be at most 5000 characters")
        String publicComment,
        @Size(max = 5000, message = "Internal comment must be at most 5000 characters")
        String internalComment,
        @Size(max = 2000, message = "Resolution notes must be at most 2000 characters")
        String resolutionNotes
) {
}
