package org.fmazmz.casemanager.user;

import lombok.extern.slf4j.Slf4j;
import org.fmazmz.casemanager.user.dto.CompleteProfileRequest;
import org.fmazmz.casemanager.user.dto.SignupRequest;
import org.fmazmz.casemanager.user.model.AuthProvider;
import org.fmazmz.casemanager.user.model.User;
import org.fmazmz.casemanager.user.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserAuth {
    private final UserRepository userRepository;

    public UserAuth(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User signup(OAuth2User principal, AuthProvider provider, SignupRequest request) {
        String providerId = extractProviderId(principal, provider);
        String avatarUrl = principal.getAttribute(provider.getAvatarAttribute());

        Optional<User> existing = userRepository.findByProviderAndProviderId(provider, providerId);
        if (existing.isPresent()) {
            throw new IllegalStateException("User already registered");
        }

        User user = new User();
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setUserName(request.userName());
        user.setAvatarUrl(avatarUrl);
        user.setProfileCompleted(false);

        return userRepository.save(user);
    }

    public User completeProfile(OAuth2User principal, AuthProvider provider, CompleteProfileRequest request) {
        String providerId = extractProviderId(principal, provider);

        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new IllegalStateException("User not found. Please sign up first."));

        if (user.isProfileCompleted()) {
            throw new IllegalStateException("Profile is already completed");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email is already in use");
        }

        user.setEmail(request.email());
        user.setProfileCompleted(true);

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
