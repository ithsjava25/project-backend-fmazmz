package org.fmazmz.casemanager.user.model.rbac;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class RolePermissionId implements Serializable {
    private UUID roleId;
    private UUID permissionId;
}