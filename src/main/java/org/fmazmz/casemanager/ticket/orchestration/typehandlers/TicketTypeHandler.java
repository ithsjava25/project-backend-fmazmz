package org.fmazmz.casemanager.ticket.orchestration.typehandlers;

import org.fmazmz.casemanager.ticket.model.Ticket;
import org.fmazmz.casemanager.ticket.model.TicketType;
import org.fmazmz.casemanager.ticket.model.dto.CreateTicketRequest;

public interface TicketTypeHandler {
    TicketType supports();

    void applyDefaults(CreateTicketRequest request, Ticket ticket);
}
