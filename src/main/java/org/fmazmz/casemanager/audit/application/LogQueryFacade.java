package org.fmazmz.casemanager.audit.application;

import org.fmazmz.casemanager.exception.AccessDeniedException;
import org.fmazmz.casemanager.audit.dto.AuditLogEntry;
import org.fmazmz.casemanager.audit.mapper.AuditLogMapper;
import org.fmazmz.casemanager.audit.domain.AuditLog;
import org.fmazmz.casemanager.ticket.domain.TicketAction;
import org.fmazmz.casemanager.ticket.repository.AuditLogRepository;
import org.fmazmz.casemanager.ticket.application.workflow.PermissionEvaluator;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.repository.UserRepository;
import org.fmazmz.casemanager.common.pagination.PagedResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LogQueryFacade {
    private final AuditLogRepository repository;
    private final PermissionEvaluator permissionEvaluator;
    private final UserRepository userRepository;

    public LogQueryFacade(AuditLogRepository auditLogRepository, PermissionEvaluator evaluator, UserRepository userRepository) {
        this.repository = auditLogRepository;
        this.permissionEvaluator = evaluator;
        this.userRepository = userRepository;
    }

    public PagedResult<AuditLogEntry> getAllTicketLogJournals(UUID actorId, Pageable pageable) {
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!permissionEvaluator.hasPermission(actor, TicketAction.LOG_READ)) {
            throw new AccessDeniedException("User is not authorized to perform action: " + TicketAction.LOG_READ);
        }

        Page<AuditLog> page = repository.findAll(pageable);
        Page<AuditLogEntry> mapped = page.map(AuditLogMapper::toDto);
        return PagedResult.from(mapped);
    }

    public PagedResult<AuditLogEntry> getAllLogJournalsByTicketId(UUID actorId, UUID ticketId, Pageable pageable) {
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!permissionEvaluator.hasPermission(actor, TicketAction.LOG_READ)) {
            throw new AccessDeniedException("User is not authorized to perform action: " + TicketAction.LOG_READ);
        }

        Page<AuditLog> page = repository.findAllByTicketId(ticketId, pageable);
        Page<AuditLogEntry> mapped = page.map(AuditLogMapper::toDto);
        return PagedResult.from(mapped);
    }
}
