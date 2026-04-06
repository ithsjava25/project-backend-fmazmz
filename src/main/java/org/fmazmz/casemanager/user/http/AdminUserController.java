package org.fmazmz.casemanager.user.http;

import jakarta.validation.Valid;
import org.fmazmz.casemanager.common.api.ApiResponseWrapper;
import org.fmazmz.casemanager.user.application.UserAdminService;
import org.fmazmz.casemanager.user.authentication.CurrentUser;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.dto.CreateUserRequest;
import org.fmazmz.casemanager.user.dto.UpdateUserRolesRequest;
import org.fmazmz.casemanager.user.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminUserController implements AdminUserApi {

    private final UserAdminService userAdminService;

    public AdminUserController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
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
                .body(new ApiResponseWrapper<>(UserResponse.from(created)));
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
