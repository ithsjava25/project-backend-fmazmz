package org.fmazmz.casemanager.ticket.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.fmazmz.casemanager.ticket.dto.LogJournal;
import org.fmazmz.casemanager.utils.ApiErrorResponse;
import org.fmazmz.casemanager.utils.ApiResponseWrapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Tag(name = "Audit API", description = "Retrieve application audit logs")
@RequestMapping(
        path = "api/v1/audit",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface AuditLogApi {

    @Operation(summary = "Get all logs")
    @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true)
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
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<LogJournal>>> getAll(
            OAuth2AuthenticationToken authentication
    );

    @Operation(summary = "Get all logs for a Ticket")
    @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true)
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
    @GetMapping("{ticketId}")
    public ResponseEntity<ApiResponseWrapper<List<LogJournal>>> getByTicketId(
            OAuth2AuthenticationToken authentication,
            @PathVariable UUID ticketId
    );
}
