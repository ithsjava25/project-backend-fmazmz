package org.fmazmz.casemanager.user.http;

import org.fmazmz.casemanager.user.application.UserAuthentication;
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

    public Oauth2Controller(UserAuthentication userAuth) {
        this.userAuth = userAuth;
    }

    @GetMapping("me")
    @Override
    public ResponseEntity<ApiResponseWrapper<UserResponse>> me(OAuth2AuthenticationToken authentication) {
        User user = userAuth.resolveUser(authentication);
        return ResponseEntity.ok(new ApiResponseWrapper<>(UserResponse.from(user)));
    }
}
