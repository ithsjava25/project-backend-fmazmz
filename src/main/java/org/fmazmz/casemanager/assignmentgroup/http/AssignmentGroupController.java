package org.fmazmz.casemanager.assignmentgroup.http;

import jakarta.validation.Valid;
import org.fmazmz.casemanager.assignmentgroup.application.AssignmentGroupOrchestrator;
import org.fmazmz.casemanager.assignmentgroup.dto.AssignmentGroupResponse;
import org.fmazmz.casemanager.assignmentgroup.dto.CreateAssignmentGroupRequest;
import org.fmazmz.casemanager.assignmentgroup.dto.ModifyGroupMembersRequest;
import org.fmazmz.casemanager.assignmentgroup.dto.UpdateAssignmentGroupRequest;
import org.fmazmz.casemanager.common.api.ApiResponseWrapper;
import org.fmazmz.casemanager.user.authentication.CurrentUser;
import org.fmazmz.casemanager.user.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class AssignmentGroupController implements AssignmentGroupApi {

    private final AssignmentGroupOrchestrator assignmentGroupOrchestrator;

    public AssignmentGroupController(AssignmentGroupOrchestrator assignmentGroupOrchestrator) {
        this.assignmentGroupOrchestrator = assignmentGroupOrchestrator;
    }

    @GetMapping
    @Override
    public ResponseEntity<ApiResponseWrapper<List<AssignmentGroupResponse>>> listGroups(@CurrentUser User actor) {
        return ResponseEntity.ok(new ApiResponseWrapper<>(assignmentGroupOrchestrator.listGroups()));
    }

    @GetMapping("{id}")
    @Override
    public ResponseEntity<ApiResponseWrapper<AssignmentGroupResponse>> getGroup(
            @CurrentUser User actor,
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(new ApiResponseWrapper<>(assignmentGroupOrchestrator.getGroup(id)));
    }

    @PostMapping
    @Override
    public ResponseEntity<ApiResponseWrapper<AssignmentGroupResponse>> createGroup(
            @CurrentUser User actor,
            @RequestBody @Valid CreateAssignmentGroupRequest request
    ) {
        AssignmentGroupResponse body = assignmentGroupOrchestrator.createGroup(request, actor);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseWrapper<>(body));
    }

    @PatchMapping("{id}")
    @Override
    public ResponseEntity<ApiResponseWrapper<AssignmentGroupResponse>> updateGroup(
            @CurrentUser User actor,
            @PathVariable UUID id,
            @RequestBody @Valid UpdateAssignmentGroupRequest request
    ) {
        return ResponseEntity.ok(
                new ApiResponseWrapper<>(assignmentGroupOrchestrator.updateGroup(id, request, actor))
        );
    }

    @PostMapping("{id}/members")
    @Override
    public ResponseEntity<ApiResponseWrapper<AssignmentGroupResponse>> addMembers(
            @CurrentUser User actor,
            @PathVariable UUID id,
            @RequestBody @Valid ModifyGroupMembersRequest request
    ) {
        return ResponseEntity.ok(
                new ApiResponseWrapper<>(assignmentGroupOrchestrator.addMembers(id, request, actor))
        );
    }

    @DeleteMapping("{id}/members/{userId}")
    @Override
    public ResponseEntity<ApiResponseWrapper<AssignmentGroupResponse>> removeMember(
            @CurrentUser User actor,
            @PathVariable UUID id,
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                new ApiResponseWrapper<>(assignmentGroupOrchestrator.removeMember(id, userId, actor))
        );
    }
}
