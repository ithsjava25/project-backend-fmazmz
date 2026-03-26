package org.fmazmz.casemanager.ticket.orchestration.typehandlers;

import org.fmazmz.casemanager.ticket.model.Priority;
import org.fmazmz.casemanager.ticket.model.Ticket;
import org.fmazmz.casemanager.ticket.model.TicketType;
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
