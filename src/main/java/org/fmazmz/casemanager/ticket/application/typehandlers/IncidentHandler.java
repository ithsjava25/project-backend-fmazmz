package org.fmazmz.casemanager.ticket.application.typehandlers;

import org.fmazmz.casemanager.ticket.domain.Priority;
import org.fmazmz.casemanager.ticket.domain.Ticket;
import org.fmazmz.casemanager.ticket.domain.TicketType;
import org.springframework.stereotype.Service;

@Service
public class IncidentHandler implements TypeHandler {
    @Override
    public TicketType supports() {
        return TicketType.INCIDENT;
    }

    @Override
    public void applyDefaults(Ticket ticket) {
        if (ticket.getPriority() == null) ticket.setPriority(Priority.P3);
    }
}
