package org.fmazmz.casemanager.user.model.rbac;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class RolePermissionId implements Serializable {
    private UUID roleId;
    private UUID permissionId;
}