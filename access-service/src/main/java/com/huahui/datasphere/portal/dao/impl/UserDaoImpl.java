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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.huahui.datasphere.portal.dao.DaoSqlQueryMapper;
import com.huahui.datasphere.portal.dao.LargeObjectsDAO;
import com.huahui.datasphere.portal.dao.RoleDao;
import com.huahui.datasphere.portal.dao.SecurityLabelDao;
import com.huahui.datasphere.portal.dao.SqlQuery;
import com.huahui.datasphere.portal.dao.UserDao;
import com.huahui.datasphere.portal.dao.rm.APIRowMapper;
import com.huahui.datasphere.portal.dao.rm.PasswordRowMapper;
import com.huahui.datasphere.portal.dao.rm.PasswordWithUserRowMapper;
import com.huahui.datasphere.portal.dao.rm.TokenRowMapper;
import com.huahui.datasphere.portal.dao.rm.UserEventRowMapper;
import com.huahui.datasphere.portal.dao.rm.UserPropertyRowMapper;
import com.huahui.datasphere.portal.dao.rm.UserPropertyValueRowMapper;
import com.huahui.datasphere.portal.dao.rm.UserRowMapper;
import com.huahui.datasphere.portal.dto.UserPropertyDTO;
import com.huahui.datasphere.portal.exception.CoreExceptionIds;
import com.huahui.datasphere.portal.security.po.Api;
import com.huahui.datasphere.portal.security.po.BaseUser;
import com.huahui.datasphere.portal.security.po.Password;
import com.huahui.datasphere.portal.security.po.Role;
import com.huahui.datasphere.portal.security.po.Token;
import com.huahui.datasphere.portal.security.po.User;
import com.huahui.datasphere.portal.security.po.UserEvent;
import com.huahui.datasphere.portal.security.po.UserProperty;
import com.huahui.datasphere.portal.security.po.UserPropertyValue;
import com.huahui.datasphere.portal.security.po.UserPropertyValue.FieldColumns;
import com.huahui.datasphere.portal.type.security.SecurityLabelInf;
import com.huahui.datasphere.portal.util.SecurityUtils;
import com.huahui.datasphere.system.exception.PlatformFailureException;
import com.huahui.datasphere.system.type.runtime.MeasurementPoint;
import com.huahui.datasphere.system.util.IdUtils;

/**
 * The Class UserDAOImpl.
 */
@Repository
public class UserDaoImpl extends BaseDAOImpl implements UserDao {

    private static final String CONNECTION_TABLE = "s_user_s_label_attribute_value";

    /** The password row mapper. */
    private final PasswordRowMapper passwordRowMapper = new PasswordRowMapper();

    /**
     * Password with user info row mapper
     */
    private final PasswordWithUserRowMapper passwordWithUserRowMapper = new PasswordWithUserRowMapper();

    /** The token row mapper. */
    private final TokenRowMapper tokenRowMapper = new TokenRowMapper();

    /** The role property row mapper. */
    private final UserPropertyRowMapper userPropertyRowMapper = new UserPropertyRowMapper();

    /**
     * Loads events by login and possibly given date.
     */
    @SqlQuery("LOAD_USER_EVENTS_BY_LOGIN_AND_DATE_SQL")
    private static String LOAD_USER_EVENTS_BY_LOGIN_AND_DATE_SQL;
    /**
     * Loads events by login and possibly given date and limit and offset.
     */
    @SqlQuery("LOAD_USER_EVENTS_BY_LOGIN_LIMIT_OFFSET_AND_DATE_SQL")
    private static String LOAD_USER_EVENTS_BY_LOGIN_LIMIT_OFFSET_AND_DATE_SQL;
    /**
     * Counts event by login.
     */
    @SqlQuery("COUNT_EVENT_BY_LOGIN")
    private static String COUNT_EVENT_BY_LOGIN;
    /**
     * Deletes event by id.
     */
    @SqlQuery("DELETE_EVENT_BY_EVENT_ID_SQL")
    private static String DELETE_EVENT_BY_EVENT_ID_SQL;
    /**
     * Delete selected events.
     */
    @SqlQuery("DELETE_EVENTS_BY_EVENT_IDS_SQL")
    private static String DELETE_EVENTS_BY_EVENT_IDS_SQL;
    /**
     * Deletes event by login and 'to' date.
     */
    @SqlQuery("DELETE_EVENTS_BY_LOGIN_AND_DATE_SQL")
    private static String DELETE_EVENTS_BY_LOGIN_AND_DATE_SQL;
    /**
     * Put an event by login.
     */
    @SqlQuery("PUT_USER_EVENT_BY_LOGIN_SQL")
    private static String PUT_USER_EVENT_BY_LOGIN_SQL;
    /**
     * Put an event by user id.
     */
    @SqlQuery("PUT_USER_EVENT_SQL")
    private static String PUT_USER_EVENT_SQL;

    /** The load all user properties. */
    @SqlQuery("LOAD_ALL_USER_PROPERTIES")
    private static String LOAD_ALL_USER_PROPERTIES;

