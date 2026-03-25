package org.fmazmz.casemanager.user.dto;

public record OAuthInfoResponse(
        boolean registered,
        String provider,
        String providerId,
        String login,
        String avatar
) {
}
