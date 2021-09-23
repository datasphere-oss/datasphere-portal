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

package com.huahui.datasphere.portal.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.huahui.datasphere.portal.dao.BaseDAO;
import com.huahui.datasphere.system.exception.PlatformFailureException;
import com.huahui.datasphere.system.exception.SystemExceptionIds;

/**
 * @author theseusyang
 * Base DAO class.
 */
public abstract class BaseDAOImpl implements BaseDAO {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDAOImpl.class);
    /**
     * JDBC template.
     */
    protected final JdbcTemplate jdbcTemplate;
    /**
     * Named parameter template.
     */
    public final NamedParameterJdbcTemplate namedJdbcTemplate;
    /**
     * Constructor.
     */
    public BaseDAOImpl(DataSource dataSource) {
        super();
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getDefaultDataSource() {
        return jdbcTemplate.getDataSource();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getBareConnection() {
        try {
            DataSource dataSource = jdbcTemplate.getDataSource();
            Objects.requireNonNull(dataSource, "DataSource cannot be null.");
            return dataSource.getConnection();
        } catch (SQLException e) {
            final String message = "Cannot get bare connection from JDBC template.";
            LOGGER.error(message);
            throw new PlatformFailureException(message, SystemExceptionIds.EX_SYSTEM_CONNECTION_GET, e);
        }
    }
    /**
     * @return the jdbcTemplate
     */
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    /**
     * @return the namedJdbcTemplate
     */
    public NamedParameterJdbcTemplate getNamedJdbcTemplate() {
        return namedJdbcTemplate;
    }
}
