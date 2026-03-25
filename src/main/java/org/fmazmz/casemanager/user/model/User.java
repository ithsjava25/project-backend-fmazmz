package org.fmazmz.casemanager.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fmazmz.casemanager.user.model.rbac.UserRoleMapping;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "provider_id"})
})
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    @Column(nullable = false)
    private String providerId;

    @Column(unique = true, nullable = false)
    private String userName;

    @Column(unique = true)
    private String email;

    private String avatarUrl;

    @CreationTimestamp
    private Instant createdAt;

    @OneToMany(mappedBy = "user")
    private Set<UserRoleMapping> roles = new HashSet<>();
}