    /** The load user property by name. */
    @SqlQuery("LOAD_USER_PROPERTY_BY_NAME")
    private static String LOAD_USER_PROPERTY_BY_NAME;

    /** The load user property by display name. */
    @SqlQuery("LOAD_USER_PROPERTY_BY_DISPLAY_NAME")
    private static String LOAD_USER_PROPERTY_BY_DISPLAY_NAME;

    /** The insert user property. */
    @SqlQuery("INSERT_USER_PROPERTY")
    private static String INSERT_USER_PROPERTY;

    /** The update user property by id. */
    @SqlQuery("UPDATE_USER_PROPERTY_BY_ID")
    private static String UPDATE_USER_PROPERTY_BY_ID;

    /** The delete user property values by user property id. */
    @SqlQuery("DELETE_USER_PROPERTY_VALUES_BY_USER_PROPERTY_ID")
    private static String DELETE_USER_PROPERTY_VALUES_BY_USER_PROPERTY_ID;

    /** The delete user property by id. */
    @SqlQuery("DELETE_USER_PROPERTY_BY_ID")
    private static String DELETE_USER_PROPERTY_BY_ID;

    /** The insert user property value. */
    @SqlQuery("INSERT_USER_PROPERTY_VALUE")
    private static String INSERT_USER_PROPERTY_VALUE;

    /** The update user property value by id. */
    @SqlQuery("UPDATE_USER_PROPERTY_VALUE_BY_ID")
    private static String UPDATE_USER_PROPERTY_VALUE_BY_ID;

    /** The delete user property values by ids. */
    @SqlQuery("DELETE_USER_PROPERTY_VALUES_BY_IDS")
    private static String DELETE_USER_PROPERTY_VALUES_BY_IDS;

    /** The delete user property values by user id. */
    @SqlQuery("DELETE_USER_PROPERTY_VALUES_BY_USER_ID")
    private static String DELETE_USER_PROPERTY_VALUES_BY_USER_ID;
    /** The load user property values by user ids. */
    @SqlQuery("LOAD_USER_PROPERTY_VALUES_BY_USER_IDS")
    private static String LOAD_USER_PROPERTY_VALUES_BY_USER_IDS;
    /**
     * Checks the user exists (simple count).
     */
    @SqlQuery("CHECK_USER_EXISTS_SQL")
    private static String CHECK_USER_EXISTS_SQL;
    @SqlQuery("SELECT_USERS_WITH_PROPERTIES")
    private static String selectUsersWithProperties;
    @SqlQuery("SELECT_USERS_ENDPOINTS")
    private static String selectUsersEndpoints;
    @SqlQuery("SELECT_USERS_ROLES")
    private static String selectUsersRoles;
    @SqlQuery("DELETE_USERS_PASSWORDS")
    private static String deleteUsersPasswords;
    @SqlQuery("DELETE_USERS_PROPERTIES_VALUES")
    private static String deleteUsersPropertiesValues;
    @SqlQuery("DELETE_USERS")
    private static String deleteUsers;
    @SqlQuery("INSERT_USERS")
    private static String insertUsers;
    @SqlQuery("INSERT_USERS_ROLES")
    private static String insertUsersRoles;
    @SqlQuery("INSERT_USERS_PROPERTIES_VALUES")
    private static String insertPropertiesValues;
    @SqlQuery("INSERT_USERS_APIS")
    private static String insertUsersApis;
    @SqlQuery("FETCH_ALL_USERS_PASSWORDS")
    private static String fetchAllUsersPasswords;
    @SqlQuery("FETCH_LAST_USER_PASSWORDS")
    private static String fetchLastUserPasswords;
    @SqlQuery("INSERT_USERS_PASSWORDS")
    private static String insertUsersPasswords;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private LargeObjectsDAO largeObjectsDAO;

    private final SecurityLabelDao securityLabelDao;

