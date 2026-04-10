package org.fmazmz.casemanager.user.mapper;

import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.dto.UserResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public final class UserMapper {

    public static UserResponse toDto(User user, Collection<String> roleNames) {
        return new UserResponse(
                true,
                user.getId(),
                user.getProvider().name(),
                user.getUserName(),
                user.getEmail(),
                user.getAvatarUrl(),
                roleNames == null ? List.of() : roleNames.stream().sorted().toList()
        );
    }
}
