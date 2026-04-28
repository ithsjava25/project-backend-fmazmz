package org.fmazmz.casemanager.user.repository;

import org.fmazmz.casemanager.user.domain.AuthProvider;
import org.fmazmz.casemanager.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
    Optional<User> findByEmailIgnoreCase(String email);
    List<User> findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String userName, String email, Pageable pageable);

    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
}
