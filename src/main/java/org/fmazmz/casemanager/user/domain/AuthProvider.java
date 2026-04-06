package org.fmazmz.casemanager.user.domain;

import lombok.Getter;

import java.util.Set;

@Getter
public enum AuthProvider {
    GITHUB("id", "login", "avatar_url");

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
            default -> throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
        };
    }

    public String pendingProviderId() {
        return switch (this) {
            case GITHUB -> "PENDING";
            default -> throw new IllegalStateException("No pending provider id marker configured for " + this.name());
        };
    }

    public String unlinkedProviderId() {
        return switch (this) {
            case GITHUB -> "UNLINKED";
            default -> throw new IllegalStateException("No unlinked provider id marker configured for " + this.name());
        };
    }

    public Set<String> linkablePlaceholderProviderIds() {
        return Set.of(pendingProviderId(), unlinkedProviderId());
    }
}
