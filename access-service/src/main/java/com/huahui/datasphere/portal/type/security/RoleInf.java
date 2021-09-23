

package com.huahui.datasphere.portal.type.security;

import java.io.Serializable;
import java.util.List;

/**
 * A security role.
 * Also is RoleDTO
 */
public interface RoleInf extends Serializable {
    /**
     * Gets the name of the role.
     * @return name
     */
    String getName();
    /**
     * Gets the type of the role (system or user defined).
     * @return type
     */
    RoleTypeInf getRoleType();
    /**
     * Gets rights, defined for this role.
     * @return rights
     */
    List<RightInf> getRights();
    /**
     * Gets the display name of the role.
     * @return display name
     */
    String getDisplayName();
    /**
     * Gets the security labels, defined for this role.
     * @return security labels
     */
    List<SecurityLabelInf> getSecurityLabels();
    /**
     * Gets collection of the custom properties, defined for this user.
     * @return collection of custom properties
     */
    List<CustomProperty> getProperties();

}
