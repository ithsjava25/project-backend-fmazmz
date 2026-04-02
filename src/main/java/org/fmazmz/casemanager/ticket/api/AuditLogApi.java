package org.fmazmz.casemanager.ticket.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.fmazmz.casemanager.openapi.NotFoundApiResponse;
import org.fmazmz.casemanager.openapi.StandardRestApiResponses;
import org.fmazmz.casemanager.ticket.dto.LogJournal;
import org.fmazmz.casemanager.user.auth.CurrentUser;
import org.fmazmz.casemanager.user.model.User;
import org.fmazmz.casemanager.utils.ApiResponseWrapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@StandardRestApiResponses
public interface AuditLogApi {

    @Operation(summary = "Get all logs")
    @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true)
    @GetMapping
    ResponseEntity<ApiResponseWrapper<List<LogJournal>>> getAll(
            @Parameter(hidden = true) @CurrentUser User actor
    );

    @Operation(summary = "Get all logs for a Ticket")
    @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @GetMapping("{ticketId}")
    ResponseEntity<ApiResponseWrapper<List<LogJournal>>> getByTicketId(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID ticketId
    );
}
