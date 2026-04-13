package org.fmazmz.casemanager.ticket.mapper;

import org.fmazmz.casemanager.common.pagination.PagedResult;
import org.fmazmz.casemanager.ticket.domain.Comment;
import org.fmazmz.casemanager.ticket.domain.CommentVisibility;
import org.fmazmz.casemanager.ticket.domain.Ticket;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class TicketMapper {

    public static TicketResponse toSummaryDto(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getNumber(),
                ticket.getType(),
                ticket.getTitle(),
                ticket.getDescription(),
                List.of(),
                ticket.getResolutionNotes(),
                ticket.getRequester().getId(),
                ticket.getAssignee() != null ? ticket.getAssignee().getId() : null,
                ticket.getAssignmentGroup() != null ? ticket.getAssignmentGroup().getId() : null,
                ticket.getAssignmentGroup() != null ? ticket.getAssignmentGroup().getName() : null,
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }

    public static TicketResponse toDto(Ticket ticket, boolean includeInternalComments) {
        var comments = ticket.getComments().stream()
                .filter(c -> includeInternalComments || c.getVisibility() == CommentVisibility.PUBLIC)
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(CommentMapper::toDto)
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
                ticket.getAssignmentGroup() != null ? ticket.getAssignmentGroup().getId() : null,
                ticket.getAssignmentGroup() != null ? ticket.getAssignmentGroup().getName() : null,
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }

    public static PagedResult<TicketResponse> mapPage(Page<Ticket> page) {
        Page<TicketResponse> mapped = page.map(TicketMapper::toSummaryDto);
        return PagedResult.from(mapped);
    }
}
