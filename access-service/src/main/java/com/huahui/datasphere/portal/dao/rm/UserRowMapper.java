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

import com.huahui.datasphere.portal.security.po.User;

/**
 * Row mapper for the User object.
 * @author theseusyang
 */
public class UserRowMapper implements RowMapper<User> {

    /**
     * Default row mapper instance.
     */
    public static final UserRowMapper DEFAULT_USER_ROW_MAPPER
        = new UserRowMapper();

    /**
     * Constructor.
     */
    private UserRowMapper() {
        super();
    }

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet,
	 * int)
	 */
	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {

	    if(rs == null){
			return null;
		}

		User result = new User();
		result.setId(rs.getInt(User.Fields.ID));
		result.setLogin(rs.getString(User.Fields.LOGIN));
		result.setEmail(rs.getString(User.Fields.EMAIL));
		result.setLocale(rs.getString(User.Fields.LOCALE));
		// 去掉first_name, last_name 与 user_name有冲突
		result.setFullName(rs.getString(User.Fields.FULL_NAME));
		result.setNotes(rs.getString(User.Fields.NOTES));
		result.setCreatedAt(rs.getTimestamp(User.Fields.CREATED_AT));
		result.setUpdatedAt(rs.getTimestamp(User.Fields.UPDATED_AT));
		result.setCreatedBy(rs.getString(User.Fields.CREATED_BY));
		result.setUpdatedBy(rs.getString(User.Fields.UPDATED_BY));
		result.setActive(rs.getBoolean(User.Fields.ACTIVE));
		result.setAdmin(rs.getBoolean(User.Fields.ADMIN));
		result.setSource(rs.getString(User.Fields.SOURCE));
        result.setExternal(rs.getBoolean(User.Fields.EXTERNAL));
		result.setEmailNotification(rs.getBoolean(User.Fields.EMAIL_NOTIFICATION));
		return result;
	}

}
