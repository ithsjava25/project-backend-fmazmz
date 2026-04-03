package org.fmazmz.casemanager.ticket.application.typehandlers;

import org.fmazmz.casemanager.ticket.domain.Ticket;
import org.fmazmz.casemanager.ticket.domain.TicketType;

public interface TypeHandler {
    TicketType supports();

    void applyDefaults(Ticket ticket);
}
