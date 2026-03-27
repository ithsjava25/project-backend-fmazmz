package org.fmazmz.casemanager.user.auth;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.fmazmz.casemanager.user.dto.OAuthInfoResponse;
import org.fmazmz.casemanager.user.dto.SignupRequest;
import org.fmazmz.casemanager.user.dto.UserResponse;
import org.fmazmz.casemanager.user.model.AuthProvider;
import org.fmazmz.casemanager.user.model.User;
import org.fmazmz.casemanager.utils.ApiResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
public class Oauth2Controller implements Oauth2Api {

    private final UserAuth userAuth;

    public Oauth2Controller(UserAuth userAuth) {
        this.userAuth = userAuth;
    }

    @GetMapping("/me")
    @Override
    public ResponseEntity<ApiResponseWrapper<?>> me(OAuth2AuthenticationToken authentication) {
        var principal = authentication.getPrincipal();
        AuthProvider provider = AuthProvider.fromRegistrationId(
                authentication.getAuthorizedClientRegistrationId());

        log.info("Provider: {}, attributes: {}", provider, principal.getAttributes());

        String providerId = principal.getAttribute(provider.getIdAttribute()).toString();
        Optional<User> user = userAuth.findByProviderAndProviderId(provider, providerId);

        if (user.isEmpty()) {
            var response = new OAuthInfoResponse(
                    false,
                    provider.name(),
                    providerId,
                    principal.getAttribute(provider.getNameAttribute()),
                    principal.getAttribute(provider.getAvatarAttribute())
            );
            return ResponseEntity.ok(new ApiResponseWrapper<>(response));
        }

        return ResponseEntity.ok(new ApiResponseWrapper<>(UserResponse.from(user.get())));
    }

    @PostMapping("/signup")
    @Override
    public ResponseEntity<ApiResponseWrapper<UserResponse>> signup(
            OAuth2AuthenticationToken authentication,
            @RequestBody @Valid SignupRequest request) {

        User user = userAuth.signup(authentication, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponseWrapper<>(UserResponse.from(user)));
    }
}
