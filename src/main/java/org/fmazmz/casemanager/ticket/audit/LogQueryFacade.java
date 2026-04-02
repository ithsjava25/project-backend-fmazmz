package org.fmazmz.casemanager.ticket.audit;

import org.fmazmz.casemanager.exception.AccessDeniedException;
import org.fmazmz.casemanager.ticket.dto.LogJournal;
import org.fmazmz.casemanager.ticket.mapper.AuditLogMapper;
import org.fmazmz.casemanager.ticket.model.AuditLog;
import org.fmazmz.casemanager.ticket.model.TicketAction;
import org.fmazmz.casemanager.ticket.repository.AuditLogRepository;
import org.fmazmz.casemanager.ticket.workflow.PermissionEvaluator;
import org.fmazmz.casemanager.user.model.User;
import org.fmazmz.casemanager.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<LogJournal> getAllTicketLogJournals(UUID actorId) {
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!permissionEvaluator.hasPermission(actor, TicketAction.LOG_READ)) {
            throw new AccessDeniedException("User is not authorized to perform action: "+ TicketAction.LOG_READ);

        }
        List<AuditLog> logs = repository.findAll();

        return logs.stream()
                .map(AuditLogMapper::toDto)
                .toList();
    }

    public List<LogJournal> getAllLogJournalsByTicketId(UUID actorId, UUID ticketId) {
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!permissionEvaluator.hasPermission(actor, TicketAction.LOG_READ)) {
            throw new AccessDeniedException("User is not authorized to perform action: "+ TicketAction.LOG_READ);

        }
        return repository.findAllByTicketId(ticketId)
                .stream()
                .map(AuditLogMapper::toDto)
                .toList();
    }
}
