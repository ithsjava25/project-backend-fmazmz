package org.fmazmz.casemanager.user.application;

import lombok.extern.slf4j.Slf4j;
import org.fmazmz.casemanager.user.infra.oauth.GithubEmailResolver;
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

    public User resolveUser(OAuth2AuthenticationToken authentication) {
        AuthProvider loginProvider = AuthProvider.fromRegistrationId(authentication.getAuthorizedClientRegistrationId());
        if (loginProvider != AuthProvider.GITHUB) {
            throw new IllegalStateException("Only GitHub login is supported.");
        }

        OAuth2User principal = authentication.getPrincipal();
        String providerId = extractProviderId(principal, loginProvider);
        String avatarUrl = principal.getAttribute(loginProvider.getAvatarAttribute());
        String email = githubEmailResolver.resolveGithubEmail(authentication);
        String oauthLogin = principal.getAttribute(loginProvider.getNameAttribute());

        Optional<User> existing = userRepository.findByProviderAndProviderId(loginProvider, providerId);
        if (existing.isPresent()) {
            return existing.get();
        }

        Optional<User> preProvisioned = userRepository.findByEmailIgnoreCase(email);
        if (preProvisioned.isEmpty()) {
            throw new IllegalStateException("No internal account found for this email. Ask an administrator to create one.");
        }

        User user = preProvisioned.get();
        if (user.getProvider() != AuthProvider.GITHUB) {
            throw new IllegalStateException("Configured account provider does not match GitHub login.");
        }

        if (!loginProvider.linkablePlaceholderProviderIds().contains(user.getProviderId())
                && !user.getProviderId().equals(providerId)) {
            throw new IllegalStateException("This internal account is already linked to a different GitHub identity.");
        }

        user.setProviderId(providerId);
        user.setAvatarUrl(avatarUrl);
        if (user.getUserName() == null || user.getUserName().isBlank()) {
            if (oauthLogin == null || oauthLogin.isBlank()) {
                throw new IllegalStateException("OAuth2 provider did not return a username/login");
            }
            String normalizedLogin = oauthLogin.trim();
            if (userRepository.existsByUserName(normalizedLogin)) {
                throw new IllegalStateException("OAuth username is already in use by another account");
            }
            user.setUserName(normalizedLogin);
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            user.setEmail(email);
        }

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
