package org.fmazmz.casemanager.user.authentication;

import org.fmazmz.casemanager.exception.AccessDeniedException;
import org.fmazmz.casemanager.user.domain.AuthProvider;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.repository.UserRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserResolver {
    private final UserRepository userRepository;

    public AuthenticatedUserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User requireUser(OAuth2AuthenticationToken authentication) {
        AuthProvider provider = AuthProvider.fromRegistrationId(authentication.getAuthorizedClientRegistrationId());
        Object rawProviderId = authentication.getPrincipal().getAttribute(provider.getIdAttribute());

        if (rawProviderId == null) {
            throw new AccessDeniedException("Authenticated principal did not include provider user id");
        }

        return userRepository
                .findByProviderAndProviderId(provider, rawProviderId.toString())
                .orElseThrow(() ->
                        new AccessDeniedException("Authenticated principal is not linked to an application user")
                );
    }
}
