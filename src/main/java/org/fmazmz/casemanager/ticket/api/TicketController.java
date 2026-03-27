package org.fmazmz.casemanager.ticket.api;

import jakarta.validation.Valid;
import org.fmazmz.casemanager.ticket.dto.CreateTicketRequest;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.fmazmz.casemanager.ticket.orchestration.TicketOrchestrator;
import org.fmazmz.casemanager.utils.ApiResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TicketController implements TicketApi{
    private final TicketOrchestrator ticketOrchestrator;

    public TicketController(TicketOrchestrator ticketOrchestrator) {
        this.ticketOrchestrator = ticketOrchestrator;
    }

    @PostMapping
    @Override
    public ResponseEntity<ApiResponseWrapper<TicketResponse>> createTicket(
            @RequestBody @Valid CreateTicketRequest request) {

        TicketResponse response = ticketOrchestrator.createTicket(request, request.requesterId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponseWrapper<>(response));
    }
}
