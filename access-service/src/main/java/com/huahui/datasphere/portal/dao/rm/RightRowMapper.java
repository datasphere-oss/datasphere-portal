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

import com.huahui.datasphere.portal.security.po.Right;

/**
 * Row mapper for the RightPO class.
 * @author theseusyang
 */
public class RightRowMapper implements RowMapper<Right> {

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public Right mapRow(ResultSet rs, int rowNum) throws SQLException {
		Right result = new Right();
		result.setId(rs.getInt(Right.Fields.ID));
		result.setName(rs.getString(Right.Fields.NAME));
		result.setDescription(rs.getString(Right.Fields.DESCRIPTION));
		result.setCreatedAt(rs.getTimestamp(Right.Fields.CREATED_AT));
		result.setUpdatedAt(rs.getTimestamp(Right.Fields.UPDATED_AT));
		result.setCreatedBy(rs.getString(Right.Fields.CREATED_BY));
		result.setUpdatedBy(rs.getString(Right.Fields.UPDATED_BY));
		return result;
	}

}
