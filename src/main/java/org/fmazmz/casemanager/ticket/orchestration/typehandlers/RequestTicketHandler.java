package org.fmazmz.casemanager.ticket.orchestration.typehandlers;

import org.fmazmz.casemanager.ticket.model.Priority;
import org.fmazmz.casemanager.ticket.model.Ticket;
import org.fmazmz.casemanager.ticket.model.TicketType;
import org.fmazmz.casemanager.ticket.model.dto.CreateTicketRequest;
import org.springframework.stereotype.Service;

@Service
public class RequestTicketHandler implements TicketTypeHandler {
    @Override
    public TicketType supports() {
        return TicketType.REQUEST;
    }

    @Override
    public void applyDefaults(CreateTicketRequest request, Ticket ticket) {
        if (ticket.getPriority() == null) ticket.setPriority(Priority.P5);
    }
}
