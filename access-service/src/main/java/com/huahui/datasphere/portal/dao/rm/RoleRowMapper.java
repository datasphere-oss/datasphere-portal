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

package com.huahui.datasphere.portal.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.huahui.datasphere.portal.security.po.Role;

/**
 *  Row mapper for the Role object.
 *  @author theseusyang
 */
public class RoleRowMapper implements RowMapper<Role> {

    /**
     * Default row mapper.
     */
    public static final RoleRowMapper DEFAULT_ROLE_ROW_MAPPER = new RoleRowMapper();

    /**
     * Constructor.
     */
    private RoleRowMapper() {
        super();
    }
	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet,
	 * int)
	 */
	@Override
	public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
		Role result = new Role();
		result.setId(rs.getInt(Role.Fields.ID));
		result.setName(rs.getString(Role.Fields.NAME));
		result.setRType(rs.getString(Role.Fields.R_TYPE));
		result.setDisplayName(rs.getString(Role.Fields.DISPLAY_NAME));
		result.setDescription(rs.getString(Role.Fields.DESCRIPTION));
		result.setCreatedAt(rs.getTimestamp(Role.Fields.CREATED_AT));
		result.setUpdatedAt(rs.getTimestamp(Role.Fields.UPDATED_AT));
		result.setCreatedBy(rs.getString(Role.Fields.CREATED_BY));
		result.setUpdatedBy(rs.getString(Role.Fields.UPDATED_BY));
		return result;
	}

}
