package org.fmazmz.casemanager.ticket.http;

import jakarta.validation.Valid;
import org.fmazmz.casemanager.ticket.dto.ChangeTicketStatusRequest;
import org.fmazmz.casemanager.ticket.dto.CreateTicketRequest;
import org.fmazmz.casemanager.ticket.dto.TicketCommentRequest;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.fmazmz.casemanager.ticket.application.TicketOrchestrator;
import org.fmazmz.casemanager.ticket.application.TicketQueryFacade;
import org.fmazmz.casemanager.user.authentication.CurrentUser;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.common.api.ApiResponseWrapper;
import org.fmazmz.casemanager.common.pagination.PagedResult;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TicketController implements TicketApi {

    private final TicketOrchestrator ticketOrchestrator;
    private final TicketQueryFacade ticketQueryFacade;

    public TicketController(TicketOrchestrator ticketOrchestrator, TicketQueryFacade ticketQueryFacade) {
        this.ticketOrchestrator = ticketOrchestrator;
        this.ticketQueryFacade = ticketQueryFacade;
    }

    @GetMapping
    @Override
    public ResponseEntity<ApiResponseWrapper<PagedResult<TicketResponse>>> listTickets(
            @CurrentUser User actor,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(new ApiResponseWrapper<>(ticketQueryFacade.findAll(actor.getId(), pageable)));
    }

    @GetMapping("requester/{userId}")
    @Override
    public ResponseEntity<ApiResponseWrapper<PagedResult<TicketResponse>>> listTicketsByRequester(
            @CurrentUser User actor,
            @PathVariable UUID userId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(new ApiResponseWrapper<>(ticketQueryFacade.findByRequesterId(actor.getId(), userId, pageable)));
    }

    @GetMapping("assignee/{userId}")
    @Override
    public ResponseEntity<ApiResponseWrapper<PagedResult<TicketResponse>>> listTicketsByAssignee(
            @CurrentUser User actor,
            @PathVariable UUID userId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(new ApiResponseWrapper<>(ticketQueryFacade.findByAssigneeId(actor.getId(), userId, pageable)));
    }

    @GetMapping("assignment-group/{assignmentGroupId}")
    @Override
    public ResponseEntity<ApiResponseWrapper<PagedResult<TicketResponse>>> listTicketsByAssignmentGroup(
            @CurrentUser User actor,
            @PathVariable UUID assignmentGroupId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(new ApiResponseWrapper<>(ticketQueryFacade.findByAssignmentGroupId(actor.getId(), assignmentGroupId, pageable)));
    }

    @GetMapping("{ticketId}")
    @Override
    public ResponseEntity<ApiResponseWrapper<TicketResponse>> getTicket(
            @CurrentUser User actor,
            @PathVariable UUID ticketId) {

        return ResponseEntity.ok(new ApiResponseWrapper<>(ticketQueryFacade.findById(actor.getId(), ticketId)));
    }

    @GetMapping("number/{ticketNumber}")
    @Override
    public ResponseEntity<ApiResponseWrapper<TicketResponse>> getTicketByNumber(
            @CurrentUser User actor,
            @PathVariable String ticketNumber) {

        return ResponseEntity.ok(new ApiResponseWrapper<>(ticketQueryFacade.findByNumber(actor.getId(), ticketNumber)));
    }

    @PostMapping
    @Override
    public ResponseEntity<ApiResponseWrapper<TicketResponse>> createTicket(
            @CurrentUser User actor,
            @RequestBody @Valid CreateTicketRequest request) {

        TicketResponse response = ticketOrchestrator.createTicket(request, actor.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponseWrapper<>(response));
    }

    @PatchMapping("{ticketId}/status")
    @Override
    public ResponseEntity<ApiResponseWrapper<TicketResponse>> changeTicketStatus(
            @CurrentUser User actor,
            @PathVariable UUID ticketId,
            @RequestBody @Valid ChangeTicketStatusRequest request) {

        TicketResponse response = ticketOrchestrator.changeStatus(ticketId, request, actor.getId());

        return ResponseEntity.ok(new ApiResponseWrapper<>(response));
    }

    @PostMapping("{ticketId}/comment")
    @Override
    public ResponseEntity<ApiResponseWrapper<TicketResponse>> comment(
            @CurrentUser User actor,
            @PathVariable UUID ticketId,
            @RequestBody @Valid TicketCommentRequest request) {

        TicketResponse response = ticketOrchestrator.addComment(ticketId, request, actor.getId());

        return ResponseEntity.ok(new ApiResponseWrapper<>(response));
    }
}
