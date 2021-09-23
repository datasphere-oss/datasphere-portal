/*
 * Apache License
 * 
 * Copyright (c) 2021 HuahuiData
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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
