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

import com.huahui.datasphere.portal.security.po.Resource;

/**
 * Row mapper for the ResourcePO object.
 * @author theseusyang
 */
public class ResourceRowMapper implements RowMapper<Resource>{

    /**
     * Default row mapper.
     */
    public static final ResourceRowMapper DEFAULT_ROW_MAPPER
        = new ResourceRowMapper();

    /**
     * Constructor.
     */
    private ResourceRowMapper() {
        super();
    }

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public Resource mapRow(ResultSet rs, int rowNum) throws SQLException {
		Resource result = new Resource();
		result.setId(rs.getInt(Resource.Fields.ID));
		result.setParentId(rs.getInt(Resource.Fields.PARENT_ID) == 0 ? null : rs.getInt(Resource.Fields.PARENT_ID));
		result.setName(rs.getString(Resource.Fields.NAME));
		result.setRType(rs.getString(Resource.Fields.R_TYPE));
		result.setCategory(rs.getString(Resource.Fields.CATEGORY));
		result.setDisplayName(rs.getString(Resource.Fields.DISPLAY_NAME));
		result.setCreatedAt(rs.getTimestamp(Resource.Fields.CREATED_AT));
		result.setUpdatedAt(rs.getTimestamp(Resource.Fields.UPDATED_AT));
		result.setCreatedBy(rs.getString(Resource.Fields.CREATED_BY));
		result.setUpdatedBy(rs.getString(Resource.Fields.UPDATED_BY));
		return result;
	}

}
