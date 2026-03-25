package org.fmazmz.casemanager.user.dto;

import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank(message = "Username is required")
        String userName
) {
}