    /**
     * Instantiates a new user dao.
     *
     * @param dataSource
     *            the data source
     * @param sql
     *            the sql
     */
    @Autowired
    public UserDaoImpl(
            @Qualifier("coreDataSource") final DataSource dataSource,
            @Qualifier("security-sql") final Properties sql
    ) {
        super(dataSource);

        DaoSqlQueryMapper.fill(UserDaoImpl.class, this, sql);

        securityLabelDao = new SecurityLabelDaoImpl(CONNECTION_TABLE, dataSource, sql);
    }

   
    @Override
    @SuppressWarnings("unchecked")
    @Transactional
    public User create(final User user, final List<SecurityLabelInf> userLabels) {
        Map<String, Object> paramMap = new HashMap<>();
        final String createdBy = user.getCreatedBy() != null ? user.getCreatedBy() : SecurityUtils.getCurrentUserName();
        paramMap.put(User.Fields.CREATED_AT, new Timestamp(new Date().getTime()));
        paramMap.put(User.Fields.CREATED_BY, createdBy);
        paramMap.put(User.Fields.ACTIVE, user.isActive());
        paramMap.put(User.Fields.ADMIN, user.isAdmin());
        paramMap.put(User.Fields.EMAIL, user.getEmail());
        paramMap.put(User.Fields.LOCALE, user.getLocale());
        paramMap.put(User.Fields.FULL_NAME, user.getFullName());
        paramMap.put(User.Fields.LOGIN, user.getLogin());
        paramMap.put(User.Fields.NOTES, user.getNotes());
        paramMap.put(User.Fields.SOURCE, user.getSource());
        paramMap.put(User.Fields.EXTERNAL, user.isExternal());
        paramMap.put(User.Fields.EMAIL_NOTIFICATION, user.isEmailNotification());
        int rowsAffected = namedJdbcTemplate.update(User.Queries.INSERT_NEW, paramMap);

        if (rowsAffected == 0) {
            throw new PlatformFailureException("No record inserted while creating user",
                    CoreExceptionIds.EX_SECURITY_CANNOT_CREATE_USER);
        }
        final User userPO = findByLogin(user.getLogin());
        user.setId(userPO.getId());
        securityLabelDao.saveLabelsForObject(userPO.getId(), userLabels);

        final List<Role> roles = user.getRoles();
        final List<Map<String, Object>> params = new ArrayList<>();
        for (final Role role : roles) {
            final Map<String, Object> toAttach = new HashMap<>();
            toAttach.put(User.Fields.S_USERS_ID, userPO.getId());
            toAttach.put(Role.Fields.NAME, role.getName());
            params.add(toAttach);
        }
        namedJdbcTemplate.batchUpdate(User.Queries.ATTACH_TO_ROLE, params.toArray(new Map[params.size()]));
        paramMap.put(Password.Fields.S_USER_ID, user.getId());
        attachToAPI(user, paramMap);

        namedJdbcTemplate.update(Password.Queries.DEACTIVATE_BY_USER_ID, paramMap);

        if (!user.isExternal() && !CollectionUtils.isEmpty(user.getPassword())) {
            updatePassword(userPO.getId(), user.getPassword().get(0), false);
        }

        return user;
    }

