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
import org.fmazmz.casemanager.utils.PagedResult;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "Audit API", description = "Retrieve application audit logs")
@RequestMapping(
        path = "api/v1/audit",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@StandardRestApiResponses
public interface AuditLogApi {

    @Operation(summary = "Get audit logs (paginated)")
    @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true)
    @GetMapping
    ResponseEntity<ApiResponseWrapper<PagedResult<LogJournal>>> getAll(
            @Parameter(hidden = true) @CurrentUser User actor,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    );

    @Operation(summary = "Get audit logs for a ticket (paginated)")
    @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @GetMapping("{ticketId}")
    ResponseEntity<ApiResponseWrapper<PagedResult<LogJournal>>> getByTicketId(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID ticketId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    );
}
