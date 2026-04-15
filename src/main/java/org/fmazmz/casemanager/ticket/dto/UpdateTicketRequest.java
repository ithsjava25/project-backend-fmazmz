package org.fmazmz.casemanager.ticket.dto;

import jakarta.validation.constraints.Size;

public record UpdateTicketRequest(
        @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
        String title,
        @Size(min = 5, max = 5000, message = "Description must be between 5 and 5000 characters")
        String description
) {
}
