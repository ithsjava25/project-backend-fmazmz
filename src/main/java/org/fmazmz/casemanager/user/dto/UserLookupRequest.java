package org.fmazmz.casemanager.user.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record UserLookupRequest(
        @NotEmpty List<UUID> userIds
) {
}
