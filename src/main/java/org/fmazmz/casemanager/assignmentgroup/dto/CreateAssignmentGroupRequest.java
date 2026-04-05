package org.fmazmz.casemanager.assignmentgroup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAssignmentGroupRequest(
        @NotBlank
        @Size(max = 120)
        String name,

        @Size(max = 2000)
        String description
) {
}
