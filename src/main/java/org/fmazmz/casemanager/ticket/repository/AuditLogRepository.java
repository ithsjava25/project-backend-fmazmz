package org.fmazmz.casemanager.ticket.repository;

import org.fmazmz.casemanager.audit.domain.AuditLog;
import org.fmazmz.casemanager.ticket.domain.TicketAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findAllByTicketId(UUID ticketId, Pageable pageable);

    List<AuditLog> findAllByUserId(UUID userId);
    List<AuditLog> findAllByAction(TicketAction action);
}
