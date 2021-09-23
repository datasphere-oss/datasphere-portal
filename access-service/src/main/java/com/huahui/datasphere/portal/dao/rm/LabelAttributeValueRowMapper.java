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

import com.huahui.datasphere.portal.security.po.LabelAttribute;
import com.huahui.datasphere.portal.security.po.LabelAttributeValue;

/**
 * Row mapper for the LabelPO object.
 * @author theseusyang
 */
public class LabelAttributeValueRowMapper implements RowMapper<LabelAttributeValue> {

    /**
     * Instance.
     */
    public static final LabelAttributeValueRowMapper DEFAULT_ROW_MAPPER
        = new LabelAttributeValueRowMapper();

    /**
     * Constructor.
     */
    private LabelAttributeValueRowMapper() {
        super();
    }
	
	@Override
	public LabelAttributeValue mapRow(ResultSet rs, int rowNum) throws SQLException {
		LabelAttributeValue result = new LabelAttributeValue();
		result.setId(rs.getInt(LabelAttributeValue.Fields.ID));
		result.setValue(rs.getString(LabelAttributeValue.Fields.VALUE));
		result.setCreatedAt(rs.getTimestamp(LabelAttributeValue.Fields.CREATED_AT));
		result.setUpdatedAt(rs.getTimestamp(LabelAttributeValue.Fields.UPDATED_AT));
		result.setCreatedBy(rs.getString(LabelAttributeValue.Fields.CREATED_BY));
		result.setUpdatedBy(rs.getString(LabelAttributeValue.Fields.UPDATED_BY));
		result.setGroup(rs.getInt(LabelAttributeValue.Fields.S_LABEL_GROUP));
		result.setLabelAttribute(new LabelAttribute(rs.getInt(LabelAttributeValue.Fields.S_LABEL_ATTRIBUTE_ID)));
		return result;
	}

}
