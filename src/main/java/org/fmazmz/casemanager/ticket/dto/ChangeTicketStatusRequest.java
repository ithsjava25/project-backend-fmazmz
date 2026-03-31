package org.fmazmz.casemanager.ticket.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.fmazmz.casemanager.ticket.model.TicketStatus;

public record ChangeTicketStatusRequest(
        @NotNull
        TicketStatus status,
        @Size(max = 5000, message = "Public comment must be at most 5000 characters")
        String publicComment,
        @Size(max = 2000, message = "Resolution notes must be at most 2000 characters")
        String resolutionNotes
) {
}
