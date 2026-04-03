package org.fmazmz.casemanager.user.domain;

import lombok.Getter;

@Getter
public enum AuthProvider {
    GITHUB("id", "login", "avatar_url"),
    GOOGLE("sub", "name", "picture");

    private final String idAttribute;
    private final String nameAttribute;
    private final String avatarAttribute;

    AuthProvider(String idAttribute, String nameAttribute, String avatarAttribute) {
        this.idAttribute = idAttribute;
        this.nameAttribute = nameAttribute;
        this.avatarAttribute = avatarAttribute;
    }

    public static AuthProvider fromRegistrationId(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "github" -> GITHUB;
            case "google" -> GOOGLE;
            default -> throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
        };
    }
}
