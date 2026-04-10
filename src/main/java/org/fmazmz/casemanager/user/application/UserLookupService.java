package org.fmazmz.casemanager.user.application;

import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.dto.UserResponse;
import org.fmazmz.casemanager.user.mapper.UserMapper;
import org.fmazmz.casemanager.user.repository.UserRepository;
import org.fmazmz.casemanager.user.repository.UserRoleMappingRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserLookupService {
    private final UserRepository userRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;

    public UserLookupService(UserRepository userRepository, UserRoleMappingRepository userRoleMappingRepository) {
        this.userRepository = userRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
    }

    public User requireActor(UUID actorId) {
        return userRepository.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public UserResponse toResponseWithRoles(User user) {
        return UserMapper.toDto(user, userRoleMappingRepository.findRoleNamesByUserId(user.getId()));
    }
}
