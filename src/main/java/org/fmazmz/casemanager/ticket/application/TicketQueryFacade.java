package org.fmazmz.casemanager.ticket.application;

import lombok.extern.slf4j.Slf4j;
import org.fmazmz.casemanager.storage.domain.PresignedGetObjectResult;
import org.fmazmz.casemanager.storage.domain.StorageService;
import org.fmazmz.casemanager.ticket.application.workflow.PermissionEvaluator;
import org.fmazmz.casemanager.ticket.domain.Attachment;
import org.fmazmz.casemanager.ticket.domain.Ticket;
import org.fmazmz.casemanager.ticket.domain.TicketAction;
import org.fmazmz.casemanager.ticket.dto.AttachmentViewUrlResponse;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.fmazmz.casemanager.ticket.mapper.TicketMapper;
import org.fmazmz.casemanager.ticket.repository.AttachmentRepository;
import org.fmazmz.casemanager.ticket.repository.TicketRepository;
import org.fmazmz.casemanager.user.application.UserLookupService;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.common.pagination.PagedResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class TicketQueryFacade {
    private static final int MAX_PRESIGN_MINUTES = 7 * 24 * 60;

    private final TicketRepository ticketRepository;
    private final PermissionEvaluator permissionEvaluator;
    private final UserLookupService userLookupService;
    private final AttachmentRepository attachmentRepository;
    private final StorageService storageService;
    private final int presignGetDurationMinutes;

    public TicketQueryFacade(
            TicketRepository ticketRepository,
            PermissionEvaluator permissionEvaluator,
            UserLookupService userLookupService,
            AttachmentRepository attachmentRepository,
            StorageService storageService,
            @Value("${app.storage.presign-get-duration-minutes:15}") int presignGetDurationMinutes
    ) {
        this.ticketRepository = ticketRepository;
        this.permissionEvaluator = permissionEvaluator;
        this.userLookupService = userLookupService;
        this.attachmentRepository = attachmentRepository;
        this.storageService = storageService;
        this.presignGetDurationMinutes = presignGetDurationMinutes;
    }

    @Transactional(readOnly = true)
    public PagedResult<TicketResponse> findAll(UUID actorId, Pageable pageable) {
        requireActorWithTicketRead(actorId);

        log.debug("Listing tickets: actorId={}, filter=all, page={}", actorId, pageable.getPageNumber());

        Page<Ticket> page = ticketRepository.findAll(pageable);
        return TicketMapper.mapPage(page);
    }

    @Transactional(readOnly = true)
    public TicketResponse findById(UUID actorId, UUID ticketId) {
        User actor = requireActorWithTicketRead(actorId);

        log.debug("Get ticket by id: actorId={}, ticketId={}", actorId, ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        return TicketMapper.toDto(ticket, permissionEvaluator.includeInternalComments(actor));
    }

    @Transactional(readOnly = true)
    public TicketResponse findByNumber(UUID actorId, String ticketNumber) {
        User actor = requireActorWithTicketRead(actorId);

        if (!StringUtils.hasText(ticketNumber)) {
            throw new IllegalArgumentException("Ticket number is required");
        }

        log.debug("Get ticket by number: actorId={}, ticketNumber={}", actorId, ticketNumber);

        Ticket ticket = ticketRepository.findByNumber(ticketNumber.trim())
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with number: " + ticketNumber.trim()));

        return TicketMapper.toDto(ticket, permissionEvaluator.includeInternalComments(actor));
    }

    @Transactional(readOnly = true)
    public PagedResult<TicketResponse> findByRequesterId(UUID actorId, UUID requesterId, Pageable pageable) {
        requireActorWithTicketRead(actorId);

        log.debug("Listing tickets: actorId={}, filter=requesterId={}, page={}", actorId, requesterId, pageable.getPageNumber());

        Page<Ticket> page = ticketRepository.findByRequester_Id(requesterId, pageable);
        return TicketMapper.mapPage(page);
    }

    @Transactional(readOnly = true)
    public PagedResult<TicketResponse> findByAssigneeId(UUID actorId, UUID assigneeId, Pageable pageable) {
        requireActorWithTicketRead(actorId);

        log.debug("Listing tickets: actorId={}, filter=assigneeId={}, page={}", actorId, assigneeId, pageable.getPageNumber());

        Page<Ticket> page = ticketRepository.findByAssignee_Id(assigneeId, pageable);
        return TicketMapper.mapPage(page);
    }

    @Transactional(readOnly = true)
    public PagedResult<TicketResponse> findByAssignmentGroupId(UUID actorId, UUID assignmentGroupId, Pageable pageable) {
        requireActorWithTicketRead(actorId);

        log.debug("Listing tickets: actorId={}, filter=assignmentGroupId={}, page={}", actorId, assignmentGroupId, pageable.getPageNumber());

        Page<Ticket> page = ticketRepository.findByAssignmentGroup_Id(assignmentGroupId, pageable);
        return TicketMapper.mapPage(page);
    }

    /**
     * Returns a time-limited URL to read the file (S3 pre-signed GET; use from the frontend as {@code href} or
     * {@code window.open}, not embedded as a permanent API secret). Requires {@link TicketAction#READ}.
     */
    @Transactional(readOnly = true)
    public AttachmentViewUrlResponse getAttachmentViewUrl(UUID actorId, UUID ticketId, UUID attachmentId) {
        requireActorWithTicketRead(actorId);

        Attachment attachment = attachmentRepository.findByIdAndTicket_Id(attachmentId, ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found for this ticket"));

        int minutes = Math.max(1, Math.min(presignGetDurationMinutes, MAX_PRESIGN_MINUTES));
        Duration validity = Duration.ofMinutes(minutes);

        log.info(
                "Issuing pre-signed read URL (S3) for attachment: ticketId={}, attachmentId={}, actorId={}, validityMinutes={}",
                ticketId,
                attachmentId,
                actorId,
                minutes
        );

        PresignedGetObjectResult result = storageService.presignGetObject(
                attachment.getStorageKey(),
                validity,
                attachment.getFileName(),
                attachment.getContentType()
        );
        return new AttachmentViewUrlResponse(result.url(), result.expiresAt());
    }

    private User requireActorWithTicketRead(UUID actorId) {
        User actor = userLookupService.requireActor(actorId);
        permissionEvaluator.requirePermission(actor, TicketAction.READ);
        return actor;
    }
}
