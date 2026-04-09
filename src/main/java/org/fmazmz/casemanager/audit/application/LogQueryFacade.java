package org.fmazmz.casemanager.audit.application;

import org.fmazmz.casemanager.exception.AccessDeniedException;
import org.fmazmz.casemanager.audit.dto.AuditLogEntry;
import org.fmazmz.casemanager.audit.mapper.AuditLogMapper;
import org.fmazmz.casemanager.audit.domain.AuditLog;
import org.fmazmz.casemanager.ticket.domain.TicketAction;
import org.fmazmz.casemanager.ticket.repository.AuditLogRepository;
import org.fmazmz.casemanager.ticket.application.workflow.PermissionEvaluator;
import org.fmazmz.casemanager.user.application.UserLookupService;
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
    private final UserLookupService userLookupService;

    public LogQueryFacade(AuditLogRepository repository, PermissionEvaluator permissionEvaluator, UserLookupService userLookupService) {
        this.repository = repository;
        this.permissionEvaluator = permissionEvaluator;
        this.userLookupService = userLookupService;
    }

    public PagedResult<AuditLogEntry> getAllTicketLogJournals(UUID actorId, Pageable pageable) {
        User actor = userLookupService.requireActor(actorId);
        requireRead(actor);

        Page<AuditLog> page = repository.findAll(pageable);
        Page<AuditLogEntry> mapped = page.map(AuditLogMapper::toDto);
        return PagedResult.from(mapped);
    }

    public PagedResult<AuditLogEntry> getAllLogJournalsByTicketId(UUID actorId, UUID ticketId, Pageable pageable) {
        User actor = userLookupService.requireActor(actorId);
        requireRead(actor);

        Page<AuditLog> page = repository.findAllByTicketId(ticketId, pageable);
        Page<AuditLogEntry> mapped = page.map(AuditLogMapper::toDto);
        return PagedResult.from(mapped);
    }

    private void requireRead(User actor) {
        if (!permissionEvaluator.hasPermission(actor, TicketAction.LOG_READ)) {
            throw new AccessDeniedException("User is not authorized to perform action: " + TicketAction.LOG_READ);
        }
    }
}
