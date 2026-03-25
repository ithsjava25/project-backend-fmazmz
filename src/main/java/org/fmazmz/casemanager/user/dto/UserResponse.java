package org.fmazmz.casemanager.user.dto;

import org.fmazmz.casemanager.user.model.User;

import java.util.UUID;

public record UserResponse(
        boolean registered,
        UUID id,
        String provider,
        String userName,
        String email,
        String avatar
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                true,
                user.getId(),
                user.getProvider().name(),
                user.getUserName(),
                user.getEmail(),
                user.getAvatarUrl()
        );
    }
}
