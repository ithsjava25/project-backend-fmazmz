package org.fmazmz.casemanager.user.application;

import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserLookupService {
    private final UserRepository userRepository;

    public UserLookupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User requireActor(UUID actorId) {
        return userRepository.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
