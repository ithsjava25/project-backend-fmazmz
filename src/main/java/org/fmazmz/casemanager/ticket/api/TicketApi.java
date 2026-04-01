package org.fmazmz.casemanager.ticket.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.fmazmz.casemanager.ticket.dto.ChangeTicketStatusRequest;
import org.fmazmz.casemanager.ticket.dto.CreateTicketRequest;
import org.fmazmz.casemanager.ticket.dto.TicketCommentRequest;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.fmazmz.casemanager.utils.ApiErrorResponse;
import org.fmazmz.casemanager.utils.ApiResponseWrapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
public interface TicketApi {

    @Operation(summary = "Create a new Ticket")
    @ApiResponse(
            responseCode = "201",
            description = "Created",
            useReturnTypeSchema = true
    )
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
)
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping
    ResponseEntity<ApiResponseWrapper<TicketResponse>> createTicket(
            OAuth2AuthenticationToken authentication,
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
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PatchMapping("{ticketId}/status")
    ResponseEntity<ApiResponseWrapper<TicketResponse>> changeTicketStatus(
            OAuth2AuthenticationToken authentication,
            @PathVariable UUID ticketId,
            @Valid @RequestBody ChangeTicketStatusRequest request
    );

    @Operation(summary = "Add a comment (public or internal work note; visibility is enforced server-side)")
    @ApiResponse(responseCode = "202", description = "Accepted", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping("{ticketId}/comment")
    ResponseEntity<ApiResponseWrapper<TicketResponse>> comment(
            OAuth2AuthenticationToken authentication,
            @PathVariable UUID ticketId,
            @Valid @RequestBody TicketCommentRequest request
    );
}
