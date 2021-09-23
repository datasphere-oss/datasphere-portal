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

import com.huahui.datasphere.portal.security.po.RoleProperty;
import com.huahui.datasphere.portal.security.po.UserProperty;
import com.huahui.datasphere.portal.security.po.UserPropertyValue;
import com.huahui.datasphere.portal.security.po.UserPropertyValue.FieldColumns;

/**
 * FIXDOC: add file description.
 *
 * @author theseusyang
 */
public class UserPropertyValueRowMapper implements RowMapper<UserPropertyValue> {

    /**
     * Dfeault instance singleton.
     */
    public static final UserPropertyValueRowMapper DEFAULT_ROW_MAPPER = new UserPropertyValueRowMapper();
    /**
     * Constructor.
     */
    private UserPropertyValueRowMapper() {
        super();
    }

    @Override
    public UserPropertyValue mapRow(ResultSet rs, int rowNum) throws SQLException {

        UserPropertyValue result = new UserPropertyValue();

        long id = rs.getLong(FieldColumns.ID.name());
        result.setId(rs.wasNull() ? null : id);

        result.setUserId(rs.getLong(FieldColumns.USER_ID.name()));
        result.setValue(rs.getString(FieldColumns.VALUE.name()));

        long propertyId = rs.getLong(FieldColumns.PROPERTY_ID.name());

        UserProperty property = new UserProperty();
        property.setId(propertyId);
        property.setName(rs.getString(RoleProperty.FieldColumns.NAME.name()));
        property.setDisplayName(rs.getString(RoleProperty.FieldColumns.DISPLAY_NAME.name()));

        result.setProperty(property);

        return result;
    }
}
