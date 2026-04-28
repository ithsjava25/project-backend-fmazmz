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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Tag(name = "Admin User API", description = "Admin operations for user provisioning and role management")
@RequestMapping(
        path = "api/v1/admin",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@StandardRestApiResponses
public interface AdminUserApi {

    @Operation(summary = "List all users with roles")
    @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true)
    @GetMapping("users")
    ResponseEntity<ApiResponseWrapper<List<UserResponse>>> listUsers(
            @Parameter(hidden = true) @CurrentUser User admin
    );

    @Operation(summary = "Get user by ID with roles")
    @ApiResponse(responseCode = "200", description = "Ok", useReturnTypeSchema = true)
    @NotFoundApiResponse
    @GetMapping("users/{userId}")
    ResponseEntity<ApiResponseWrapper<UserResponse>> getUserById(
            @Parameter(hidden = true) @CurrentUser User admin,
            @PathVariable UUID userId
    );

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
