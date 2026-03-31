package org.fmazmz.casemanager.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.fmazmz.casemanager.ticket.model.TicketType;

public record CreateTicketRequest(
        @NotBlank
        @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
        String title,

        @NotBlank
        @Size(max = 5000, message = "Description must be between 0 and 255 characters")
        String description,

        @NotNull
        TicketType type
) {
}
