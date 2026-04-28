package org.fmazmz.casemanager.user.http;

import jakarta.validation.Valid;
import org.fmazmz.casemanager.common.api.ApiResponseWrapper;
import org.fmazmz.casemanager.user.application.UserAdminService;
import org.fmazmz.casemanager.user.application.UserLookupService;
import org.fmazmz.casemanager.user.authentication.CurrentUser;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.dto.CreateUserRequest;
import org.fmazmz.casemanager.user.dto.UpdateUserRolesRequest;
import org.fmazmz.casemanager.user.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class AdminUserController implements AdminUserApi {

    private final UserAdminService userAdminService;
    private final UserLookupService userLookupService;

    public AdminUserController(UserAdminService userAdminService, UserLookupService userLookupService) {
        this.userAdminService = userAdminService;
        this.userLookupService = userLookupService;
    }

    @GetMapping("users")
    @Override
    public ResponseEntity<ApiResponseWrapper<List<UserResponse>>> listUsers(@CurrentUser User admin) {
        return ResponseEntity.ok(new ApiResponseWrapper<>(userLookupService.listAllUsersWithRoles()));
    }

    @GetMapping("users/{userId}")
    @Override
    public ResponseEntity<ApiResponseWrapper<UserResponse>> getUserById(
            @CurrentUser User admin,
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(new ApiResponseWrapper<>(userLookupService.getUserByIdWithRoles(userId)));
    }

    @PostMapping("users")
    @Override
    public ResponseEntity<ApiResponseWrapper<UserResponse>> createUser(
            @CurrentUser User admin,
            @RequestBody @Valid CreateUserRequest request
    ) {
        var created = userAdminService.createUser(admin, request.email(), request.role());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponseWrapper<>(userLookupService.toResponseWithRoles(created)));
    }

    @PutMapping("users/roles")
    @Override
    public ResponseEntity<ApiResponseWrapper<Void>> replaceRoles(
            @CurrentUser User admin,
            @RequestBody @Valid UpdateUserRolesRequest request
    ) {
        userAdminService.replaceUserRolesByEmail(admin, request.email(), UserAdminService.parseRoleNames(request.roles()));
        return ResponseEntity.ok(new ApiResponseWrapper<>(null));
    }
}
