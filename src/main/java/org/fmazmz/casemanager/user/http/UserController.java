package org.fmazmz.casemanager.user.http;

import org.fmazmz.casemanager.common.api.ApiResponseWrapper;
import org.fmazmz.casemanager.user.application.UserLookupService;
import org.fmazmz.casemanager.user.authentication.CurrentUser;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.dto.UserLookupRequest;
import org.fmazmz.casemanager.user.dto.UserResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(
        path = "api/v1/users",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class UserController implements UserApi {

    private final UserLookupService userLookupService;

    public UserController(UserLookupService userLookupService) {
        this.userLookupService = userLookupService;
    }

    @PostMapping("lookup")
    @Override
    public ResponseEntity<ApiResponseWrapper<List<UserResponse>>> lookupByIds(
            @CurrentUser User actor,
            @RequestBody UserLookupRequest request
    ) {
        var users = userLookupService.lookupByIds(request.userIds());
        return ResponseEntity.ok(new ApiResponseWrapper<>(users));
    }
}
