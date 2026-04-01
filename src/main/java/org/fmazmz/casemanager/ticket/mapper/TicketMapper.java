package org.fmazmz.casemanager.ticket.mapper;

import org.fmazmz.casemanager.ticket.model.Comment;
import org.fmazmz.casemanager.ticket.model.CommentVisibility;
import org.fmazmz.casemanager.ticket.model.Ticket;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class TicketMapper {

    public static TicketResponse toDto(Ticket ticket, boolean includeInternalComments) {
        var comments = ticket.getComments().stream()
                .filter(c -> includeInternalComments || c.getVisibility() == CommentVisibility.PUBLIC)
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .toList();
        return new TicketResponse(
                ticket.getId(),
                ticket.getNumber(),
                ticket.getType(),
                ticket.getTitle(),
                ticket.getDescription(),
                comments,
                ticket.getResolutionNotes(),
                ticket.getRequester().getId(),
                ticket.getAssignee() != null ? ticket.getAssignee().getId() : null,
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}
