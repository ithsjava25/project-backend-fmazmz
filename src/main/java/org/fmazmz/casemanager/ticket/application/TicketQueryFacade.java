package org.fmazmz.casemanager.ticket.application;

import lombok.extern.slf4j.Slf4j;
import org.fmazmz.casemanager.exception.AccessDeniedException;
import org.fmazmz.casemanager.ticket.application.workflow.PermissionEvaluator;
import org.fmazmz.casemanager.ticket.domain.Ticket;
import org.fmazmz.casemanager.ticket.domain.TicketAction;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.fmazmz.casemanager.ticket.mapper.TicketMapper;
import org.fmazmz.casemanager.ticket.repository.TicketRepository;
import org.fmazmz.casemanager.user.application.UserLookupService;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.common.pagination.PagedResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class TicketQueryFacade {
    private final TicketRepository ticketRepository;
    private final PermissionEvaluator permissionEvaluator;
    private final UserLookupService userLookupService;

    public TicketQueryFacade(TicketRepository ticketRepository, PermissionEvaluator permissionEvaluator, UserLookupService userLookupService) {
        this.ticketRepository = ticketRepository;
        this.permissionEvaluator = permissionEvaluator;
        this.userLookupService = userLookupService;
    }

    @Transactional(readOnly = true)
    public PagedResult<TicketResponse> findAll(UUID actorId, Pageable pageable) {
        User actor = userLookupService.requireActor(actorId);
        requireRead(actor);

        log.debug("Listing tickets: actorId={}, filter=all, page={}", actorId, pageable.getPageNumber());

        Page<Ticket> page = ticketRepository.findAll(pageable);
        return TicketMapper.mapPage(page);
    }

    @Transactional(readOnly = true)
    public PagedResult<TicketResponse> findByRequesterId(UUID actorId, UUID requesterId, Pageable pageable) {
        User actor = userLookupService.requireActor(actorId);
        requireRead(actor);

        log.debug("Listing tickets: actorId={}, filter=requesterId={}, page={}", actorId, requesterId, pageable.getPageNumber());

        Page<Ticket> page = ticketRepository.findByRequester_Id(requesterId, pageable);
        return TicketMapper.mapPage(page);
    }

    @Transactional(readOnly = true)
    public PagedResult<TicketResponse> findByAssigneeId(UUID actorId, UUID assigneeId, Pageable pageable) {
        User actor = userLookupService.requireActor(actorId);
        requireRead(actor);

        log.debug("Listing tickets: actorId={}, filter=assigneeId={}, page={}", actorId, assigneeId, pageable.getPageNumber());

        Page<Ticket> page = ticketRepository.findByAssignee_Id(assigneeId, pageable);
        return TicketMapper.mapPage(page);
    }

    @Transactional(readOnly = true)
    public PagedResult<TicketResponse> findByAssignmentGroupId(UUID actorId, UUID assignmentGroupId, Pageable pageable) {
        User actor = userLookupService.requireActor(actorId);
        requireRead(actor);

        log.debug("Listing tickets: actorId={}, filter=assignmentGroupId={}, page={}", actorId, assignmentGroupId, pageable.getPageNumber());

        Page<Ticket> page = ticketRepository.findByAssignmentGroup_Id(assignmentGroupId, pageable);
        return TicketMapper.mapPage(page);
    }


    private void requireRead(User actor) {
        TicketAction action = TicketAction.READ;
        if (!permissionEvaluator.hasPermission(actor, action)) {
            throw new AccessDeniedException("User is not authorized to perform action: " + action);
        }
    }
}
