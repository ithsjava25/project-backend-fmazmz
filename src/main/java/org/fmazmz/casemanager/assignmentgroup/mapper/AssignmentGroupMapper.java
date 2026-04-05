package org.fmazmz.casemanager.assignmentgroup.mapper;

import org.fmazmz.casemanager.assignmentgroup.domain.AssignmentGroup;
import org.fmazmz.casemanager.assignmentgroup.dto.AssignmentGroupResponse;

import java.util.Comparator;

public final class AssignmentGroupMapper {

    private AssignmentGroupMapper() {
    }

    public static AssignmentGroupResponse toDto(AssignmentGroup group) {
        var memberIds = group.getMembers().stream()
                .map(u -> u.getId())
                .sorted(Comparator.comparing(id -> id.toString()))
                .toList();
        return new AssignmentGroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                memberIds,
                group.getCreatedAt()
        );
    }
}
