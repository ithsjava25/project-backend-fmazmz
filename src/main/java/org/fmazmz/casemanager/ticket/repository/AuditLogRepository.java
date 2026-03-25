package org.fmazmz.casemanager.ticket.repository;

import org.fmazmz.casemanager.ticket.model.AuditLog;
import org.fmazmz.casemanager.ticket.model.TicketAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findAllByTicketId(UUID ticketId);
    List<AuditLog> findAllByUserId(UUID userId);
    List<AuditLog> findAllByAction(TicketAction action);
}