    private void attachToAPI(final User user, Map<String, Object> paramMap) {
        if (user.getApis() != null && user.getApis().size() != 0) {
            Set<String> apiNames = user.getApis().stream().map(Api::getName).collect(Collectors.toSet());
            paramMap.put("api_names", apiNames);
            namedJdbcTemplate.update(User.Queries.DETACH_FROM_API, paramMap);
            namedJdbcTemplate.update(User.Queries.ATTACH_TO_API, paramMap);
        }
    }

   
    @Override
    @Transactional
    public boolean isExist(final String login) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.queryForObject(CHECK_USER_EXISTS_SQL, Long.class, login) > 0;
        } finally {
            MeasurementPoint.stop();
        }
    }

    
    @Override
    public User findByEmail(String email, String source) {
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(User.Fields.EMAIL, email);
        paramMap.put(User.Fields.SOURCE, source);
        final List<User> result = namedJdbcTemplate.query(User.Queries.SELECT_BY_EMAIL_SOURCE, paramMap,
                UserRowMapper.DEFAULT_USER_ROW_MAPPER);
        if (result == null || result.size() == 0) {
            return null;
        }
        final User user = result.get(0);

        fillUser(user);

        return user;
    }

    
    @Override
    @Transactional
    public User update(final String login, final User user, final List<SecurityLabelInf> userLabels) {

        final User userPO = findByLogin(login);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(User.Fields.ID, userPO.getId());
        paramMap.put(User.Fields.S_USER_ID, userPO.getId());

        paramMap = new HashMap<>();
        paramMap.put(User.Fields.ID, userPO.getId());
        paramMap.put(User.Fields.UPDATED_AT, user.getUpdatedAt());
        paramMap.put(User.Fields.UPDATED_BY, user.getUpdatedBy());
        paramMap.put(User.Fields.ACTIVE, user.isActive());
        paramMap.put(User.Fields.ADMIN, user.isAdmin());
        paramMap.put(User.Fields.EMAIL, user.getEmail());
        paramMap.put(User.Fields.LOCALE, user.getLocale());
        paramMap.put(User.Fields.FULL_NAME, user.getFullName());
        paramMap.put(User.Fields.LOGIN, user.getLogin());
        paramMap.put(User.Fields.NOTES, user.getNotes());
        paramMap.put(User.Fields.SOURCE, user.getSource());
        paramMap.put(User.Fields.EXTERNAL, user.isExternal());
        paramMap.put(User.Fields.EMAIL_NOTIFICATION, user.isEmailNotification());

        namedJdbcTemplate.update(User.Queries.UPDATE_BY_ID, paramMap);
        attachToAPI(user, paramMap);

        securityLabelDao.saveLabelsForObject(userPO.getId(), userLabels);

        final List<Role> roles = user.getRoles();
        final List<Map<String, Object>> params = new ArrayList<>();
        for (final Role role : roles) {
            final Map<String, Object> toAttach = new HashMap<>();
            toAttach.put(User.Fields.S_USERS_ID, userPO.getId());
            toAttach.put(Role.Fields.NAME, role.getName());
            params.add(toAttach);
        }
        paramMap.put(User.Fields.S_USERS_ID, userPO.getId());
        namedJdbcTemplate.update(User.Queries.CLEAN_USERS, paramMap);
        namedJdbcTemplate.batchUpdate(User.Queries.ATTACH_TO_ROLE, (params.toArray(new Map[params.size()])));
        if (!CollectionUtils.isEmpty(user.getPassword())) {
            updatePassword(userPO.getId(), user.getPassword().get(0), false);
        }

        return user;
    }

    @Override
    @Transactional
    public void updatePassword(Integer userId, Password password, boolean temp) {
        if (userId==null || password == null) {
            return;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(Password.Fields.S_USER_ID, userId);
		if (!temp) {
			namedJdbcTemplate.update(Password.Queries.DEACTIVATE_BY_USER_ID, paramMap);
            namedJdbcTemplate.update(Password.Queries.DEACTIVATE_ACTIVATION_CODES_BY_USER_ID, paramMap);
		}
        paramMap = new HashMap<>();
        paramMap.put(Password.Fields.UPDATED_AT, password.getUpdatedAt());
        paramMap.put(Password.Fields.UPDATED_BY, password.getUpdatedBy());
        paramMap.put(Password.Fields.CREATED_AT, password.getCreatedAt());
        paramMap.put(Password.Fields.CREATED_BY, SecurityUtils.getCurrentUserName());
        paramMap.put(Password.Fields.PASSWORD_TEXT, password.getPasswordText());
        paramMap.put(Password.Fields.ACTIVE, temp?false:true);
        paramMap.put(Password.Fields.ACTIVATION_CODE, password.getActivationCode());
        paramMap.put(Password.Fields.S_USER_ID, userId);
        namedJdbcTemplate.update(Password.Queries.INSERT_NEW, paramMap);
    }

    @Override
    @Transactional
    public boolean updateLocale(String login, String locale) {
        final User userPO = findByLogin(login);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(User.Fields.ID, userPO.getId());
        paramMap.put(User.Fields.LOCALE, locale);
        return namedJdbcTemplate.update(User.Queries.CHANGE_LOCALE_BY_ID, paramMap) > 0;
    }

 
    @Override
    @Transactional
    public List<User> getAll() {
        List<User> result = namedJdbcTemplate.query(User.Queries.SELECT_ALL, UserRowMapper.DEFAULT_USER_ROW_MAPPER);
        return result;
    }

    /**
     * {@inheritDoc }.
     *
     * @return the list
     */
    @Override
    public List<UserProperty> loadAllProperties() {
        return namedJdbcTemplate.query(LOAD_ALL_USER_PROPERTIES, userPropertyRowMapper);
    }

    /**
     * {@inheritDoc }.
     *
     * @param name
     *            the name
     * @return the user property po
     */
    @Override
    public UserProperty loadPropertyByName(String name) {
        List<UserProperty> list = namedJdbcTemplate.query(LOAD_USER_PROPERTY_BY_NAME,
                Collections.singletonMap("name", name), userPropertyRowMapper);

        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

  
    @Override
    public UserProperty loadPropertyByDisplayName(final String displayName) {
        final List<UserProperty> list = namedJdbcTemplate.query(LOAD_USER_PROPERTY_BY_DISPLAY_NAME,
                Collections.singletonMap("display_name", displayName), userPropertyRowMapper);

        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc }.
     *
     * @param property
     *            the property
     */
    @Override
    public void saveProperty(UserProperty property) {
        if (property.getId() == null) {
            // Insert property
            MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();

            sqlParameterSource.addValue("name", property.getName());
            sqlParameterSource.addValue("required", property.isRequired());
            sqlParameterSource.addValue("read_only", property.isReadOnly());
            sqlParameterSource.addValue("display_name", property.getDisplayName());
            sqlParameterSource.addValue("created_at", property.getCreatedAt());
            sqlParameterSource.addValue("updated_at", property.getUpdatedAt());
            sqlParameterSource.addValue("created_by", property.getCreatedBy());
            sqlParameterSource.addValue("updated_by", property.getUpdatedBy());
            sqlParameterSource.addValue("field_type", property.getFieldType());

            KeyHolder keyHolder = new GeneratedKeyHolder();

            namedJdbcTemplate.update(INSERT_USER_PROPERTY, sqlParameterSource, keyHolder, new String[] { "id" });

            property.setId(keyHolder.getKey().longValue());

        } else {
            // Update property
            MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();

            sqlParameterSource.addValue("name", property.getName());
            sqlParameterSource.addValue("required", property.isRequired());
            sqlParameterSource.addValue("read_only", property.isReadOnly());
            sqlParameterSource.addValue("field_type", property.getFieldType());
            sqlParameterSource.addValue("display_name", property.getDisplayName());
            sqlParameterSource.addValue("updated_at", property.getUpdatedAt());
            sqlParameterSource.addValue("updated_by", property.getUpdatedBy());
            sqlParameterSource.addValue("id", property.getId());

            namedJdbcTemplate.update(UPDATE_USER_PROPERTY_BY_ID, sqlParameterSource);
        }
    }

    /**
     * {@inheritDoc }.
     *
     * @param id
     *            the id
     */
    @Override
    public void deleteProperty(long id) {
        // Delete all values first.
        namedJdbcTemplate.update(DELETE_USER_PROPERTY_VALUES_BY_USER_PROPERTY_ID,
                Collections.singletonMap("userPropertyId", id));

        // Delete property.
        namedJdbcTemplate.update(DELETE_USER_PROPERTY_BY_ID, Collections.singletonMap("userPropertyId", id));
    }

    /**
     * {@inheritDoc }.
     *
     * @param propertyValues
     *            the property values
     */
    @Override
    public void saveUserPropertyValues(Collection<UserPropertyValue> propertyValues) {

        if (CollectionUtils.isEmpty(propertyValues)) {
            return;
        }

        List<UserPropertyValue> insertValues = new ArrayList<>();
        List<UserPropertyValue> updateValues = new ArrayList<>();

        for (UserPropertyValue propertyValue : propertyValues) {

            if (propertyValue.getId() == null) {
                insertValues.add(propertyValue);
            } else {
                updateValues.add(propertyValue);
            }
        }

        if (!CollectionUtils.isEmpty(insertValues)) {
            insertUserPropertyValues(insertValues);
        }

        if (!CollectionUtils.isEmpty(updateValues)) {
            Map<String, Object>[] map = createUserPropertyValueParams(updateValues);

            namedJdbcTemplate.batchUpdate(UPDATE_USER_PROPERTY_VALUE_BY_ID, map);
        }
    }

    /**
     * Insert user property values.
     *
     * @param propertyValues
     *            the property values
     */
    private void insertUserPropertyValues(List<UserPropertyValue> propertyValues) {

        if (CollectionUtils.isEmpty(propertyValues)) {
            return;
        }

        namedJdbcTemplate.batchUpdate(INSERT_USER_PROPERTY_VALUE, createUserPropertyValueParams(propertyValues));
    }

    /**
     * {@inheritDoc }.
     *
     * @param ids
     *            the ids
     */
    @Override
    public void deleteUserPropertyValuesByIds(Collection<Long> ids) {
        namedJdbcTemplate.update(DELETE_USER_PROPERTY_VALUES_BY_IDS, Collections.singletonMap("listId", ids));
    }

   
    @Override
    public void deleteUserPropertyValuesByUserId(long userId) {
        namedJdbcTemplate.update(DELETE_USER_PROPERTY_VALUES_BY_USER_ID, Collections.singletonMap("userId", userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserPropertyValue> loadUserPropertyValuesByUserId(Integer userId) {
        return loadUserPropertyValuesByUserIds(Collections.singleton(userId)).get(userId);
    }

   
    @Override
    public Map<Integer, List<UserPropertyValue>> loadUserPropertyValuesByUserIds(Collection<Integer> userIds) {

        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }

        return namedJdbcTemplate.query(LOAD_USER_PROPERTY_VALUES_BY_USER_IDS,
                Collections.singletonMap("listId", userIds), rs -> {
                    Map<Integer, List<UserPropertyValue>> result = new HashMap<>();

                    while (rs.next()) {
                        Integer userId = rs.getInt(FieldColumns.USER_ID.name());

                        List<UserPropertyValue> propValues = result.get(userId);
                        if (propValues == null) {
                            propValues = new ArrayList<>();
                            result.put(userId, propValues);
                        }

                        UserPropertyValue propValue = UserPropertyValueRowMapper.DEFAULT_ROW_MAPPER.mapRow(rs, 0);
                        propValues.add(propValue);
                    }

                    return result;
                });
    }

    /**
     * Fill user. TODO Reduce the number of queries performed in the loop
     *
     * @param user
     *            the user
     */
    private void fillUser(final User user) {
        final Map<String, Object> paramMap = new HashMap<>();

        paramMap.put(Password.Fields.S_USER_ID, user.getId());
        user.setPassword(
                namedJdbcTemplate.query(Password.Queries.SELECT_BY_USER_ID_ACTIVE_ONLY, paramMap, passwordRowMapper)
        );

        user.setLabelAttributeValues(securityLabelDao.findLabelsAttributesValuesForObject(user.getId()));
        paramMap.put(User.Fields.S_USERS_ID, user.getId());
        user.setRoles(roleDao.findRolesByUserLogin(user.getLogin()));
        user.setApis(namedJdbcTemplate.query(Api.Queries.SELECT_BY_USER_ID, paramMap,
                APIRowMapper.DEFAULT_API_ROW_MAPPER));
        user.setTokens(namedJdbcTemplate.query(Token.Queries.SELECT_BY_USER_ID, paramMap, tokenRowMapper));
        user.setProperties(loadUserPropertyValuesByUserId(user.getId()));
    }

    /**
     * Creates the user property value params.
     *
     * @param propertyValues
     *            the property values
     * @return the map[]
     */
    private Map<String, Object>[] createUserPropertyValueParams(List<UserPropertyValue> propertyValues) {
        @SuppressWarnings("unchecked")
        Map<String, Object>[] result = new Map[propertyValues.size()];

        for (int i = 0; i < propertyValues.size(); i++) {
            UserPropertyValue propertyValue = propertyValues.get(i);

            Map<String, Object> params = new HashMap<>();

            if (Objects.nonNull(propertyValue.getId())) {
                params.put("id", propertyValue.getId());
            }

            params.put("user_id", propertyValue.getUserId());

            if (propertyValue.getProperty() != null) {
                params.put("property_id", propertyValue.getProperty().getId());
            }

            params.put("value", propertyValue.getValue());
            params.put("created_at", propertyValue.getCreatedAt());
            params.put("updated_at", propertyValue.getUpdatedAt());
            params.put("created_by", propertyValue.getCreatedBy());
            params.put("updated_by", propertyValue.getUpdatedBy());

            result[i] = params;
        }

        return result;
    }

  
    @Override
    @Transactional
    public void saveProperties(final List<UserPropertyDTO> properties, final String login) {
        jdbcTemplate.update("DELETE from s_user_property_value where user_id = (select id from s_user where login=?)",
                login);
        if (!CollectionUtils.isEmpty(properties)) {
            properties.stream().filter(userProperty -> userProperty.getValue() != null).forEach(userProperty -> {
                // TODO: warn! replace it with batch insert instead of loop.
                jdbcTemplate.update(
                        "insert into s_user_property_value(user_id, value, property_id, created_at, created_by) "
                                + "values((select id from s_user where login=?), ?, (select id from s_user_property where name=?),current_timestamp, ?)",
                        login, userProperty.getValue(), userProperty.getName(), SecurityUtils.getCurrentUserName());
            });
        }
    }

   
    @Override
    @Transactional
    public boolean isLastAdmin() {
        return jdbcTemplate.queryForObject("select count(id) from s_user where active=true and admin=true",
                Long.class) == 1;
    }

   
    @Override
    @Transactional
    public void saveToken(Token tokenPO) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(Token.Fields.UPDATED_AT, tokenPO.getUpdatedAt());
        paramMap.put(Token.Fields.UPDATED_BY, tokenPO.getUpdatedBy());
        paramMap.put(Token.Fields.CREATED_AT, tokenPO.getCreatedAt());
        paramMap.put(Token.Fields.CREATED_BY, tokenPO.getCreatedBy());
        paramMap.put(Token.Fields.TOKEN, tokenPO.getToken());
        paramMap.put(Token.Fields.S_USER_ID, tokenPO.getUser().getId());
        namedJdbcTemplate.update(Token.Queries.INSERT_NEW, paramMap);

    }

   
    @Override
    public List<UserEvent> loadUserEvents(String login, Date from, int page, int count) {

        MeasurementPoint.start();
        try {
            boolean returnAll = page <= 0 || count <= 0;
            Timestamp point = from == null ? null : new Timestamp(from.getTime());
            if (returnAll) {
                return jdbcTemplate.query(LOAD_USER_EVENTS_BY_LOGIN_AND_DATE_SQL, UserEventRowMapper.DEFAULT_ROW_MAPPER,
                        login, point);
            } else {
                return jdbcTemplate.query(LOAD_USER_EVENTS_BY_LOGIN_LIMIT_OFFSET_AND_DATE_SQL,
                        UserEventRowMapper.DEFAULT_ROW_MAPPER, login, point, (page - 1) * count, count);
            }
        } finally {
            MeasurementPoint.stop();
        }
    }

   
    @Override
    public Long countUserEvents(String login) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.queryForObject(COUNT_EVENT_BY_LOGIN, Long.class, login);

        } finally {
            MeasurementPoint.stop();
        }
    }

    
    @Override
    public boolean deleteUserEvent(String eventId) {

        MeasurementPoint.start();
        try {
            largeObjectsDAO.cleanForSubjectIds(Collections.singletonList(eventId));
            return jdbcTemplate.update(DELETE_EVENT_BY_EVENT_ID_SQL, eventId) > 0;
        } finally {
            MeasurementPoint.stop();
        }
    }

  
    @Override
    public boolean deleteUserEvents(List<String> eventIds) {

        MeasurementPoint.start();
        try {
            largeObjectsDAO.cleanForSubjectIds(eventIds);

            Map<String, Object> params = new HashMap<>();
            params.put(UserEvent.FIELD_ID, eventIds);
            return namedJdbcTemplate.update(DELETE_EVENTS_BY_EVENT_IDS_SQL, params) > 0;
        } finally {
            MeasurementPoint.stop();
        }
    }

   
    @Override
    public boolean deleteAllUserEvents(String login, Date to) {

        MeasurementPoint.start();
        try {
            Timestamp point = to == null ? null : new Timestamp(to.getTime());
            return jdbcTemplate.update(DELETE_EVENTS_BY_LOGIN_AND_DATE_SQL, login, point) > 0;
        } finally {
            MeasurementPoint.stop();
        }
    }

   
    @Override
    public UserEvent create(UserEvent event) {
        MeasurementPoint.start();
        try {

            String newId = IdUtils.v1String();

            int result = jdbcTemplate.update(PUT_USER_EVENT_SQL, newId, event.getUserId(), event.getType(),
                    event.getContent(), event.getDetails(), event.getCreatedBy());

            if (result > 0) {
                event.setId(newId);
                return event;
            }

            return null;
        } finally {
            MeasurementPoint.stop();
        }
    }

   
    @Override
    public UserEvent create(UserEvent event, String login) {

        MeasurementPoint.start();
        try {

            String newId = IdUtils.v1String();

            int result = jdbcTemplate.update(PUT_USER_EVENT_BY_LOGIN_SQL, newId, login, event.getType(),
                    event.getContent(), event.getDetails(), event.getCreatedBy());

            if (result > 0) {
                event.setId(newId);
                return event;
            }

            return null;
        } finally {
            MeasurementPoint.stop();
        }
    }

   
    @Override
    public void deleteToken(String tokenString) {
        jdbcTemplate.update("delete from s_token cascade where token=?", tokenString);
    }

  
    @Override
    public User findByLogin(String login) {
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(User.Fields.LOGIN, login);
        final List<User> result = namedJdbcTemplate.query(User.Queries.SELECT_BY_LOGIN, paramMap,
                UserRowMapper.DEFAULT_USER_ROW_MAPPER);

        if (result == null || result.size() == 0) {
            return null;
        }
        final User user = result.get(0);

        fillUser(user);

        return user;
    }

    @Override
    public List<Api> getAPIList() {
        final List<Api> result = namedJdbcTemplate.query(Api.Queries.SELECT_ALL,
                APIRowMapper.DEFAULT_API_ROW_MAPPER);
        return result;
    }

    @Override
    public List<User> fetchUsersFullInfo() {
        final Map<Integer, User> users = jdbcTemplate.query(selectUsersWithProperties, this::extractUsers);
        jdbcTemplate.query(selectUsersEndpoints, rs -> {
            while (rs.next()) {
                final Api apiPO = new Api();
                apiPO.setName(rs.getString("apiName"));
                users.get(rs.getInt("userId")).addApi(apiPO);
            }
            return null;
        });
        jdbcTemplate.query(selectUsersRoles, rs -> {
            while (rs.next()) {
                final Role rolePO = new Role();
                rolePO.setName(rs.getString("roleName"));
                users.get(rs.getInt("userId")).addRole(rolePO);
            }
            return null;
        });
        securityLabelDao.fetchObjectsSecurityLabelsValues().forEach((userId, labelAttributeValuePOS) ->
                users.get(userId).setLabelAttributeValues(labelAttributeValuePOS)
        );
        return new ArrayList<>(users.values());
    }

    private Map<Integer, User> extractUsers(ResultSet rs) throws SQLException {
        final Map<Integer, User> result = new HashMap<>();
        while (rs.next()) {
            final int id = rs.getInt("id");
            final User userPO = result.computeIfAbsent(id, userId -> {
                final User user = new User();
                user.setId(userId);
                try {
                    user.setLogin(rs.getString("login"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setSource(rs.getString("source"));
                    user.setLocale(rs.getString("locale"));
                    user.setActive(rs.getBoolean("active"));
                    user.setAdmin(rs.getBoolean("admin"));
                    user.setExternal(rs.getBoolean("external"));
                    user.setEmailNotification(rs.getBoolean("email_notification"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return user;
            });
            if (rs.getString("propertyValue") != null) {
                userPO.addProperty(extractPropertyValueFromResultSet(rs));
            }
        }
        return result;
    }

    private UserPropertyValue extractPropertyValueFromResultSet(ResultSet rs) throws SQLException {
        final UserPropertyValue UserPropertyValue = new UserPropertyValue();
        final UserProperty property = new UserProperty();
        property.setName(rs.getString("propertyName"));
        UserPropertyValue.setProperty(property);
        UserPropertyValue.setValue(rs.getString("propertyValue"));
        return UserPropertyValue;
    }

    @Override
    public void deleteUsersByLogin(final List<String> logins) {
        if (CollectionUtils.isNotEmpty(logins)) {
            final Map<String, List<String>> loginParameter = Collections.singletonMap("logins", logins);
            namedJdbcTemplate.update(deleteUsersPasswords, loginParameter);
            namedJdbcTemplate.update(deleteUsersPropertiesValues, loginParameter);
            namedJdbcTemplate.update(deleteUsers, loginParameter);
        }
    }

    @Override
    public void saveUsers(List<User> users, Map<String, List<SecurityLabelInf>> userLabels) {
        insertUsers(users);
        insertUsersPropertiesValues(users);
        insertUsersApis(users);
        insertUserRoles(users);
        insertUserLabels(userLabels);
    }

    private void insertUsers(final List<User> users) {
        jdbcTemplate.batchUpdate(
                insertUsers,
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        final User user = users.get(i);
                        ps.setString(1, user.getLogin());
                        ps.setString(2, user.getEmail());
                        ps.setString(4, user.getFullName());
                        ps.setBoolean(5, user.isActive());
                        ps.setBoolean(6, user.isAdmin());
                        ps.setBoolean(7, user.isExternal());
                        ps.setString(8, user.getSource());
                        ps.setString(9, user.getLocale());
                        ps.setBoolean(10, user.isEmailNotification());
                    }

                    @Override
                    public int getBatchSize() {
                        return users.size();
                    }
                }
        );
    }

    private void insertUsersPropertiesValues(final List<User> users) {
        final Map[] values = users.stream()
                .filter(u -> CollectionUtils.isNotEmpty(u.getProperties()))
                .flatMap(u ->
                        u.getProperties().stream().map(p ->
                                Map.of(
                                        "login", u.getLogin(),
                                        "property", p.getProperty().getName(),
                                        "value", p.getValue(),
                                        "createdAt", new Date(),
                                        "createdBy", SecurityUtils.getCurrentUserName()
                                )
                        )
                )
                .toArray(Map[]::new);
        if (ArrayUtils.isNotEmpty(values)) {
            namedJdbcTemplate.batchUpdate(insertPropertiesValues, values);
        }
    }

    private void insertUsersApis(final List<User> users) {
        final Map[] links = users.stream()
                .filter(u -> CollectionUtils.isNotEmpty(u.getApis()))
                .flatMap(u ->
                        u.getApis().stream().map(a ->
                                Map.of("login", u.getLogin(), "name", a.getName())
                        )
                )
                .toArray(Map[]::new);
        if (ArrayUtils.isNotEmpty(links)) {
            namedJdbcTemplate.batchUpdate(insertUsersApis, links);
        }
    }

    private void insertUserRoles(final List<User> users) {
        final Map[] roles = users.stream()
                .filter(u -> CollectionUtils.isNotEmpty(u.getRoles()))
                .flatMap(u ->
                        u.getRoles().stream().map(r ->
                                Map.of(
                                        "login", u.getLogin(),
                                        "role", r.getName(),
                                        "createdAt", new Date(),
                                        "createdBy", SecurityUtils.getCurrentUserName()
                                )
                        )
                )
                .toArray(Map[]::new);
        if (ArrayUtils.isNotEmpty(roles)) {
            namedJdbcTemplate.batchUpdate(insertUsersRoles, roles);
        }
    }

    private void insertUserLabels(final  Map<String, List<SecurityLabelInf>> userLabels) {
        final Map<String, Integer> usersIds = getAll().stream()
                .collect(Collectors.toMap(BaseUser::getLogin, BaseUser::getId));
        if (MapUtils.isNotEmpty(userLabels)) {
            userLabels.forEach((key, value) -> securityLabelDao.saveLabelsForObject(usersIds.get(key), value));
        }
    }

    @Override
    public List<Password> fetchAllUsersPasswords() {
        return jdbcTemplate.query(fetchAllUsersPasswords, passwordWithUserRowMapper);
    }

    @Override
    public void saveUsersPasswords(List<Password> passwords) {
        if (CollectionUtils.isNotEmpty(passwords)) {
            final Map[] values = passwords.stream().map(p ->
                    Map.of(
                            "password", p.getPasswordText(),
                            "active", p.getActive(),
                            "login", p.getUser().getLogin()
                    )
            ).toArray(Map[]::new);
            namedJdbcTemplate.batchUpdate(insertUsersPasswords, values);
        }
    }

    @Override
    public List<Password> fetchLastUserPasswords(long userId, int lastCount) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(Password.Fields.S_USER_ID, userId);
        paramMap.put(Password.ROWS_LIMIT, lastCount);
        return namedJdbcTemplate.query(fetchLastUserPasswords, paramMap, passwordWithUserRowMapper);
    }

    @Override
    public void addUsersRoles(Map<String, Set<String>> userRoleNames) {
        final List<Map<String, Object>> params = new ArrayList<>();

        userRoleNames.forEach((key, value) -> value.forEach(roleName -> {
            final Map<String, Object> toAttach = new HashMap<>();
            toAttach.put(User.Fields.LOGIN, key);
            toAttach.put(Role.Fields.NAME, roleName);
            params.add(toAttach);
        }));

        namedJdbcTemplate.batchUpdate(User.Queries.ATTACH_TO_ROLE_BY_LOGIN, (params.toArray(new Map[params.size()])));
    }

	@Override
	public void activatePassword(String activationCode) {
		List<Integer> result=jdbcTemplate.queryForList("select s_user_id from s_password where activation_code=?", Integer.class, activationCode);
		if(result ==null|| result.size()==0) {
			return;
		}
		Integer userId = result.get(0);
		Map<String, Object> params = new HashMap<>();
		params.put(User.Fields.S_USER_ID, userId);
		params.put(Password.Fields.ACTIVATION_CODE, activationCode);
		namedJdbcTemplate.update(Password.Queries.DEACTIVATE_BY_USER_ID, params);
		namedJdbcTemplate.update(Password.Queries.ACTIVATE_PASSWORD, params);
	}


	
}
