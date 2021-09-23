/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.huahui.datasphere.portal.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.huahui.datasphere.portal.type.security.CustomProperty;
import com.huahui.datasphere.portal.type.security.RightInf;
import com.huahui.datasphere.portal.type.security.Role;
import com.huahui.datasphere.portal.type.security.RoleInf;
import com.huahui.datasphere.portal.type.security.RoleTypeInf;
import com.huahui.datasphere.portal.type.security.SecurityLabelInf;

/**
 * The Class RoleRO.
 */
public class RoleDTO extends BaseSecurityDTO implements RoleInf, Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 883041167295213217L;
    /** The name. */
    private String name;
    /** The display name. */
    private String displayName;
    /** The role type. */
    private RoleTypeInf type;
    /** The rights. */
    private List<RightInf> rights;
    /** The security labels. */
    private List<SecurityLabelInf> securityLabelInfs;
    /** Custom properties. */
    private List<CustomProperty> properties;
    /**
     * Gets the name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the role type.
     *
     * @return the role type
     */
    @Override
    public RoleTypeInf getRoleType() {
        return type;
    }

    /**
     * Sets the role type.
     *
     * @param roleTypeInf
     *            the new role type
     */
    public void setRoleType(RoleTypeInf roleTypeInf) {
        this.type = roleTypeInf;
    }

    /**
     * Gets the rights.
     *
     * @return the rights
     */
    @Override
    public List<RightInf> getRights() {
        return Objects.isNull(rights) ? Collections.emptyList() : Collections.unmodifiableList(rights);
    }

    /**
     * Sets the rights.
     *
     * @param rights
     *            the new rights
     */
    public void setRights(List<RightInf> rights) {
        this.rights = rights;
    }

    /**
	 * Gets the display name.
	 *
	 * @return the displayName
	 */
    @Override
    public String getDisplayName() {
        return displayName;
    }

    /**
	 * Sets the display name.
	 *
	 * @param displayName
	 *            the displayName to set
	 */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
	 * Gets the security labels.
	 *
	 * @return the security labels
	 */
    @Override
    public List<SecurityLabelInf> getSecurityLabels() {
        return Objects.isNull(securityLabelInfs) ? Collections.emptyList() : Collections.unmodifiableList(securityLabelInfs);
    }

    /**
	 * Sets the security labels.
	 *
	 * @param securityLabelInfs
	 *            the new security labels
	 */
    public void setSecurityLabels(final List<SecurityLabelInf> securityLabelInfs) {
        this.securityLabelInfs = securityLabelInfs;
    }

    /**
     * @return the properties
     */
    @Override
    public List<CustomProperty> getProperties() {
        return Objects.isNull(properties) ? Collections.emptyList() : Collections.unmodifiableList(properties);
    }

    /**
     * @param customProperties the properties to set
     */
    public void setProperties(List<CustomProperty> customProperties) {
        this.properties = customProperties;
    }

}
