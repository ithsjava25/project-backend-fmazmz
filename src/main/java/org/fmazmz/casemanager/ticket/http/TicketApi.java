package org.fmazmz.casemanager.ticket.http;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.fmazmz.casemanager.common.api.openapi.NotFoundApiResponse;
import org.fmazmz.casemanager.common.api.openapi.StandardRestApiResponses;
import org.fmazmz.casemanager.ticket.dto.UpdateTicketPriorityRequest;
import org.fmazmz.casemanager.ticket.dto.ChangeTicketStatusRequest;
import org.fmazmz.casemanager.ticket.dto.CreateTicketRequest;
import org.fmazmz.casemanager.ticket.dto.TicketCommentRequest;
import org.fmazmz.casemanager.ticket.dto.TicketResponse;
import org.fmazmz.casemanager.ticket.dto.UpdateTicketRequest;
import org.fmazmz.casemanager.user.authentication.CurrentUser;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.common.api.ApiResponseWrapper;
import org.fmazmz.casemanager.common.pagination.PagedResult;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "Ticket API", description = "Perform CRUD operations on Tickets")
@RequestMapping(
        path = "api/v1/tickets",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@StandardRestApiResponses
public interface TicketApi {

    @Operation(summary = "List tickets (paginated)")
    @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true)
    @GetMapping
    ResponseEntity<ApiResponseWrapper<PagedResult<TicketResponse>>> listTickets(
            @Parameter(hidden = true) @CurrentUser User actor,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    );

    @Operation(summary = "List tickets by requester user id (paginated)")
    @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true)
    @GetMapping("requester/{userId}")
    ResponseEntity<ApiResponseWrapper<PagedResult<TicketResponse>>> listTicketsByRequester(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID userId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    );

    @Operation(summary = "List tickets by assignee user id (paginated)")
    @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true)
    @GetMapping("assignee/{userId}")
    ResponseEntity<ApiResponseWrapper<PagedResult<TicketResponse>>> listTicketsByAssignee(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID userId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    );

    @Operation(summary = "List tickets by assignment group id (paginated)")
    @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true)
    @GetMapping("assignment-group/{assignmentGroupId}")
    ResponseEntity<ApiResponseWrapper<PagedResult<TicketResponse>>> listTicketsByAssignmentGroup(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID assignmentGroupId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    );

    @Operation(summary = "Get ticket by id (internal comments only if caller has ticket.comment_internal)")
    @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @GetMapping("{ticketId}")
    ResponseEntity<ApiResponseWrapper<TicketResponse>> getTicket(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID ticketId
    );

    @Operation(summary = "Get ticket by ticket number (e.g. INC0000001; internal comments only if caller has ticket.comment_internal)")
    @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @GetMapping("number/{ticketNumber}")
    ResponseEntity<ApiResponseWrapper<TicketResponse>> getTicketByNumber(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable String ticketNumber
    );

    @Operation(summary = "Create a new Ticket")
    @ApiResponse(
            responseCode = "201",
            description = "Created",
            useReturnTypeSchema = true
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
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
    @PatchMapping(path = "{ticketId}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponseWrapper<TicketResponse>> changeTicketStatus(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID ticketId,
            @Valid @RequestBody ChangeTicketStatusRequest request
    );

    @Operation(summary = "Update ticket priority")
    @ApiResponse(responseCode = "200", description = "Updated", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @PatchMapping(path = "{ticketId}/priority", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponseWrapper<TicketResponse>> changeTicketPriority(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID ticketId,
            @Valid @RequestBody UpdateTicketPriorityRequest request
    );

    @Operation(summary = "Update ticket title and/or description")
    @ApiResponse(responseCode = "200", description = "Updated", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @PatchMapping(path = "{ticketId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponseWrapper<TicketResponse>> updateTicket(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID ticketId,
            @Valid @RequestBody UpdateTicketRequest request
    );

    @Operation(summary = "Add a comment (public or internal work note; visibility is enforced server-side)")
    @ApiResponse(responseCode = "202", description = "Accepted", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @PostMapping(path = "{ticketId}/comment", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponseWrapper<TicketResponse>> comment(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID ticketId,
            @Valid @RequestBody TicketCommentRequest request
    );

    @Operation(
            summary = "Upload attachment to ticket and log as comment",
            description = "Use multipart/form-data with a part named `file` (not JSON). In Swagger, choose a file; do not send a JSON body."
    )
    @ApiResponse(responseCode = "200", description = "Ok (attachment stored and public comment created)", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @PostMapping(path = "{ticketId}/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiResponseWrapper<TicketResponse>> uploadAttachment(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID ticketId,
            @Parameter(
                    name = "file",
                    required = true,
                    description = "Binary file; form field name must be exactly \"file\"",
                    schema = @Schema(type = "string", format = "binary")
            )
            @RequestPart("file")
            MultipartFile file
    );
}
