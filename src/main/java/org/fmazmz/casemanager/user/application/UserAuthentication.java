package org.fmazmz.casemanager.user.application;

import lombok.extern.slf4j.Slf4j;
import org.fmazmz.casemanager.user.infra.oauth.GithubEmailResolver;
import org.fmazmz.casemanager.user.dto.SignupRequest;
import org.fmazmz.casemanager.user.domain.AuthProvider;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.repository.UserRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserAuthentication {
    private final UserRepository userRepository;
    private final GithubEmailResolver githubEmailResolver;

    public UserAuthentication(UserRepository userRepository, GithubEmailResolver githubEmailResolver) {
        this.userRepository = userRepository;
        this.githubEmailResolver = githubEmailResolver;
    }

    public User signup(OAuth2AuthenticationToken authentication, SignupRequest request) {
        if (!"github".equalsIgnoreCase(authentication.getAuthorizedClientRegistrationId())) {
            throw new IllegalStateException("Only GitHub signup is supported.");
        }

        OAuth2User principal = authentication.getPrincipal();
        String providerId = extractProviderId(principal, AuthProvider.GITHUB);
        String avatarUrl = principal.getAttribute(AuthProvider.GITHUB.getAvatarAttribute());
        String email = githubEmailResolver.resolveGithubEmail(authentication);

        Optional<User> existing = userRepository.findByProviderAndProviderId(AuthProvider.GITHUB, providerId);
        if (existing.isPresent()) {
            throw new IllegalStateException("User already registered");
        }

        User user = new User();
        user.setProvider(AuthProvider.GITHUB);
        user.setProviderId(providerId);
        user.setUserName(request.userName());
        user.setAvatarUrl(avatarUrl);
        user.setEmail(email);

        return userRepository.save(user);
    }


    public Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }

    private String extractProviderId(OAuth2User principal, AuthProvider provider) {
        Object rawId = principal.getAttribute(provider.getIdAttribute());
        if (rawId == null) {
            throw new IllegalStateException("OAuth2 provider did not return a user ID");
        }
        return rawId.toString();
    }
}
