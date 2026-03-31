package org.fmazmz.casemanager.ticket.orchestration;

import org.fmazmz.casemanager.exception.AccessDeniedException;
import org.fmazmz.casemanager.ticket.audit.AuditLogWriter;
import org.fmazmz.casemanager.ticket.dto.ChangeTicketStatusRequest;
import org.fmazmz.casemanager.ticket.mapper.TicketMapper;
import org.fmazmz.casemanager.ticket.model.Comment;
import org.fmazmz.casemanager.ticket.model.CommentVisibility;
import org.fmazmz.casemanager.ticket.model.Ticket;
import org.fmazmz.casemanager.ticket.model.TicketAction;
import org.fmazmz.casemanager.ticket.model.TicketStatus;
import org.fmazmz.casemanager.ticket.dto.CreateTicketRequest;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.fmazmz.casemanager.ticket.orchestration.typehandlers.TypeHandler;
import org.fmazmz.casemanager.ticket.orchestration.typehandlers.TypeHandlerFactory;
import org.fmazmz.casemanager.ticket.repository.CommentRepository;
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

    private final TicketNumberGenerator numberGenerator;
    private final PermissionEvaluator permissionEvaluator;
    private final AuditLogWriter auditLogWriter;
    private final TypeHandlerFactory typeHandlerFactory;
    private final TicketWorkflowValidator workflowValidator;
    private final CommentRepository commentRepository;

    public TicketOrchestrator(TicketRepository ticketRepository, UserRepository userRepository,
                              TicketNumberGenerator numberGenerator, PermissionEvaluator permissionEvaluator,
                              AuditLogWriter auditLogWriter, TypeHandlerFactory typeHandlerFactory,
                              TicketWorkflowValidator workflowValidator, CommentRepository commentRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.numberGenerator = numberGenerator;
        this.permissionEvaluator = permissionEvaluator;
        this.auditLogWriter = auditLogWriter;
        this.typeHandlerFactory = typeHandlerFactory;
        this.workflowValidator = workflowValidator;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request, UUID requesterId) {
        User actor = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!permissionEvaluator.hasPermission(actor, TicketAction.CREATE)) {
            throw new AccessDeniedException("User is not authorized to perform action: " + TicketAction.CREATE);
        }

        TypeHandler typeHandler = typeHandlerFactory.resolve(request.type());

        Ticket ticket = new Ticket();
        typeHandler.applyDefaults(ticket);

        String ticketNumber = numberGenerator.generate(request.type());

        ticket.setNumber(ticketNumber);
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

    @Transactional
    public TicketResponse changeStatus(UUID ticketId, ChangeTicketStatusRequest request, UUID actorId) {
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        TicketStatus fromStatus = ticket.getStatus();
        TicketStatus toStatus = request.status();

        if (!permissionEvaluator.hasPermission(actor, TicketAction.CHANGE_STATUS)) {
            throw new AccessDeniedException(
                    "User is not authorized to perform transition " + fromStatus + " -> " + toStatus
            );
        }

        workflowValidator.validateRequiredTransitionFields(toStatus, request);

        if (toStatus == TicketStatus.WORK_IN_PROGRESS) {
            User assignee = userRepository.findById(request.assignee())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee user not found"));
            ticket.setAssignee(assignee);

            Comment internalComment = new Comment();
            internalComment.setTicket(ticket);
            internalComment.setUser(actor);
            internalComment.setVisibility(CommentVisibility.INTERNAL);
            internalComment.setMessage(request.internalComment().trim());
            commentRepository.save(internalComment);
        }

        if (toStatus == TicketStatus.AWAITING_USER_INFO) {
            Comment comment = new Comment();
            comment.setTicket(ticket);
            comment.setUser(actor);
            comment.setVisibility(CommentVisibility.PUBLIC);
            comment.setMessage(request.publicComment().trim());
            commentRepository.save(comment);
        }

        String oldStatus = fromStatus.name();
        ticket.setStatus(toStatus);

        if (toStatus == TicketStatus.RESOLVED) {
            ticket.setResolutionNotes(request.resolutionNotes().trim());
        }

        Ticket saved = ticketRepository.saveAndFlush(ticket);

        auditLogWriter.logChange(saved, actor, TicketAction.CHANGE_STATUS, "status", oldStatus, toStatus.name());

        return TicketMapper.toDto(saved);
    }
}
