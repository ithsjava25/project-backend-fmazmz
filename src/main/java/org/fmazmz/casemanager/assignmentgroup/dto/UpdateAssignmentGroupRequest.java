package org.fmazmz.casemanager.assignmentgroup.dto;

import jakarta.validation.constraints.Size;

public record UpdateAssignmentGroupRequest(
        @Size(min = 1, max = 120)
        String name,

        @Size(max = 2000)
        String description
) {
}
