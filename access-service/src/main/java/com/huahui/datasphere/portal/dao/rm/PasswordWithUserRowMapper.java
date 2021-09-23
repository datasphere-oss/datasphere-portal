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

import com.huahui.datasphere.portal.security.po.Password;
import com.huahui.datasphere.portal.security.po.User;


public class PasswordWithUserRowMapper extends PasswordRowMapper {
    @Override
    public Password mapRow(ResultSet rs, int rowNum) throws SQLException {
        final Password passwordPO = super.mapRow(rs, rowNum);
        final User user = new User();
        user.setId(rs.getInt("userId"));
        user.setLogin(rs.getString(User.Fields.LOGIN));
        user.setEmail(rs.getString(User.Fields.EMAIL));
        user.setEmailNotification(rs.getBoolean(User.Fields.EMAIL_NOTIFICATION));
        user.setLocale(rs.getString(User.Fields.LOCALE));
        user.setAdmin(rs.getBoolean(User.Fields.ADMIN));

        passwordPO.setUser(user);
        return passwordPO;
    }
}
