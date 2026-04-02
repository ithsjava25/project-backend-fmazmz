package org.fmazmz.casemanager.ticket.api;

import jakarta.validation.Valid;
import org.fmazmz.casemanager.ticket.dto.ChangeTicketStatusRequest;
import org.fmazmz.casemanager.ticket.dto.CreateTicketRequest;
import org.fmazmz.casemanager.ticket.dto.TicketCommentRequest;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.fmazmz.casemanager.ticket.orchestration.TicketOrchestrator;
import org.fmazmz.casemanager.user.auth.CurrentUser;
import org.fmazmz.casemanager.user.model.User;
import org.fmazmz.casemanager.utils.ApiResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TicketController implements TicketApi {

    private final TicketOrchestrator ticketOrchestrator;

    public TicketController(TicketOrchestrator ticketOrchestrator) {
        this.ticketOrchestrator = ticketOrchestrator;
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
