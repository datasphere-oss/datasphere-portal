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

package com.huahui.datasphere.portal.type.security;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Denis Kostovarov
 */
public abstract class AbstractRole implements Role, Serializable {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -7221644432780048916L;
    /**
     * Name.
     */
    private String name;
    /**
     * Dsiplay name.
     */
    private String displayName;
    /**
     * Role type (system or user defined).
     */
    private RoleTypeInf roleTypeInf = RoleTypeInf.USER_DEFINED;
    /**
     * Security labels.
     */
    private List<SecurityLabelInf> securityLabelInfs;
    /**
     * Rights.
     */
    private List<RightInf> rights;

    /**
     * Custom properties.
     */
    private List<CustomProperty> customProperties;
    /**
     * Constructor.
     */
    public AbstractRole() {
        super();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public RoleTypeInf getRoleType() {
        return roleTypeInf;
    }

    public void setRoleType(final RoleTypeInf roleTypeInf) {
        this.roleTypeInf = roleTypeInf;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public List<RightInf> getRights() {
        return Objects.isNull(rights) ? Collections.emptyList() : Collections.unmodifiableList(rights);
    }

    public void setRights(final List<RightInf> rights) {
        this.rights = rights;
    }

    @Override
    public List<SecurityLabelInf> getSecurityLabels() {
        return Objects.isNull(securityLabelInfs) ? Collections.emptyList() : Collections.unmodifiableList(securityLabelInfs);
    }

    public void setSecurityLabels(final List<SecurityLabelInf> securityLabelInfs) {
        this.securityLabelInfs = securityLabelInfs;
    }

    /**
     * @return the customProperties
     */
    @Override
    public List<CustomProperty> getProperties() {
        return Objects.isNull(customProperties) ? Collections.emptyList() : Collections.unmodifiableList(customProperties);
    }
    /**
     * @param customProperties the customProperties to set
     */
    public void setCustomProperties(List<CustomProperty> customProperties) {
        this.customProperties = customProperties;
    }


}
