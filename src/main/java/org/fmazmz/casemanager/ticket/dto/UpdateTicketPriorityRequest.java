package org.fmazmz.casemanager.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.fmazmz.casemanager.ticket.domain.Priority;

public record UpdateTicketPriorityRequest(
        @NotNull
        Priority priority,
        @NotBlank
        @Size(max = 5000, message = "Internal comment must be at most 5000 characters")
        String internalComment
) {
}
