package org.fmazmz.casemanager.ticket.orchestration;

import org.fmazmz.casemanager.ticket.audit.AuditLogWriter;
import org.fmazmz.casemanager.ticket.mapper.TicketMapper;
import org.fmazmz.casemanager.ticket.model.Ticket;
import org.fmazmz.casemanager.ticket.model.TicketAction;
import org.fmazmz.casemanager.ticket.model.TicketStatus;
import org.fmazmz.casemanager.ticket.dto.CreateTicketRequest;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.fmazmz.casemanager.ticket.orchestration.typehandlers.TypeHandler;
import org.fmazmz.casemanager.ticket.orchestration.typehandlers.TypeHandlerFactory;
import org.fmazmz.casemanager.ticket.repository.TicketRepository;
import org.fmazmz.casemanager.ticket.workflow.PermissionEvaluator;
import org.fmazmz.casemanager.ticket.workflow.TicketWorkflowValidator;
import org.fmazmz.casemanager.user.model.User;
import org.fmazmz.casemanager.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TicketOrchestrator {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    private final TicketWorkflowValidator workflowValidator;
    private final PermissionEvaluator permissionEvaluator;
    private final AuditLogWriter auditLogWriter;
    private final TypeHandlerFactory typeHandlerFactory;

    public TicketOrchestrator(TicketRepository ticketRepository, UserRepository userRepository, TicketWorkflowValidator workflowValidator, PermissionEvaluator permissionEvaluator, AuditLogWriter auditLogWriter, TypeHandlerFactory typeHandlerFactory) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.workflowValidator = workflowValidator;
        this.permissionEvaluator = permissionEvaluator;
        this.auditLogWriter = auditLogWriter;
        this.typeHandlerFactory = typeHandlerFactory;
    }

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request, UUID requesterId) {
        User actor = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!permissionEvaluator.hasPermission(actor, "ticket.create")) {
            throw new SecurityException("User is not authorized to perform " + TicketAction.CREATE);
        }

        TypeHandler typeHandler = typeHandlerFactory.resolve(request.type());

        Ticket ticket = new Ticket();
        typeHandler.applyDefaults(ticket);

        // random UUID string until a number generator has been implemented
        ticket.setNumber(UUID.randomUUID().toString());
        ticket.setType(request.type());
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setRequester(actor);
        ticket.setStatus(TicketStatus.OPEN);

        Ticket saved = ticketRepository.saveAndFlush(ticket);

        auditLogWriter.logChange(
                saved,
                actor,
                TicketAction.CREATE,
                "status",
                null,
                TicketStatus.OPEN.name()
        );
        
        return TicketMapper.toDto(saved);
    }
}
