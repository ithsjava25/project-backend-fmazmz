package org.fmazmz.casemanager.user.domain.rbac;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fmazmz.casemanager.user.domain.User;

@Entity
@Table(name = "user_role_mapping")
@Getter
@Setter
@NoArgsConstructor
public class UserRoleMapping {

    @EmbeddedId
    private UserRoleId id;

    @ManyToOne(optional = false)
    @MapsId("userId")
    private User user;

    @ManyToOne(optional = false)
    @MapsId("roleId")
    private Role role;
}