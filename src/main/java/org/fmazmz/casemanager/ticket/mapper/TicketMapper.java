package org.fmazmz.casemanager.ticket.mapper;

import org.fmazmz.casemanager.ticket.model.Ticket;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public static TicketResponse toDto(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getNumber(),
                ticket.getType(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getRequester().getId(),
                ticket.getAssignee() != null ? ticket.getAssignee().getId() : null,
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}
