package org.fmazmz.casemanager.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.fmazmz.casemanager.ticket.domain.CommentVisibility;

public record TicketCommentRequest(
        @NotNull
        CommentVisibility visibility,
        @NotBlank
        @Size(min = 1, max = 5000)
        String comment
) {
}
