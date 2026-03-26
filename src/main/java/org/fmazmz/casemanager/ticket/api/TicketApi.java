package org.fmazmz.casemanager.ticket.api;

import jakarta.validation.Valid;
import org.fmazmz.casemanager.ticket.dto.CreateTicketRequest;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.fmazmz.casemanager.ticket.orchestration.TicketOrchestrator;
import org.fmazmz.casemanager.utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/tickets")
public class TicketApi {
    private final TicketOrchestrator ticketOrchestrator;

    public TicketApi(TicketOrchestrator ticketOrchestrator) {
        this.ticketOrchestrator = ticketOrchestrator;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(
            @RequestBody @Valid CreateTicketRequest request) {

        TicketResponse response = ticketOrchestrator.createTicket(request, request.requesterId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(response));
    }
}
