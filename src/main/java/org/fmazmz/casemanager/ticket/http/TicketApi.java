package org.fmazmz.casemanager.ticket.http;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.fmazmz.casemanager.common.api.openapi.NotFoundApiResponse;
import org.fmazmz.casemanager.common.api.openapi.StandardRestApiResponses;
import org.fmazmz.casemanager.ticket.dto.ChangeTicketStatusRequest;
import org.fmazmz.casemanager.ticket.dto.CreateTicketRequest;
import org.fmazmz.casemanager.ticket.dto.TicketCommentRequest;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.fmazmz.casemanager.user.authentication.CurrentUser;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.common.api.ApiResponseWrapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "Ticket API", description = "Perform CRUD operations on Tickets")
@RequestMapping(
        path = "api/v1/tickets",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@StandardRestApiResponses
public interface TicketApi {

    @Operation(summary = "Create a new Ticket")
    @ApiResponse(
            responseCode = "201",
            description = "Created",
            useReturnTypeSchema = true
    )
    @PostMapping
    ResponseEntity<ApiResponseWrapper<TicketResponse>> createTicket(
            @Parameter(hidden = true) @CurrentUser User actor,
            @Valid
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload to create a new Ticket"
            )
            CreateTicketRequest request
    );

    @Operation(summary = "Change ticket status")
    @ApiResponse(responseCode = "200", description = "Updated", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @PatchMapping("{ticketId}/status")
    ResponseEntity<ApiResponseWrapper<TicketResponse>> changeTicketStatus(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID ticketId,
            @Valid @RequestBody ChangeTicketStatusRequest request
    );

    @Operation(summary = "Add a comment (public or internal work note; visibility is enforced server-side)")
    @ApiResponse(responseCode = "202", description = "Accepted", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @PostMapping("{ticketId}/comment")
    ResponseEntity<ApiResponseWrapper<TicketResponse>> comment(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID ticketId,
            @Valid @RequestBody TicketCommentRequest request
    );
}
