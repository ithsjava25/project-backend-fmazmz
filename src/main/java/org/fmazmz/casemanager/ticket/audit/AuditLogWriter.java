package org.fmazmz.casemanager.ticket.audit;

import org.fmazmz.casemanager.ticket.model.AuditLog;
import org.fmazmz.casemanager.ticket.model.Ticket;
import org.fmazmz.casemanager.ticket.model.TicketAction;
import org.fmazmz.casemanager.ticket.repository.AuditLogRepository;
import org.fmazmz.casemanager.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuditLogWriter {
    private final AuditLogRepository auditLogRepository;

    public AuditLogWriter(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logChange(Ticket ticket,
                          User actor,
                          TicketAction action,
                          String field,
                          String oldValue,
                          String newValue) {

        AuditLog event = new AuditLog();
        event.setTicket(ticket);
        event.setUser(actor);
        event.setField(field);
        event.setAction(action);
        event.setOldValue(oldValue);
        event.setNewValue(newValue);

        auditLogRepository.save(event);
    }
}
