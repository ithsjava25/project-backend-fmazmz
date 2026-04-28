package org.fmazmz.casemanager.ticket.application;

import org.fmazmz.casemanager.exception.AccessDeniedException;
import org.fmazmz.casemanager.audit.application.AuditLogWriter;
import org.fmazmz.casemanager.ticket.dto.UpdateTicketPriorityRequest;
import org.fmazmz.casemanager.ticket.dto.ChangeTicketStatusRequest;
import org.fmazmz.casemanager.ticket.dto.TicketCommentRequest;
import org.fmazmz.casemanager.ticket.dto.UpdateTicketRequest;
import org.fmazmz.casemanager.ticket.mapper.CommentMapper;
import org.fmazmz.casemanager.ticket.mapper.TicketMapper;
import org.fmazmz.casemanager.ticket.domain.Comment;
import org.fmazmz.casemanager.ticket.domain.CommentVisibility;
import org.fmazmz.casemanager.ticket.domain.Priority;
import org.fmazmz.casemanager.ticket.domain.Ticket;
import org.fmazmz.casemanager.ticket.domain.TicketAction;
import org.fmazmz.casemanager.ticket.domain.TicketStatus;
import org.fmazmz.casemanager.ticket.dto.CreateTicketRequest;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.fmazmz.casemanager.ticket.application.typehandlers.TypeHandler;
import org.fmazmz.casemanager.ticket.application.typehandlers.TypeHandlerFactory;
import org.fmazmz.casemanager.assignmentgroup.domain.AssignmentGroup;
import org.fmazmz.casemanager.assignmentgroup.repository.AssignmentGroupRepository;
import org.fmazmz.casemanager.ticket.repository.CommentRepository;
import org.fmazmz.casemanager.ticket.repository.TicketRepository;
import org.fmazmz.casemanager.ticket.repository.AttachmentRepository;
import org.fmazmz.casemanager.ticket.application.workflow.PermissionEvaluator;
import org.fmazmz.casemanager.ticket.application.workflow.TicketWorkflowValidator;
import org.fmazmz.casemanager.storage.domain.StorageObject;
import org.fmazmz.casemanager.storage.domain.StorageService;
import org.fmazmz.casemanager.storage.domain.StoreObjectRequest;
import org.fmazmz.casemanager.ticket.domain.Attachment;
import org.fmazmz.casemanager.user.application.UserLookupService;
import org.fmazmz.casemanager.user.domain.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class TicketOrchestrator {
    private final TicketRepository ticketRepository;

    private final UserLookupService userLookupService;
    private final TicketNumberGenerator numberGenerator;
    private final PermissionEvaluator permissionEvaluator;
    private final AuditLogWriter auditLogWriter;
    private final TypeHandlerFactory typeHandlerFactory;
    private final TicketWorkflowValidator workflowValidator;
    private final CommentRepository commentRepository;
    private final AssignmentGroupRepository assignmentGroupRepository;
    private final AttachmentRepository attachmentRepository;
    private final StorageService storageService;

    public TicketOrchestrator(TicketRepository ticketRepository, UserLookupService userLookupService,
                              TicketNumberGenerator numberGenerator, PermissionEvaluator permissionEvaluator,
                              AuditLogWriter auditLogWriter, TypeHandlerFactory typeHandlerFactory,
                              TicketWorkflowValidator workflowValidator, CommentRepository commentRepository,
                              AssignmentGroupRepository assignmentGroupRepository,
                              AttachmentRepository attachmentRepository,
                              StorageService storageService) {
        this.ticketRepository = ticketRepository;
        this.userLookupService = userLookupService;
        this.numberGenerator = numberGenerator;
        this.permissionEvaluator = permissionEvaluator;
        this.auditLogWriter = auditLogWriter;
        this.typeHandlerFactory = typeHandlerFactory;
        this.workflowValidator = workflowValidator;
        this.commentRepository = commentRepository;
        this.assignmentGroupRepository = assignmentGroupRepository;
        this.attachmentRepository = attachmentRepository;
        this.storageService = storageService;
    }

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request, UUID actorId) {
        User actor = userLookupService.requireActor(actorId);

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

        if (request.assignmentGroupId() != null) {
            AssignmentGroup group = assignmentGroupRepository.findById(request.assignmentGroupId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignment group not found"));
            ticket.setAssignmentGroup(group);
        }

        Ticket saved = ticketRepository.saveAndFlush(ticket);

        auditLogWriter.logChange(
                saved,
                actor,
                TicketAction.CREATE,
                "status",
                null,
                TicketStatus.OPEN.name()
        );
        
        return TicketMapper.toDto(saved, permissionEvaluator.includeInternalComments(actor));
    }

    @Transactional
    public TicketResponse changeStatus(UUID ticketId, ChangeTicketStatusRequest request, UUID actorId) {
        User actor = userLookupService.requireActor(actorId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        requireAgentScopeForTicketUpdate(actor, ticket);

        TicketStatus fromStatus = ticket.getStatus();
        TicketStatus toStatus = request.status();

        UUID currentGroupId = ticket.getAssignmentGroup() != null ? ticket.getAssignmentGroup().getId() : null;
        UUID currentAssigneeId = ticket.getAssignee() != null ? ticket.getAssignee().getId() : null;

        boolean assignmentGroupChanged = !Objects.equals(currentGroupId, request.assignmentGroup());
        boolean assigneeChanged = !Objects.equals(currentAssigneeId, request.assignee());

        if (fromStatus == toStatus && (assignmentGroupChanged || assigneeChanged)) {
            if (!permissionEvaluator.hasPermission(actor, TicketAction.ASSIGN)) {
                throw new AccessDeniedException("User is not authorized to perform action: " + TicketAction.ASSIGN);
            }
            if (request.internalComment() == null || request.internalComment().isBlank()) {
                throw new IllegalArgumentException("Assignment updates require an internal comment");
            }

            if (request.assignee() != null) {
                UUID targetGroupId = request.assignmentGroup() != null ? request.assignmentGroup() : currentGroupId;
                if (targetGroupId == null) {
                    throw new IllegalArgumentException("Assignee requires an assignment group");
                }
                TicketWorkflowValidator.ResolvedAssignment resolved = workflowValidator.resolveAssignmentOrThrow(
                        new ChangeTicketStatusRequest(
                                request.status(),
                                targetGroupId,
                                request.assignee(),
                                null,
                                request.internalComment(),
                                null
                        )
                );
                ticket.setAssignmentGroup(resolved.group());
                ticket.setAssignee(resolved.assignee());
            } else if (request.assignmentGroup() != null) {
                AssignmentGroup group = assignmentGroupRepository.findById(request.assignmentGroup())
                        .orElseThrow(() -> new IllegalArgumentException("Assignment group not found"));
                ticket.setAssignmentGroup(group);
                ticket.setAssignee(null);
            } else {
                ticket.setAssignmentGroup(null);
                ticket.setAssignee(null);
            }

            Comment internalComment = new Comment();
            internalComment.setTicket(ticket);
            internalComment.setUser(actor);
            internalComment.setVisibility(CommentVisibility.INTERNAL);
            internalComment.setMessage(request.internalComment().trim());
            commentRepository.save(internalComment);

            Ticket saved = ticketRepository.saveAndFlush(ticket);
            if (assignmentGroupChanged) {
                auditLogWriter.logChange(
                        saved,
                        actor,
                        TicketAction.ASSIGN,
                        "assignmentGroup",
                        currentGroupId != null ? currentGroupId.toString() : null,
                        request.assignmentGroup() != null ? request.assignmentGroup().toString() : null
                );
            }
            if (assigneeChanged) {
                auditLogWriter.logChange(
                        saved,
                        actor,
                        TicketAction.ASSIGN,
                        "assignee",
                        currentAssigneeId != null ? currentAssigneeId.toString() : null,
                        request.assignee() != null ? request.assignee().toString() : null
                );
            }
            return TicketMapper.toDto(saved, permissionEvaluator.includeInternalComments(actor));
        }

        if (fromStatus == TicketStatus.CLOSED) {
            throw new IllegalArgumentException(
                    "Closed tickets are final; create a new ticket if work needs to continue."
            );
        }

        String requiredPermissionName = workflowValidator.requiredPermissionName(fromStatus, toStatus);
        if (!permissionEvaluator.hasPermission(actor, requiredPermissionName)) {
            throw new AccessDeniedException(
                    "User is not authorized for transition " + fromStatus + " -> " + toStatus
                            + " (required permission: " + requiredPermissionName + ")"
            );
        }

        if (TicketAction.REOPEN.permissionName().equals(requiredPermissionName)) {
            boolean staff = permissionEvaluator.hasPermission(actor, TicketAction.CHANGE_STATUS)
                    || permissionEvaluator.hasPermission(actor, TicketAction.ASSIGN);
            if (!staff && !ticket.getRequester().getId().equals(actor.getId())) {
                throw new AccessDeniedException(
                        "Only the requester or service staff can reopen this ticket"
                );
            }
        }

        workflowValidator.validateRequiredTransitionFields(toStatus, request);

        if (toStatus == TicketStatus.ASSIGNED || toStatus == TicketStatus.WORK_IN_PROGRESS) {
            TicketWorkflowValidator.ResolvedAssignment resolved = workflowValidator.resolveAssignmentOrThrow(request);
            ticket.setAssignmentGroup(resolved.group());
            ticket.setAssignee(resolved.assignee());
        }

        if (toStatus == TicketStatus.WORK_IN_PROGRESS) {
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

        TicketAction auditAction = TicketAction.fromPermissionName(requiredPermissionName);
        auditLogWriter.logChange(saved, actor, auditAction, "status", oldStatus, toStatus.name());

        return TicketMapper.toDto(saved, permissionEvaluator.includeInternalComments(actor));
    }

    @Transactional
    public TicketResponse changePriority(UUID ticketId, UpdateTicketPriorityRequest request, UUID actorId) {
        User actor = userLookupService.requireActor(actorId);

        if (!permissionEvaluator.hasPermission(actor, TicketAction.CHANGE_PRIORITY)) {
            throw new AccessDeniedException("User is not authorized to perform action: " + TicketAction.CHANGE_PRIORITY);
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        requireAgentScopeForTicketUpdate(actor, ticket);

        Priority fromPriority = ticket.getPriority();
        Priority toPriority = request.priority();

        if (fromPriority == toPriority) {
            return TicketMapper.toDto(ticket, permissionEvaluator.includeInternalComments(actor));
        }

        Comment internalComment = new Comment();
        internalComment.setTicket(ticket);
        internalComment.setUser(actor);
        internalComment.setVisibility(CommentVisibility.INTERNAL);
        internalComment.setMessage(request.internalComment().trim());
        commentRepository.save(internalComment);

        ticket.setPriority(toPriority);
        Ticket saved = ticketRepository.saveAndFlush(ticket);

        auditLogWriter.logChange(
                saved,
                actor,
                TicketAction.CHANGE_PRIORITY,
                "priority",
                fromPriority != null ? fromPriority.name() : null,
                toPriority.name()
        );

        return TicketMapper.toDto(saved, permissionEvaluator.includeInternalComments(actor));
    }

    @Transactional
    public TicketResponse updateTicket(UUID ticketId, UpdateTicketRequest request, UUID actorId) {
        User actor = userLookupService.requireActor(actorId);

        if (!permissionEvaluator.hasPermission(actor, TicketAction.UPDATE)) {
            throw new AccessDeniedException("User is not authorized to perform action: " + TicketAction.UPDATE);
        }

        if (request.title() == null && request.description() == null) {
            throw new IllegalArgumentException("At least one field must be provided: title or description");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        requireAgentScopeForTicketUpdate(actor, ticket);

        boolean changed = false;

        if (request.title() != null) {
            String newTitle = request.title().trim();
            if (!StringUtils.hasText(newTitle)) {
                throw new IllegalArgumentException("Title must not be blank");
            }
            if (!newTitle.equals(ticket.getTitle())) {
                String oldTitle = ticket.getTitle();
                ticket.setTitle(newTitle);
                auditLogWriter.logChange(ticket, actor, TicketAction.UPDATE, "title", oldTitle, newTitle);
                changed = true;
            }
        }

        if (request.description() != null) {
            String newDescription = request.description().trim();
            if (!StringUtils.hasText(newDescription)) {
                throw new IllegalArgumentException("Description must not be blank");
            }
            if (!newDescription.equals(ticket.getDescription())) {
                String oldDescription = ticket.getDescription();
                ticket.setDescription(newDescription);
                auditLogWriter.logChange(ticket, actor, TicketAction.UPDATE, "description", oldDescription, newDescription);
                changed = true;
            }
        }

        if (!changed) {
            return TicketMapper.toDto(ticket, permissionEvaluator.includeInternalComments(actor));
        }

        Ticket saved = ticketRepository.saveAndFlush(ticket);
        return TicketMapper.toDto(saved, permissionEvaluator.includeInternalComments(actor));
    }

    private void requireAgentScopeForTicketUpdate(User actor, Ticket ticket) {
        if (!permissionEvaluator.hasRole(actor, "AGENT")) {
            return;
        }
        
        if (permissionEvaluator.hasRole(actor, "ADMIN") || permissionEvaluator.hasRole(actor, "SUPER_AGENT")) {
            return;
        }

        boolean isAssignee = ticket.getAssignee() != null && actor.getId().equals(ticket.getAssignee().getId());
        boolean isInAssignmentGroup = ticket.getAssignmentGroup() != null
                && assignmentGroupRepository.isUserMember(ticket.getAssignmentGroup().getId(), actor.getId());

        if (!isAssignee && !isInAssignmentGroup) {
            throw new AccessDeniedException(
                    "Agent can only update incidents assigned to them or to one of their assignment groups"
            );
        }
    }

    @Transactional
    public TicketResponse addComment(UUID ticketId, TicketCommentRequest request, UUID actorId) {
        User actor = userLookupService.requireActor(actorId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        TicketAction requiredAction = switch (request.visibility()) {
            case PUBLIC -> TicketAction.COMMENT_PUBLIC;
            case INTERNAL -> TicketAction.COMMENT_INTERNAL;
        };

        if (!permissionEvaluator.hasPermission(actor, requiredAction)) {
            throw new AccessDeniedException("User is not authorized to perform action: " + requiredAction);
        }

        Comment comment = CommentMapper.toComment(request, actor, ticket);
        Comment saved = commentRepository.saveAndFlush(comment);
        ticket.getComments().add(saved);

        return TicketMapper.toDto(ticket, permissionEvaluator.includeInternalComments(actor));
    }

    @Transactional
    public TicketResponse uploadAttachment(UUID ticketId, MultipartFile file, UUID actorId) {
        log.debug("Attachment upload started: ticketId={}, actorId={}", ticketId, actorId);

        User actor = userLookupService.requireActor(actorId);

        if (!permissionEvaluator.hasPermission(actor, TicketAction.UPLOAD_ATTACHMENT)) {
            log.warn(
                    "Attachment upload denied: missing permission ticket.upload_attachment; ticketId={}, actorId={}",
                    ticketId,
                    actorId
            );
            throw new AccessDeniedException("User is not authorized to perform action: " + TicketAction.UPLOAD_ATTACHMENT);
        }

        if (file == null || file.isEmpty()) {
            log.warn("Attachment upload rejected: empty file, ticketId={}, actorId={}", ticketId, actorId);
            throw new IllegalArgumentException("Attachment file must not be empty");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    log.warn("Attachment upload failed: ticket not found, ticketId={}, actorId={}", ticketId, actorId);
                    return new IllegalArgumentException("Ticket not found");
                });

        String originalFilename = StringUtils.hasText(file.getOriginalFilename())
                ? file.getOriginalFilename().trim()
                : "attachment";
        String contentType = StringUtils.hasText(file.getContentType())
                ? file.getContentType()
                : "application/octet-stream";
        String storageKey = "tickets/%s/%s-%s".formatted(ticket.getId(), UUID.randomUUID(), originalFilename);

        log.info(
                "Ticket attachment: preparing object storage upload ticketId={}, ticketNumber={}, actorId={}, fileName={}, sizeBytes={}, contentType={}, storageKey={}",
                ticket.getId(),
                ticket.getNumber(),
                actor.getId(),
                originalFilename,
                file.getSize(),
                contentType,
                storageKey
        );

        StorageObject uploaded;
        try {
            log.debug("Ticket attachment: calling StorageService.upload (S3 when app.storage.provider=s3) storageKey={}", storageKey);
            uploaded = storageService.upload(new StoreObjectRequest(
                    storageKey,
                    file.getInputStream(),
                    file.getSize(),
                    contentType
            ));
        } catch (IOException e) {
            log.error(
                    "Attachment upload failed while reading or uploading stream: ticketId={}, actorId={}, storageKey={}",
                    ticketId,
                    actorId,
                    storageKey,
                    e
            );
            throw new IllegalStateException("Failed to read attachment payload", e);
        }

        Attachment attachment = new Attachment();
        attachment.setTicket(ticket);
        attachment.setUploadedBy(actor);
        attachment.setFileName(originalFilename);
        attachment.setContentType(contentType);
        attachment.setFileSize(file.getSize());
        attachment.setStorageKey(uploaded.key());
        attachment.setUrl(uploaded.url());
        Attachment savedAttachment = attachmentRepository.saveAndFlush(attachment);
        ticket.getAttachments().add(savedAttachment);

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setUser(actor);
        comment.setVisibility(CommentVisibility.PUBLIC);
        comment.setMessage("Uploaded attachment: %s (%s)".formatted(savedAttachment.getFileName(), savedAttachment.getUrl()));
        Comment savedComment = commentRepository.saveAndFlush(comment);
        ticket.getComments().add(savedComment);

        auditLogWriter.logChange(
                ticket,
                actor,
                TicketAction.UPLOAD_ATTACHMENT,
                "attachment",
                null,
                savedAttachment.getStorageKey()
        );

        log.info(
                "Attachment upload completed: ticketId={}, ticketNumber={}, actorId={}, attachmentId={}, commentId={}, storageKey={}",
                ticket.getId(),
                ticket.getNumber(),
                actor.getId(),
                savedAttachment.getId(),
                savedComment.getId(),
                savedAttachment.getStorageKey()
        );

        return TicketMapper.toDto(ticket, permissionEvaluator.includeInternalComments(actor));
    }
}
