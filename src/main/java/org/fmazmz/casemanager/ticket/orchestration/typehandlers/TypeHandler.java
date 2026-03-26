package org.fmazmz.casemanager.ticket.orchestration.typehandlers;

import org.fmazmz.casemanager.ticket.model.Ticket;
import org.fmazmz.casemanager.ticket.model.TicketType;

public interface TypeHandler {
    TicketType supports();

    void applyDefaults(Ticket ticket);
}
