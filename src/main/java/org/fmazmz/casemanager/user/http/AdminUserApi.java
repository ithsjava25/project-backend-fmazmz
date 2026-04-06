package org.fmazmz.casemanager.user.http;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.fmazmz.casemanager.common.api.ApiResponseWrapper;
import org.fmazmz.casemanager.common.api.openapi.NotFoundApiResponse;
import org.fmazmz.casemanager.common.api.openapi.StandardRestApiResponses;
import org.fmazmz.casemanager.user.authentication.CurrentUser;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.dto.CreateUserRequest;
import org.fmazmz.casemanager.user.dto.UpdateUserRolesRequest;
import org.fmazmz.casemanager.user.dto.UserResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Admin User API", description = "Admin operations for user provisioning and role management")
@RequestMapping(
        path = "api/v1/admin",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@StandardRestApiResponses
public interface AdminUserApi {

    @Operation(summary = "Create a pre-provisioned internal user")
    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true)
    @PostMapping("users")
    ResponseEntity<ApiResponseWrapper<UserResponse>> createUser(
            @Parameter(hidden = true) @CurrentUser User admin,
            @Valid @RequestBody CreateUserRequest request
    );

    @Operation(summary = "Replace all roles for a user by email")
    @ApiResponse(responseCode = "200", description = "Updated", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @PutMapping("users/roles")
    ResponseEntity<ApiResponseWrapper<Void>> replaceRoles(
            @Parameter(hidden = true) @CurrentUser User admin,
            @Valid @RequestBody UpdateUserRolesRequest request
    );
}
