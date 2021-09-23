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

import com.huahui.datasphere.portal.security.po.Label;
import com.huahui.datasphere.portal.security.po.LabelAttribute;

/**
 * Row mapper for the Label object.
 * @author theseusyang
 */
public class LabelAttributeRowMapper implements RowMapper<LabelAttribute> {

    /**
     * Mapper singleton.
     */
    public static final LabelAttributeRowMapper DEFAULT_ROW_MAPPER = new LabelAttributeRowMapper();

    /**
     * Constructor.
     */
    private LabelAttributeRowMapper() {
        super();
    }
	
	@Override
	public LabelAttribute mapRow(ResultSet rs, int rowNum) throws SQLException {
		LabelAttribute result = new LabelAttribute();
		result.setId(rs.getInt(LabelAttribute.Fields.ID));
		result.setName(rs.getString(LabelAttribute.Fields.NAME));
		result.setValue(rs.getString(LabelAttribute.Fields.VALUE));
		result.setPath(rs.getString(LabelAttribute.Fields.PATH));
		result.setDescription(rs.getString(LabelAttribute.Fields.DESCRIPTION));
		result.setCreatedAt(rs.getTimestamp(LabelAttribute.Fields.CREATED_AT));
		result.setUpdatedAt(rs.getTimestamp(LabelAttribute.Fields.UPDATED_AT));
		result.setCreatedBy(rs.getString(LabelAttribute.Fields.CREATED_BY));
		result.setUpdatedBy(rs.getString(LabelAttribute.Fields.UPDATED_BY));
		result.setLabel(new Label(rs.getInt(LabelAttribute.Fields.S_LABEL_ID)));
		return result;
	}

}
