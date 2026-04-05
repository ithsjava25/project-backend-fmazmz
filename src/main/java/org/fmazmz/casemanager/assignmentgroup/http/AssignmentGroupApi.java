package org.fmazmz.casemanager.assignmentgroup.http;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.fmazmz.casemanager.assignmentgroup.dto.AssignmentGroupResponse;
import org.fmazmz.casemanager.assignmentgroup.dto.CreateAssignmentGroupRequest;
import org.fmazmz.casemanager.assignmentgroup.dto.ModifyGroupMembersRequest;
import org.fmazmz.casemanager.assignmentgroup.dto.UpdateAssignmentGroupRequest;
import org.fmazmz.casemanager.common.api.ApiResponseWrapper;
import org.fmazmz.casemanager.common.api.openapi.NotFoundApiResponse;
import org.fmazmz.casemanager.common.api.openapi.StandardRestApiResponses;
import org.fmazmz.casemanager.user.authentication.CurrentUser;
import org.fmazmz.casemanager.user.domain.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Tag(name = "Assignment groups", description = "Service-desk style groups (e.g. L1, L2, L3) and their members")
@RequestMapping(
        path = "api/v1/assignment-groups",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@StandardRestApiResponses
public interface AssignmentGroupApi {

    @Operation(summary = "List assignment groups")
    @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
    @GetMapping
    ResponseEntity<ApiResponseWrapper<List<AssignmentGroupResponse>>> listGroups(
            @Parameter(hidden = true) @CurrentUser User actor
    );

    @Operation(summary = "Get an assignment group by id")
    @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @GetMapping("{id}")
    ResponseEntity<ApiResponseWrapper<AssignmentGroupResponse>> getGroup(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID id
    );

    @Operation(summary = "Create an assignment group (admin)")
    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponseWrapper<AssignmentGroupResponse>> createGroup(
            @Parameter(hidden = true) @CurrentUser User actor,
            @Valid @RequestBody CreateAssignmentGroupRequest request
    );

    @Operation(summary = "Update an assignment group (admin)")
    @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @PatchMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponseWrapper<AssignmentGroupResponse>> updateGroup(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAssignmentGroupRequest request
    );

    @Operation(summary = "Add users to an assignment group (admin)")
    @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @PostMapping(path = "{id}/members", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponseWrapper<AssignmentGroupResponse>> addMembers(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID id,
            @Valid @RequestBody ModifyGroupMembersRequest request
    );

    @Operation(summary = "Remove a user from an assignment group (admin)")
    @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @DeleteMapping("{id}/members/{userId}")
    ResponseEntity<ApiResponseWrapper<AssignmentGroupResponse>> removeMember(
            @Parameter(hidden = true) @CurrentUser User actor,
            @PathVariable UUID id,
            @PathVariable UUID userId
    );
}
