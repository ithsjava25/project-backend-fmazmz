package org.fmazmz.casemanager.user.dto;

import java.util.List;
import java.util.UUID;

public record UserResponse(
        boolean registered,
        UUID id,
        String provider,
        String userName,
        String email,
        String avatar,
        List<String> roles
) {
}
