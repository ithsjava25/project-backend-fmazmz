package org.fmazmz.casemanager.assignmentgroup.application;

import org.fmazmz.casemanager.assignmentgroup.domain.AssignmentGroup;
import org.fmazmz.casemanager.assignmentgroup.dto.AssignmentGroupResponse;
import org.fmazmz.casemanager.assignmentgroup.dto.CreateAssignmentGroupRequest;
import org.fmazmz.casemanager.assignmentgroup.dto.ModifyGroupMembersRequest;
import org.fmazmz.casemanager.assignmentgroup.dto.UpdateAssignmentGroupRequest;
import org.fmazmz.casemanager.assignmentgroup.mapper.AssignmentGroupMapper;
import org.fmazmz.casemanager.assignmentgroup.repository.AssignmentGroupRepository;
import org.fmazmz.casemanager.exception.AccessDeniedException;
import org.fmazmz.casemanager.ticket.domain.TicketAction;
import org.fmazmz.casemanager.ticket.application.workflow.PermissionEvaluator;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AssignmentGroupOrchestrator {

    private final AssignmentGroupRepository assignmentGroupRepository;
    private final UserRepository userRepository;
    private final PermissionEvaluator permissionEvaluator;

    public AssignmentGroupOrchestrator(
            AssignmentGroupRepository assignmentGroupRepository,
            UserRepository userRepository,
            PermissionEvaluator permissionEvaluator
    ) {
        this.assignmentGroupRepository = assignmentGroupRepository;
        this.userRepository = userRepository;
        this.permissionEvaluator = permissionEvaluator;
    }

    @Transactional(readOnly = true)
    public List<AssignmentGroupResponse> listGroups() {
        return assignmentGroupRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(AssignmentGroup::getName))
                .map(AssignmentGroupMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AssignmentGroupResponse getGroup(UUID id) {
        AssignmentGroup group = assignmentGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment group not found"));
        return AssignmentGroupMapper.toDto(group);
    }

    @Transactional
    public AssignmentGroupResponse createGroup(CreateAssignmentGroupRequest request, User actor) {
        requireManageAssignmentGroups(actor);

        if (assignmentGroupRepository.findByName(request.name().trim()).isPresent()) {
            throw new IllegalArgumentException("An assignment group with this name already exists");
        }

        AssignmentGroup group = new AssignmentGroup();
        group.setName(request.name().trim());
        group.setDescription(request.description() != null ? request.description().trim() : null);
        if (group.getDescription() != null && group.getDescription().isEmpty()) {
            group.setDescription(null);
        }

        AssignmentGroup saved = assignmentGroupRepository.saveAndFlush(group);
        return AssignmentGroupMapper.toDto(saved);
    }

    @Transactional
    public AssignmentGroupResponse updateGroup(UUID id, UpdateAssignmentGroupRequest request, User actor) {
        requireManageAssignmentGroups(actor);

        if (request.name() == null && request.description() == null) {
            throw new IllegalArgumentException("At least one field must be provided to update");
        }

        AssignmentGroup group = assignmentGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment group not found"));

        if (request.name() != null) {
            String trimmed = request.name().trim();
            assignmentGroupRepository.findByName(trimmed)
                    .filter(g -> !g.getId().equals(id))
                    .ifPresent(g -> {
                        throw new IllegalArgumentException("An assignment group with this name already exists");
                    });
            group.setName(trimmed);
        }

        if (request.description() != null) {
            String d = request.description().trim();
            group.setDescription(d.isEmpty() ? null : d);
        }

        return AssignmentGroupMapper.toDto(assignmentGroupRepository.saveAndFlush(group));
    }

    @Transactional
    public AssignmentGroupResponse addMembers(UUID groupId, ModifyGroupMembersRequest request, User actor) {
        requireManageAssignmentGroups(actor);

        AssignmentGroup group = assignmentGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment group not found"));

        for (UUID userId : request.userIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
            group.getMembers().add(user);
        }

        return AssignmentGroupMapper.toDto(assignmentGroupRepository.saveAndFlush(group));
    }

    @Transactional
    public AssignmentGroupResponse removeMember(UUID groupId, UUID userId, User actor) {
        requireManageAssignmentGroups(actor);

        AssignmentGroup group = assignmentGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment group not found"));

        boolean removed = group.getMembers().removeIf(u -> u.getId().equals(userId));
        if (!removed) {
            throw new IllegalArgumentException("User is not a member of this assignment group");
        }

        return AssignmentGroupMapper.toDto(assignmentGroupRepository.saveAndFlush(group));
    }

    private void requireManageAssignmentGroups(User actor) {
        if (!permissionEvaluator.hasPermission(actor, TicketAction.MANAGE_ASSIGNMENT_GROUPS)) {
            throw new AccessDeniedException(
                    "User is not authorized to perform action: " + TicketAction.MANAGE_ASSIGNMENT_GROUPS
            );
        }
    }
}
