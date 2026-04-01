package org.fmazmz.casemanager.ticket.api;

import jakarta.validation.Valid;
import org.fmazmz.casemanager.ticket.dto.ChangeTicketStatusRequest;
import org.fmazmz.casemanager.ticket.dto.CreateTicketRequest;
import org.fmazmz.casemanager.ticket.dto.TicketCommentRequest;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.fmazmz.casemanager.ticket.orchestration.TicketOrchestrator;
import org.fmazmz.casemanager.user.auth.AuthenticatedUserResolver;
import org.fmazmz.casemanager.user.model.User;
import org.fmazmz.casemanager.utils.ApiResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.UUID;

@RestController
public class TicketController implements TicketApi{
    private final TicketOrchestrator ticketOrchestrator;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    public TicketController(TicketOrchestrator ticketOrchestrator,
                            AuthenticatedUserResolver authenticatedUserResolver) {
        this.ticketOrchestrator = ticketOrchestrator;
        this.authenticatedUserResolver = authenticatedUserResolver;
    }

    @PostMapping
    @Override
    public ResponseEntity<ApiResponseWrapper<TicketResponse>> createTicket(
            OAuth2AuthenticationToken authentication,
            @RequestBody @Valid CreateTicketRequest request) {

        User actor = authenticatedUserResolver.requireUser(authentication);
        TicketResponse response = ticketOrchestrator.createTicket(request, actor.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponseWrapper<>(response));
    }

    @PatchMapping("{ticketId}/status")
    @Override
    public ResponseEntity<ApiResponseWrapper<TicketResponse>> changeTicketStatus(
            OAuth2AuthenticationToken authentication,
            @PathVariable UUID ticketId,
            @RequestBody @Valid ChangeTicketStatusRequest request) {

        User actor = authenticatedUserResolver.requireUser(authentication);
        TicketResponse response = ticketOrchestrator.changeStatus(ticketId, request, actor.getId());

        return ResponseEntity.ok(new ApiResponseWrapper<>(response));
    }

    @PostMapping("{ticketId}/comment")
    @Override
    public ResponseEntity<ApiResponseWrapper<TicketResponse>> comment(
            OAuth2AuthenticationToken authentication,
            @PathVariable UUID ticketId,
            @RequestBody @Valid TicketCommentRequest request) {

        User actor = authenticatedUserResolver.requireUser(authentication);
        TicketResponse response = ticketOrchestrator.addComment(ticketId, request, actor.getId());

        return ResponseEntity.ok(new ApiResponseWrapper<>(response));
    }
}
