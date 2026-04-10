package org.fmazmz.casemanager.user.http;

import org.fmazmz.casemanager.user.application.UserAuthentication;
import org.fmazmz.casemanager.user.application.UserLookupService;
import org.fmazmz.casemanager.user.dto.UserResponse;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.common.api.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Oauth2Controller implements Oauth2Api {

    private final UserAuthentication userAuth;
    private final UserLookupService userLookupService;

    public Oauth2Controller(UserAuthentication userAuth, UserLookupService userLookupService) {
        this.userAuth = userAuth;
        this.userLookupService = userLookupService;
    }

    @GetMapping("me")
    @Override
    public ResponseEntity<ApiResponseWrapper<UserResponse>> me(OAuth2AuthenticationToken authentication) {
        User user = userAuth.resolveUser(authentication);
        return ResponseEntity.ok(new ApiResponseWrapper<>(userLookupService.toResponseWithRoles(user)));
    }
}
