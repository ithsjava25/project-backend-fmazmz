package org.fmazmz.casemanager.assignmentgroup.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AssignmentGroupResponse(
        UUID id,
        String name,
        String description,
        List<UUID> memberIds,
        Instant createdAt
) {
}
