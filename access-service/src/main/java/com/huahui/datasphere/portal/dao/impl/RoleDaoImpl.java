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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huahui.datasphere.portal.dao.DaoSqlQueryMapper;
import com.huahui.datasphere.portal.dao.RoleDao;
import com.huahui.datasphere.portal.dao.SecurityLabelDao;
import com.huahui.datasphere.portal.dao.SqlQuery;
import com.huahui.datasphere.portal.dao.rm.LabelAttributeRowMapper;
import com.huahui.datasphere.portal.dao.rm.LabelRowMapper;
import com.huahui.datasphere.portal.dao.rm.ResourceRowMapper;
import com.huahui.datasphere.portal.dao.rm.RightRowMapper;
import com.huahui.datasphere.portal.dao.rm.RolePropertyRowMapper;
import com.huahui.datasphere.portal.dao.rm.RolePropertyValueRowMapper;
import com.huahui.datasphere.portal.dao.rm.RoleRowMapper;
import com.huahui.datasphere.portal.security.po.Label;
import com.huahui.datasphere.portal.security.po.LabelAttribute;
import com.huahui.datasphere.portal.security.po.LabelAttributeValue;
import com.huahui.datasphere.portal.security.po.Resource;
import com.huahui.datasphere.portal.security.po.ResourceRight;
import com.huahui.datasphere.portal.security.po.Right;
import com.huahui.datasphere.portal.security.po.Role;
import com.huahui.datasphere.portal.security.po.RoleProperty;
import com.huahui.datasphere.portal.security.po.RolePropertyValue;
import com.huahui.datasphere.portal.security.po.RolePropertyValue.FieldColumns;
import com.huahui.datasphere.portal.type.security.RightInf;
import com.huahui.datasphere.portal.type.security.SecuredResourceCategoryInf;
import com.huahui.datasphere.portal.type.security.SecurityLabelInf;
import com.huahui.datasphere.portal.util.SecurityUtils;
import com.huahui.datasphere.system.type.runtime.MeasurementPoint;

/**
 * The Class RoleDAO.
 */
@Repository
public class RoleDaoImpl extends BaseDAOImpl implements RoleDao {

    private static final String CONNECTION_TABLE = "s_role_s_label_attribute_value";
    private static final ResultSetExtractor<Map<Integer, Role>> ROLES_WITH_PROPERTIES_RESULT_EXTRACTOR = rs -> {
        final Map<Integer, Role> result = new HashMap<>();
        while (rs.next()) {
            final int id = rs.getInt("id");
            result.computeIfAbsent(id, roleId -> {
                final Role role = new Role();
                try {
                    role.setId(roleId);
                    role.setName(rs.getString("name"));
                    role.setDisplayName(rs.getString("display_name"));
                    role.setRType(rs.getString("r_type"));
                    role.setDescription(rs.getString("description"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return role;
            });
            final String propertyName = rs.getString("propertyName");
            if (propertyName != null) {
                final RolePropertyValue rolePropertyValuePO = new RolePropertyValue();
                rolePropertyValuePO.setRoleId((long) id);
                final RoleProperty property = new RoleProperty();
                property.setId(-1L);
                property.setName(propertyName);
                rolePropertyValuePO.setProperty(property);
                rolePropertyValuePO.setValue(rs.getString("propertyValue"));
                result.get(id).addProperty(rolePropertyValuePO);
            }
        }
        return result;
    };

    //region SQLQueryMapping
    /**
     * Combined roles query.
     */
    @SqlQuery("LOAD_ROLES_RESOURCES_AND_RIGHTS_BY_USER_LOGIN_SQL")
    private String LOAD_ROLES_RESOURCES_AND_RIGHTS_BY_USER_LOGIN_SQL;
    @SqlQuery("LOAD_ALL_ROLE_PROPERTIES")
    private String LOAD_ALL_ROLE_PROPERTIES;
    @SqlQuery("LOAD_NEW_ROLE_PROPERTIES")
    private String LOAD_NEW_ROLE_PROPERTIES;
    @SqlQuery("LOAD_ROLE_PROPERTY_BY_NAME")
    private String LOAD_ROLE_PROPERTY_BY_NAME;
    @SqlQuery("LOAD_ROLE_PROPERTY_BY_DISPLAY_NAME")
    private String LOAD_ROLE_PROPERTY_BY_DISPLAY_NAME;
    @SqlQuery("INSERT_ROLE_PROPERTY")
    private String INSERT_ROLE_PROPERTY;
    @SqlQuery("UPDATE_ROLE_PROPERTY_BY_ID")
    private String UPDATE_ROLE_PROPERTY_BY_ID;
    @SqlQuery("DELETE_ROLE_PROPERTY_VALUES_BY_ROLE_PROPERTY_ID")
    private String DELETE_ROLE_PROPERTY_VALUES_BY_ROLE_PROPERTY_ID;
    @SqlQuery("DELETE_ROLE_PROPERTY_BY_ID")
    private String DELETE_ROLE_PROPERTY_BY_ID;
    @SqlQuery("INSERT_ROLE_PROPERTY_VALUE")
    private String INSERT_ROLE_PROPERTY_VALUE;
    @SqlQuery("UPDATE_ROLE_PROPERTY_VALUE_BY_ID")
    private String UPDATE_ROLE_PROPERTY_VALUE_BY_ID;
    @SqlQuery("DELETE_ROLE_PROPERTY_VALUES_BY_IDS")
    private String DELETE_ROLE_PROPERTY_VALUES_BY_IDS;
    @SqlQuery("DELETE_ROLE_PROPERTY_VALUES_BY_ROLE_ID")
    private String DELETE_ROLE_PROPERTY_VALUES_BY_ROLE_ID;
    @SqlQuery("LOAD_ROLE_PROPERTY_VALUES_BY_ROLE_IDS")
    private String LOAD_ROLE_PROPERTY_VALUES_BY_ROLE_IDS;
    // resources queries
    @SqlQuery("INSERT_RESOURCE_SQL")
    private String INSERT_RESOURCE_SQL;
    @SqlQuery("DROP_ALL_NON_SYSTEM_RESOURCES_SQL")
    private String DROP_ALL_NON_SYSTEM_RESOURCES_SQL;
    @SqlQuery("DELETE_RESOURCES_BY_CATEGORY_SQL")
    private String DELETE_RESOURCES_BY_CATEGORY_SQL;
    @SqlQuery("LOAD_ALL_RESOURCES_SQL")
    private String LOAD_ALL_RESOURCES_SQL;
    @SqlQuery("UPDATE_RESOURCE_DISPLAY_NAME_SQL")
    private String updateResourceDisplayNameSql;
    @SqlQuery("INSERT_ROLE_RESOURCE_RIGHT_LINK_SQL")
    private String insertRoleResourceRightLinkSql;
    @SqlQuery("UPDATE_ROLE_RESOURCE_RIGHT_LINK_SQL")
    private String updateRoleResourceRightLinkSql;
    @SqlQuery("DELETE_BY_ROLE_RESOURCE_RIGHT_LINK_SQL")
    private String deleteByRoleResourceRightLinkSql;
    @SqlQuery("SELECT_ROLES_WITH_PROPERTIES")
    private String selectRolesWithProperties;
    @SqlQuery("SELECT_ROLES_LABELS")
    private String selectRolesLabels;
    @SqlQuery("SELECT_ROLES_RESOURCE_RIGHTS")
    private String selectRolesResourceRights;
    @SqlQuery("DELETE_ROLES_BY_NAME")
    private String deleteRolesByName;
    @SqlQuery("DELETE_ROLES_RIGHTS_BY_NAME")
    private String deleteRolesRightsByRolesNames;
    @SqlQuery("DELETE_USERS_ROLES_BY_ROLES_NAMES")
    private String deleteUsersRolesByRolesNames;
    @SqlQuery("DELETE_ROLES_PROPERTIES_BY_ROLES_NAMES")
    private String deleteRolesPropertiesByRolesNames;
    @SqlQuery("SELECT_ROLES_WITH_PROPERTIES_BY_ROLES_NAMES")
    private String selectRolesWithPropertiesByNames;
    @SqlQuery("SELECT_ROLE_RESOURCES_BY_ROLE_ID")
    private String selectRoleResourcesByRoleId;
    //endregion

    /**
     * The right row mapper.
     */
    private RightRowMapper rightRowMapper = new RightRowMapper();

    /**
     * The role property row mapper.
     */
    private RolePropertyRowMapper rolePropertyRowMapper = new RolePropertyRowMapper();

    /**
     * The role property value row mapper.
     */
    private RolePropertyValueRowMapper rolePropertyValueRowMapper = new RolePropertyValueRowMapper();

    private final SecurityLabelDao securityLabelDao;

    /**
     * Instantiates a new role dao.
     *
     * @param dataSource the data source
     */
    @Autowired
    public RoleDaoImpl(
            @Qualifier("coreDataSource") final DataSource dataSource,
            @Qualifier("securitySql") final Properties sql
    ) {
        super(dataSource);

        DaoSqlQueryMapper.fill(RoleDaoImpl.class, this, sql);

        securityLabelDao = new SecurityLabelDaoImpl(CONNECTION_TABLE, dataSource, sql);
    }

    /**
     * Creates the.
     *
     * @param role the role
     * @return the role po
     */
  
    @Override
    @Transactional
    public Role create(Role role) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(Role.Fields.UPDATED_AT, role.getUpdatedAt());
        paramMap.put(Role.Fields.UPDATED_BY, role.getUpdatedBy());
        paramMap.put(Role.Fields.CREATED_AT, role.getCreatedAt());
        paramMap.put(Role.Fields.CREATED_BY, role.getCreatedBy());
        paramMap.put(Role.Fields.NAME, role.getName());
        paramMap.put(Role.Fields.R_TYPE, role.getRType());
        paramMap.put(Role.Fields.DESCRIPTION, role.getDescription());
        paramMap.put(Role.Fields.DISPLAY_NAME, role.getDisplayName());
        namedJdbcTemplate.update(Role.Queries.INSERT_NEW, paramMap);
        role.setId(findByName(role.getName()).getId());
        return role;
    }

    /**
     * Find by name.
     *
     * @param name the name
     * @return the role po
     */
  
    @Override
    @Transactional
    public Role findByName(String name) {

        MeasurementPoint.start();
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(Role.Fields.NAME, name);

            List<Role> result = namedJdbcTemplate.query(
                    Role.Queries.SELECT_BY_NAME,
                    paramMap,
                    RoleRowMapper.DEFAULT_ROLE_ROW_MAPPER);

            if (CollectionUtils.isEmpty(result)) {
                return null;
            }

            Role role = result.get(0);
            paramMap.put(Right.Fields.S_ROLE_ID, role.getId());

            SqlRowSet rs = jdbcTemplate.queryForRowSet(selectRoleResourcesByRoleId, role.getId());

            while (rs.next()) {
                Right right = fillRightData(rs);

                fillResource(rs, role, right);
            }
            role.setLabels(findSecurityLabelsByRoleName(role.getName()));
            role.setLabelAttributeValues(securityLabelDao.findLabelsAttributesValuesForObject(role.getId()));
            role.setProperties(loadRolePropertyValuesByRoleId(role.getId()));
            return role;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Find right by name.
     *
     * @param name the name
     * @return the right po
     */
  
    @Override
    @Transactional
    public Right findRightByName(String name) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(Right.Fields.NAME, name);
        List<Right> result = namedJdbcTemplate.query(Right.Queries.SELECT_BY_NAME, paramMap, rightRowMapper);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }

    /**
     * Find resource by name.
     *
     * @param name the name
     * @return the resource po
     */
  
    @Override
    @Transactional
    public Resource findResourceByName(String name) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(Resource.Fields.NAME, name);
        List<Resource> result = namedJdbcTemplate.query(Resource.Queries.SELECT_BY_NAME, paramMap,
                ResourceRowMapper.DEFAULT_ROW_MAPPER);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }

    /**
     * Update.
     *
     * @param name the name
     * @param role the role
     * @return the role po
     */
   
    @Override
    @Transactional
    public Role update(String name, Role role) {
        Map<String, Object> params = new HashMap<>();
        params.put(Role.Fields.UPDATED_AT, role.getUpdatedAt());
        params.put(Role.Fields.UPDATED_BY, role.getUpdatedBy());
        params.put(Role.Fields.CREATED_AT, role.getCreatedAt());
        params.put(Role.Fields.CREATED_BY, role.getCreatedBy());
        params.put(Role.Fields.NAME, role.getName());
        params.put(Role.Fields.R_TYPE, role.getRType());
        params.put(Role.Fields.DESCRIPTION, role.getDescription());
        params.put(Role.Fields.DISPLAY_NAME, role.getDisplayName());
        namedJdbcTemplate.update(Role.Queries.UPDATE_BY_NAME, params);
        return role;
    }

    /**
     * Update.
     *
     * @param roleName the role name
     * @param newRole the new role
     * @param securityLabelInfs the label names
     */
    
    @Override
    @SuppressWarnings("unchecked")
    public void update(String roleName, Role newRole, List<SecurityLabelInf> securityLabelInfs) {

        // 1. Role general
        update(roleName, newRole);

        // 2. Labels
        Role updated = findByName(newRole.getName());

        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(Role.Fields.S_ROLE_ID, updated.getId());
        namedJdbcTemplate.update(Role.Queries.CLEAN_LABELS, paramMap);

        if (CollectionUtils.isNotEmpty(securityLabelInfs)) {
            final List<Map<String, Object>> params = new ArrayList<>();
            securityLabelInfs.stream().map(SecurityLabelInf::getName).forEach(labelName -> {
                Map<String, Object> toAttach = new HashMap<>();
                toAttach.put(Role.Fields.S_ROLE_ID, updated.getId());
                toAttach.put(Role.Fields.NAME, labelName);
                params.add(toAttach);
            });
            namedJdbcTemplate.batchUpdate(Role.Queries.ATTACH_LABELS, params.toArray(new Map[params.size()]));
        }

        securityLabelDao.saveLabelsForObject(updated.getId(), securityLabelInfs);
        securityLabelDao.cleanUsersLabels(roleName);

        // 3. Single permissions connect
        List<ResourceRight> resourcesToCreate = CollectionUtils.isEmpty(newRole.getConnectedResourceRights())
                ? Collections.emptyList()
                : newRole.getConnectedResourceRights().stream().filter(po -> po.getId() == null).collect(Collectors.toList());
        List<ResourceRight> resourcesToUpdate = CollectionUtils.isEmpty(newRole.getConnectedResourceRights())
                ? Collections.emptyList()
                : newRole.getConnectedResourceRights().stream().filter(po -> po.getId() != null).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(resourcesToCreate)) {
            jdbcTemplate.batchUpdate(insertRoleResourceRightLinkSql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ResourceRight po = resourcesToCreate.get(i);
                    ps.setInt(1, updated.getId());
                    ps.setInt(2, po.getResource().getId());
                    ps.setInt(3, po.getRight().getId());
                    ps.setString(4, po.getCreatedBy());
                }

                @Override
                public int getBatchSize() {
                    return resourcesToCreate.size();
                }
            });
        }

        if (!CollectionUtils.isEmpty(resourcesToUpdate)) {
            jdbcTemplate.batchUpdate(updateRoleResourceRightLinkSql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ResourceRight po = resourcesToUpdate.get(i);
                    ps.setString(1, po.getUpdatedBy());
                    ps.setInt(2, po.getId());
                }

                @Override
                public int getBatchSize() {
                    return resourcesToUpdate.size();
                }
            });
        }

        // 4. Single permissions disconnect
        if (!CollectionUtils.isEmpty(newRole.getDisconnectedResourceRights())) {
            jdbcTemplate.batchUpdate(deleteByRoleResourceRightLinkSql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ResourceRight po = newRole.getDisconnectedResourceRights().get(i);
                    ps.setInt(1, po.getRole().getId());
                    ps.setInt(2, po.getResource().getId());
                    ps.setInt(3, po.getRight().getId());
                }

                @Override
                public int getBatchSize() {
                    return newRole.getDisconnectedResourceRights().size();
                }
            });
        }
    }

    /**
     * Delete.
     *
     * @param name the name
     */
    
    @Override
    @Transactional
    public void delete(String name) {
        Map<String, Object> params = new HashMap<>();
        Role toDelete = findByName(name);
        params.put(Role.Fields.ID, toDelete.getId());
        params.put(Role.Fields.S_ROLE_ID, toDelete.getId());
        params.put(Role.Fields.S_ROLES_ID, toDelete.getId());
        namedJdbcTemplate.update(Role.Queries.CLEAN_PROPERTIES, params);
        namedJdbcTemplate.update(Role.Queries.CLEAN_RESOURCES, params);
        namedJdbcTemplate.update(Role.Queries.CLEAN_USERS, params);
        namedJdbcTemplate.update(Role.Queries.CLEAN_LABELS, params);
        namedJdbcTemplate.update(Role.Queries.DELETE_BY_ID, params);
    }

    /**
     * Gets the all.
     *
     * @return the all
     */
    
    @Override
    @Transactional
    public List<Role> getAll() {
        MeasurementPoint.start();
        try {
            List<Role> roles = namedJdbcTemplate.query(Role.Queries.SELECT_ALL,
                    RoleRowMapper.DEFAULT_ROLE_ROW_MAPPER);

            Map<Integer, List<RolePropertyValue>> properties
                    = loadRolePropertyValuesByRoleIds(roles.stream()
                    .map(Role::getId)
                    .collect(Collectors.toCollection(ArrayList::new)));

            for (Role rolePO : roles) {
                rolePO.setLabels(findSecurityLabelsByRoleName(rolePO.getName()));
                rolePO.setLabelAttributeValues(securityLabelDao.findLabelsAttributesValuesForObject(rolePO.getId()));
                rolePO.setProperties(properties.get(rolePO.getId()));
            }

            return roles;

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets combined roles, rights and resources by user login name.
     *
     * @param login the user login
     * @return list of roles.
     */
    @Override
    public List<Role> findRolesByUserLogin(String login) {

        Map<Integer, Role> roles = new HashMap<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(LOAD_ROLES_RESOURCES_AND_RIGHTS_BY_USER_LOGIN_SQL, login, login);

        while (rs.next()) {

            Role current = fillRoleData(roles, rs);

            Right right = fillRightData(rs);

            fillResource(rs, current, right);
        }

        Map<Integer, List<RolePropertyValue>> properties
                = loadRolePropertyValuesByRoleIds(roles.keySet());

        properties.forEach((key, value) -> {

            Role r = roles.get(key);
            if (Objects.nonNull(r)) {
                r.setProperties(value);
            }
        });

        Map<Integer, List<LabelAttributeValue>> securityLabels = loadRoleSecurityLabelsByRoleIds(roles.keySet());
        securityLabels.forEach((key, value) -> {
            Role r = roles.get(key);
            if (r != null) {
                r.setLabelAttributeValues(value);
            }
        });

        return new ArrayList<>(roles.values());
    }

    private Role fillRoleData(Map<Integer, Role> roles, SqlRowSet rs) {
        Integer roleId = rs.getInt("role_id");
        Role current = roles.get(roleId);
        if (current == null) {
            current = new Role();
            current.setId(roleId);
            current.setName(rs.getString("role_name"));
            current.setRType(rs.getString("role_type"));
            current.setDisplayName(rs.getString("role_display_name"));
            current.setDescription(rs.getString("role_description"));

            roles.put(roleId, current);
        }
        return current;
    }

    private Right fillRightData(SqlRowSet rs) {
        int rightId = rs.getInt("right_id");
        Right right = new Right();
        if (rightId != 0) {
            right.setId(rightId);
            right.setName(rs.getString("right_name"));
            right.setDescription(rs.getString("right_description"));
        }
        return right;
    }

    private void fillResource(SqlRowSet rs, Role current, Right right) {

        Resource resource = new Resource();
        int resourceId = rs.getInt("resource_id");
        if (resourceId != 0) {
            resource.setId(rs.getInt("resource_id"));
            resource.setName(rs.getString("resource_name"));
            resource.setRType(rs.getString("resource_type"));
            resource.setCategory(rs.getString("resource_category"));
            resource.setDisplayName(rs.getString("resource_display_name"));
        }
        int resourceRightId = rs.getInt("resource_right_id");
        if (resourceRightId != 0) {
            ResourceRight resourceRight = new ResourceRight();
            resourceRight.setId(resourceRightId);
            resourceRight.setRight(right);
            resourceRight.setResource(resource);
            resourceRight.setRole(current);

            if (current.getConnectedResourceRights() == null) {
                current.setConnectedResourceRights(new ArrayList<>());
            }

            current.getConnectedResourceRights().add(resourceRight);
        }

    }

    private Map<Integer, List<LabelAttributeValue>> loadRoleSecurityLabelsByRoleIds(Set<Integer> ids) {
        return ids.stream()
                .collect(Collectors.toMap(Function.identity(), securityLabelDao::findLabelsAttributesValuesForObject));
    }

    private List<Label> toLabelsPOs(List<LabelAttributeValue> labelsAttributesValuesForObject) {
        final Map<Integer, Label> labelsPOs = new HashMap<>();
        labelsAttributesValuesForObject.stream()
                .map(lav -> lav.getLabelAttribute().getLabel())
                .forEach(l -> labelsPOs.putIfAbsent(l.getId(), l));
        return new ArrayList<>(labelsPOs.values());
    }

    /**
     * Gets the all secured res.
     *
     * @return the all secured res
     */
  
    @Override
    @Transactional
    public List<Resource> getAllSecurityResources() {
        return jdbcTemplate.query(LOAD_ALL_RESOURCES_SQL, ResourceRowMapper.DEFAULT_ROW_MAPPER);
    }

    /**
     * Gets the all security labels.
     *
     * @return the all security labels
     */
    
    @Override
    @Transactional
    public List<Label> getAllSecurityLabels() {
        List<Label> result = namedJdbcTemplate.query(Label.Queries.SELECT_ALL,
                LabelRowMapper.DEFAULT_LABEL_ROW_MAPPER);
        attachAttributes(result);
        return result;
    }

    /**
     * Find security label by name.
     *
     * @param name the name
     * @return the label po
     */
    @Override
    @Transactional
    public Label findSecurityLabelByName(String name) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(Label.Fields.NAME, name);
        List<Label> result = namedJdbcTemplate.query(Label.Queries.SELECT_BY_NAME, paramMap,
                LabelRowMapper.DEFAULT_LABEL_ROW_MAPPER);
        attachAttributes(result);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }

        return result.get(0);
    }

    /*
     * TODO: merge this part with queries from above and below.
     */
    private void attachAttributes(List<Label> labels) {

        if (labels == null) {
            return;
        }

        for (Label labelPO : labels) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(LabelAttribute.Fields.S_LABEL_ID, labelPO.getId());
            List<LabelAttribute> result = namedJdbcTemplate.query(LabelAttribute.Queries.SELECT_BY_LABEL_ID,
                    paramMap, LabelAttributeRowMapper.DEFAULT_ROW_MAPPER);
            labelPO.setLabelAttribute(result);
        }
    }

    @Override
    @Transactional
    public List<Label> findSecurityLabelsByRoleName(String roleName) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(Label.Fields.ROLE_NAME, roleName);
        List<Label> result = namedJdbcTemplate.query(Label.Queries.SELECT_BY_ROLE_NAME, paramMap,
                LabelRowMapper.DEFAULT_LABEL_ROW_MAPPER);

        //attachAttributes(result);
        if (CollectionUtils.isEmpty(result)) {
            return Collections.emptyList();
        }

        return result;
    }

    /**
     * Delete security label by name.
     *
     * @param name the name
     */
    
    @Override
    @Transactional
    public void deleteSecurityLabelByName(String name) {
        Map<String, Object> params = new HashMap<>();
        Label toDelete = findSecurityLabelByName(name);
        if (toDelete == null) {
            return;
        }
        params.put(Label.Fields.ID, toDelete.getId());
        params.put(Label.Fields.S_LABEL_ID, toDelete.getId());
        namedJdbcTemplate.update(Label.Queries.CLEAN_ROLE_LABELS, params);
        namedJdbcTemplate.update(Label.Queries.CLEAN_LABEL_ATTRIBUTE_VALUES, params);
        namedJdbcTemplate.update(Label.Queries.CLEAN_LABEL_ATTRIBUTES, params);
        namedJdbcTemplate.update(Label.Queries.DELETE_BY_ID, params);

    }

    /**
     * Creates the security label.
     *
     * @param label the label
     */
    
    @Override
    @SuppressWarnings("unchecked")
    @Transactional
    public void createSecurityLabel(Label label) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(Label.Fields.UPDATED_AT, label.getUpdatedAt());
        paramMap.put(Label.Fields.UPDATED_BY, label.getUpdatedBy());
        paramMap.put(Label.Fields.CREATED_AT, label.getCreatedAt());
        paramMap.put(Label.Fields.CREATED_BY, label.getCreatedBy());
        paramMap.put(Label.Fields.NAME, label.getName());
        paramMap.put(Label.Fields.DESCRIPTION, label.getDescription());
        paramMap.put(Label.Fields.DISPLAY_NAME, label.getDisplayName());
        namedJdbcTemplate.update(Label.Queries.INSERT_NEW, paramMap);
        label.setId(findSecurityLabelByName(label.getName()).getId());
        List<Map<String, Object>> las = new ArrayList<>();
        for (LabelAttribute la : label.getLabelAttribute()) {
            paramMap = new HashMap<>();
            paramMap.put(LabelAttribute.Fields.UPDATED_AT, la.getUpdatedAt());
            paramMap.put(LabelAttribute.Fields.UPDATED_BY, la.getUpdatedBy());
            paramMap.put(LabelAttribute.Fields.CREATED_AT, la.getCreatedAt());
            paramMap.put(LabelAttribute.Fields.CREATED_BY, la.getCreatedBy());
            paramMap.put(LabelAttribute.Fields.NAME, la.getName());
            paramMap.put(LabelAttribute.Fields.DESCRIPTION, la.getDescription());
            paramMap.put(LabelAttribute.Fields.S_LABEL_ID, label.getId());
            paramMap.put(LabelAttribute.Fields.VALUE, la.getPath());
            paramMap.put(LabelAttribute.Fields.PATH, la.getPath());
            las.add(paramMap);
        }
        namedJdbcTemplate.batchUpdate(LabelAttribute.Queries.INSERT_NEW, las.toArray(new Map[0]));
    }

    /**
     * Update security label by name.
     *
     * @param name the name
     * @param label the label
     */
    @SuppressWarnings({"unchecked"})
    @Override
    @Transactional
    public void updateSecurityLabelByName(String name, Label label) {
        Label oldLabel = findSecurityLabelByName(label.getName());
        label.setId(oldLabel.getId());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(LabelAttribute.Fields.S_LABEL_ID, oldLabel.getId());
        List<LabelAttribute> oldAttrs = namedJdbcTemplate.query(LabelAttribute.Queries.SELECT_BY_LABEL_ID,
                paramMap, LabelAttributeRowMapper.DEFAULT_ROW_MAPPER);
        Map<Integer, LabelAttribute> las = new HashMap<>();
        for (LabelAttribute oa : oldAttrs) {
            las.put(oa.getId(), oa);
        }
        List<LabelAttribute> toCreate = new ArrayList<>();
        List<LabelAttribute> toUpdate = new ArrayList<>();
        for (LabelAttribute la : label.getLabelAttribute()) {
            if (la.getId() < 0) {
                toCreate.add(la);
            } else {
                toUpdate.add(la);
                las.remove(la.getId());
            }
        }
        List<LabelAttribute> toDelete = new ArrayList<>(las.values());
        if (CollectionUtils.isNotEmpty(toCreate)) {
            List<Map<String, Object>> ls = new ArrayList<>();
            for (LabelAttribute la : toCreate) {
                paramMap = new HashMap<>();
                paramMap.put(LabelAttribute.Fields.UPDATED_AT, la.getUpdatedAt());
                paramMap.put(LabelAttribute.Fields.UPDATED_BY, la.getUpdatedBy());
                paramMap.put(LabelAttribute.Fields.CREATED_AT, la.getCreatedAt());
                paramMap.put(LabelAttribute.Fields.CREATED_BY, la.getCreatedBy());
                paramMap.put(LabelAttribute.Fields.NAME, la.getName());
                paramMap.put(LabelAttribute.Fields.DESCRIPTION, la.getDescription());
                paramMap.put(LabelAttribute.Fields.S_LABEL_ID, label.getId());
                paramMap.put(LabelAttribute.Fields.VALUE, la.getPath());
                paramMap.put(LabelAttribute.Fields.PATH, la.getPath());
                ls.add(paramMap);
            }
            namedJdbcTemplate.batchUpdate(LabelAttribute.Queries.INSERT_NEW, ls.toArray(new Map[0]));
        }
        if (CollectionUtils.isNotEmpty(toUpdate)) {
            List<Map<String, Object>> ls = new ArrayList<>();
            for (LabelAttribute la : toUpdate) {
                paramMap = new HashMap<>();
                paramMap.put(LabelAttribute.Fields.ID, la.getId());
                paramMap.put(LabelAttribute.Fields.UPDATED_AT, la.getUpdatedAt());
                paramMap.put(LabelAttribute.Fields.UPDATED_BY, la.getUpdatedBy());
                paramMap.put(LabelAttribute.Fields.CREATED_AT, la.getCreatedAt());
                paramMap.put(LabelAttribute.Fields.CREATED_BY, la.getCreatedBy());
                paramMap.put(LabelAttribute.Fields.NAME, la.getName());
                paramMap.put(LabelAttribute.Fields.DESCRIPTION, la.getDescription());
                paramMap.put(LabelAttribute.Fields.S_LABEL_ID, label.getId());
                paramMap.put(LabelAttribute.Fields.PATH, la.getPath());
                paramMap.put(LabelAttribute.Fields.VALUE, la.getPath());
                ls.add(paramMap);
            }
            namedJdbcTemplate.batchUpdate(LabelAttribute.Queries.UPDATE_BY_ID, ls.toArray(new Map[0]));
        }
        if (!CollectionUtils.isEmpty(toDelete)) {
            List<Map<String, Object>> ls = new ArrayList<>();
            for (LabelAttribute la : toDelete) {
                paramMap = new HashMap<>();
                paramMap.put(LabelAttribute.Fields.NAME, la.getName());
                paramMap.put(LabelAttribute.Fields.ID, la.getId());
                ls.add(paramMap);
            }
            namedJdbcTemplate.batchUpdate("delete from s_label_attribute_value where s_label_attribute_id = :ID", ls.toArray(new Map[0]));
            namedJdbcTemplate.batchUpdate(LabelAttribute.Queries.DELETE_BY_ID, ls.toArray(new Map[0]));

        }

        paramMap = new HashMap<>();
        paramMap.put(Label.Fields.UPDATED_AT, label.getUpdatedAt());
        paramMap.put(Label.Fields.UPDATED_BY, label.getUpdatedBy());
        paramMap.put(Label.Fields.CREATED_AT, label.getCreatedAt());
        paramMap.put(Label.Fields.CREATED_BY, label.getCreatedBy());
        paramMap.put(Label.Fields.NAME, label.getName());
        paramMap.put(Label.Fields.DESCRIPTION, label.getDescription());
        paramMap.put(Label.Fields.DISPLAY_NAME, label.getDisplayName());
        namedJdbcTemplate.update(Label.Queries.UPDATE_BY_NAME, paramMap);

    }

    /**
     * Adds the label attribute.
     *
     * @param toAdd the to add
     */
    
    @Override
    @Transactional
    public void addLabelAttribute(LabelAttribute toAdd) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(LabelAttribute.Fields.UPDATED_AT, toAdd.getUpdatedAt());
        paramMap.put(LabelAttribute.Fields.UPDATED_BY, toAdd.getUpdatedBy());
        paramMap.put(LabelAttribute.Fields.CREATED_AT, toAdd.getCreatedAt());
        paramMap.put(LabelAttribute.Fields.CREATED_BY, toAdd.getCreatedBy());
        paramMap.put(LabelAttribute.Fields.NAME, toAdd.getName());
        paramMap.put(LabelAttribute.Fields.DESCRIPTION, toAdd.getDescription());
        paramMap.put(LabelAttribute.Fields.VALUE, toAdd.getPath());
        paramMap.put(LabelAttribute.Fields.S_LABEL_ID, toAdd.getLabel().getId());
        namedJdbcTemplate.update(LabelAttribute.Queries.UPDATE_BY_NAME, paramMap);

    }

    /**
     * Checks if is user in role.
     *
     * @param userName the user name
     * @param roleName the role name
     * @return true, if is user in role
     */
  
    @Override
    @Transactional
    public boolean isUserInRole(String userName, String roleName) {
        return jdbcTemplate.queryForObject(
                "select count(id) from s_user_s_role" + " where s_users_id="
                        + "(select id from s_user where login=?) "
                        + "and s_roles_id=" + "(select id from s_role where name=?)",
                Long.class, userName, roleName) > 0;

    }

    /**
     * Creates the resources.
     *
     * @param resourcePOs the resource p os
     */
    @Override
    @Transactional
    public void createResources(List<Resource> resourcePOs) {
        for (Resource toAdd : resourcePOs) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(Resource.Fields.UPDATED_AT, toAdd.getUpdatedAt());
            paramMap.put(Resource.Fields.UPDATED_BY, toAdd.getUpdatedBy());
            paramMap.put(Resource.Fields.CREATED_AT, toAdd.getCreatedAt());
            paramMap.put(Resource.Fields.CREATED_BY, toAdd.getCreatedBy());
            paramMap.put(Resource.Fields.NAME, toAdd.getName());
            paramMap.put(Resource.Fields.DISPLAY_NAME, toAdd.getDisplayName());
            paramMap.put(Resource.Fields.R_TYPE, toAdd.getRType());
            List<Map<String, Object>> resultOfSelect = namedJdbcTemplate.queryForList(Resource.Queries.SELECT_BY_NAME, paramMap);

            if (CollectionUtils.isEmpty(resultOfSelect)) {
                jdbcTemplate.update(INSERT_RESOURCE_SQL,
                        toAdd.getName(),
                        toAdd.getDisplayName(),
                        toAdd.getRType(),
                        toAdd.getCategory(),
                        toAdd.getCreatedBy(),
                        toAdd.getParentName());
            } else {
                paramMap.put(Resource.Fields.CREATED_AT, resultOfSelect.get(0).get(Resource.Fields.CREATED_AT));
                paramMap.put(Resource.Fields.CREATED_BY, resultOfSelect.get(0).get(Resource.Fields.CREATED_BY));
                paramMap.put(Resource.Fields.NAME, resultOfSelect.get(0).get(Resource.Fields.NAME));
                paramMap.put(Resource.Fields.R_TYPE, resultOfSelect.get(0).get(Resource.Fields.R_TYPE));
                paramMap.put(Resource.Fields.CATEGORY, resultOfSelect.get(0).get(Resource.Fields.CATEGORY));
                namedJdbcTemplate.update(Resource.Queries.UPDATE_BY_NAME, paramMap);
            }
        }
    }

    /**
     * Delete resource by name.
     *
     * @param resourceName resource name.
     */
    @Override
    @Transactional
    public void deleteResource(String resourceName) {
        Map<String, Object> params = new HashMap<>();
        params.put(Resource.Fields.NAME.toLowerCase(), resourceName);
        namedJdbcTemplate.update(
                "delete from s_right_s_resource where s_resource_id in(select id from s_resource where name = :name )",
                params);
        namedJdbcTemplate.update("delete from s_resource where name = :name", params);
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select id, s_label_id from s_label_attribute where value like ?",
                resourceName + ".%");
        int labelId = 0;
        while (rs.next()) {
            int labelAttrId = rs.getInt(1);
            labelId = rs.getInt(2);
            jdbcTemplate.update("delete from s_label_attribute_value where s_label_attribute_id = ?", labelAttrId);
            jdbcTemplate.update("delete from s_label_attribute where id = ?", labelAttrId);

        }
        jdbcTemplate.update("delete from s_role_s_label where s_label_id = ?", labelId);
        jdbcTemplate.update("delete from s_label where id = ?", labelId);

    }

    @Override
    public boolean updateResourceDisplayName(String resourceName, String resourceDisplayName) {
        Map<String, Object> params = new HashMap<>();
        params.put(Resource.Fields.NAME.toLowerCase(), resourceName);
        params.put(Resource.Fields.DISPLAY_NAME.toLowerCase(), resourceDisplayName);
        params.put(Resource.Fields.UPDATED_AT.toLowerCase(), new Timestamp(System.currentTimeMillis()));
        params.put(Resource.Fields.UPDATED_BY.toLowerCase(), SecurityUtils.getCurrentUserName());
        return namedJdbcTemplate.update(updateResourceDisplayNameSql, params) > 0;
    }

    /**
     * Drop all resources.
     */
    @Override
    public void dropResources(SecuredResourceCategoryInf... categories) {

        boolean hasMetaModel = false;
        if (ArrayUtils.isEmpty(categories)) {
            jdbcTemplate.update(DROP_ALL_NON_SYSTEM_RESOURCES_SQL);
            hasMetaModel = true;
        } else {
            for (SecuredResourceCategoryInf category : categories) {
                jdbcTemplate.update(DELETE_RESOURCES_BY_CATEGORY_SQL, category.name());
                hasMetaModel = hasMetaModel ? hasMetaModel : category == SecuredResourceCategoryInf.META_MODEL;
            }
        }

        if (hasMetaModel) {
            jdbcTemplate.update("delete from s_label_attribute_value");
            jdbcTemplate.update("delete from s_label_attribute");
            jdbcTemplate.update("delete from s_role_s_label");
            jdbcTemplate.update("delete from s_label");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<RoleProperty> loadAllProperties() {
        return namedJdbcTemplate.query(LOAD_ALL_ROLE_PROPERTIES, rolePropertyRowMapper);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public RoleProperty loadPropertyByName(String name) {
        List<RoleProperty> list = namedJdbcTemplate.query(LOAD_ROLE_PROPERTY_BY_NAME,
                Collections.singletonMap("name", name), rolePropertyRowMapper);

        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public RoleProperty loadPropertyByDisplayName(String displayName) {
        final List<RoleProperty> list = namedJdbcTemplate.query(LOAD_ROLE_PROPERTY_BY_DISPLAY_NAME,
                Collections.singletonMap("display_name", displayName), rolePropertyRowMapper);

        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void saveProperty(RoleProperty property) {
        if (property.getId() == null) {
            // Insert property
            MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();

            sqlParameterSource.addValue("name", property.getName());
            sqlParameterSource.addValue("required", property.isRequired());
            sqlParameterSource.addValue("read_only", property.isReadOnly());
            sqlParameterSource.addValue("field_type", property.getFieldType());
            sqlParameterSource.addValue("display_name", property.getDisplayName());
            sqlParameterSource.addValue("created_at", property.getCreatedAt());
            sqlParameterSource.addValue("updated_at", property.getUpdatedAt());
            sqlParameterSource.addValue("created_by", property.getCreatedBy());
            sqlParameterSource.addValue("updated_by", property.getUpdatedBy());


            KeyHolder keyHolder = new GeneratedKeyHolder();

            namedJdbcTemplate.update(INSERT_ROLE_PROPERTY, sqlParameterSource, keyHolder, new String[]{"id"});

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


            namedJdbcTemplate.update(UPDATE_ROLE_PROPERTY_BY_ID, sqlParameterSource);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteProperty(long id) {
        // Delete all values first.
        namedJdbcTemplate.update(DELETE_ROLE_PROPERTY_VALUES_BY_ROLE_PROPERTY_ID,
                Collections.singletonMap("rolePropertyId", id));

        // Delete property.
        namedJdbcTemplate.update(DELETE_ROLE_PROPERTY_BY_ID,
                Collections.singletonMap("rolePropertyId", id));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void saveRolePropertyValues(Collection<RolePropertyValue> propertyValues) {

        if (CollectionUtils.isEmpty(propertyValues)) {
            return;
        }

        List<RolePropertyValue> insertValues = new ArrayList<>();
        List<RolePropertyValue> updateValues = new ArrayList<>();

        for (RolePropertyValue propertyValue : propertyValues) {
            if (propertyValue.getId() == null) {
                insertValues.add(propertyValue);
            } else {
                updateValues.add(propertyValue);
            }
        }

        if (!CollectionUtils.isEmpty(insertValues)) {
            insertRolePropertyValues(insertValues);
        }

        if (!CollectionUtils.isEmpty(updateValues)) {
            Map<String, Object>[] map = createRolePropertyValueParams(updateValues);

            namedJdbcTemplate.batchUpdate(UPDATE_ROLE_PROPERTY_VALUE_BY_ID, map);
        }
    }

    /**
     * @param propertyValues
     */
    private void insertRolePropertyValues(List<RolePropertyValue> propertyValues) {

        if (CollectionUtils.isEmpty(propertyValues)) {
            return;
        }

        Map<String, Object>[] map = createRolePropertyValueParams(propertyValues);
        namedJdbcTemplate.batchUpdate(INSERT_ROLE_PROPERTY_VALUE, map);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteRolePropertyValuesByIds(Collection<Long> ids) {
        namedJdbcTemplate.update(DELETE_ROLE_PROPERTY_VALUES_BY_IDS, Collections.singletonMap("listId", ids));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void deleteRolePropertyValuesByRoleId(long roleId) {
        namedJdbcTemplate.update(DELETE_ROLE_PROPERTY_VALUES_BY_ROLE_ID,
                Collections.singletonMap("roleId", roleId));
    }

    @Override
    public List<RolePropertyValue> loadRolePropertyValuesByRoleId(Integer roleId) {
        List<RolePropertyValue> values = loadRolePropertyValuesByRoleIds(Collections.singleton(roleId)).get(roleId);
        if (values == null) {
            values = new ArrayList<>();
        }
        values.addAll(loadNewProperties(roleId).stream().map(p -> new RolePropertyValue(roleId.longValue(), p)).collect(Collectors.toList()));
        return values;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<Integer, List<RolePropertyValue>> loadRolePropertyValuesByRoleIds(Collection<Integer> roleIds) {

        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyMap();
        }

        return namedJdbcTemplate.query(LOAD_ROLE_PROPERTY_VALUES_BY_ROLE_IDS,
                Collections.singletonMap("listId", roleIds),
                rs -> {

                    Map<Integer, List<RolePropertyValue>> result = new HashMap<>();
                    while (rs.next()) {
                        Integer roleId = rs.getInt(FieldColumns.ROLE_ID.name());

                        List<RolePropertyValue> propValues = result.get(roleId);
                        if (propValues == null) {
                            propValues = new ArrayList<>();
                            result.put(roleId, propValues);
                        }

                        RolePropertyValue propValue = rolePropertyValueRowMapper.mapRow(rs, 0);
                        propValues.add(propValue);
                    }

                    return result;
                }
        );
    }

    /**
     * @param propertyValues
     * @return
     */
    private Map<String, Object>[] createRolePropertyValueParams(List<RolePropertyValue> propertyValues) {

        Map<String, Object>[] result = new Map[propertyValues.size()];
        for (int i = 0; i < propertyValues.size(); i++) {
            RolePropertyValue propertyValue = propertyValues.get(i);

            Map<String, Object> params = new HashMap<>();

            if (Objects.nonNull(propertyValue.getId())) {
                params.put("id", propertyValue.getId());
            }

            params.put("role_id", propertyValue.getRoleId());

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
    public List<Role> fetchRolesFullInfo() {
        final Map<Integer, Role> roles = jdbcTemplate.query(selectRolesWithProperties, ROLES_WITH_PROPERTIES_RESULT_EXTRACTOR);
        jdbcTemplate.query(selectRolesLabels, rs -> {
            while (rs.next()) {
                final Label label = new Label(rs.getInt("labelId"));
                label.setName(rs.getString("labelName"));
                roles.get(rs.getInt("roleId")).addLabel(label);
            }
            return null;
        });
        securityLabelDao.fetchObjectsSecurityLabelsValues().forEach((roleId, labelAttributeValuePOS) ->
                roles.get(roleId).setLabelAttributeValues(labelAttributeValuePOS)
        );
        jdbcTemplate.query(selectRolesResourceRights, rs -> {
            while (rs.next()) {
                final ResourceRight resourceRight = new ResourceRight();
                resourceRight.setRole(roles.get(rs.getInt("roleId")));
                final Resource resource = new Resource();
                resource.setName(rs.getString("resourceName"));
                resource.setRType(rs.getString("rType"));
                resource.setCategory(rs.getString("category"));
                resourceRight.setResource(resource);
                final Right right = new Right();
                right.setName(rs.getString("rightName"));
                resourceRight.setRight(right);
                roles.get(rs.getInt("roleId")).addConnectedResourceRight(resourceRight);
            }
            return null;
        });
        return new ArrayList<>(roles.values());
    }

    @Override
    public void removeRolesByName(final List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return;
        }

        String rolesNameParamName = "names";
        namedJdbcTemplate.update(deleteRolesRightsByRolesNames, Collections.singletonMap(rolesNameParamName, roles));
        namedJdbcTemplate.update(deleteUsersRolesByRolesNames, Collections.singletonMap(rolesNameParamName, roles));
        namedJdbcTemplate.update(deleteRolesPropertiesByRolesNames, Collections.singletonMap(rolesNameParamName, roles));
        namedJdbcTemplate.update(deleteRolesByName, Collections.singletonMap(rolesNameParamName, roles));

    }

    @Override
    public void cleanRolesDataByName(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return;
        }

        String rolesNameParamName = "names";
        namedJdbcTemplate.update(deleteRolesRightsByRolesNames, Collections.singletonMap(rolesNameParamName, roles));
        namedJdbcTemplate.update(deleteUsersRolesByRolesNames, Collections.singletonMap(rolesNameParamName, roles));
        namedJdbcTemplate.update(deleteRolesPropertiesByRolesNames, Collections.singletonMap(rolesNameParamName, roles));
        namedJdbcTemplate.update(deleteRolesPropertiesByRolesNames, Collections.singletonMap(rolesNameParamName, roles));

    }

    @Override
    public Collection<Role> loadRoles(final List<String> rolesNames) {
        if (CollectionUtils.isEmpty(rolesNames)) {
            return Collections.emptyList();
        }
        return namedJdbcTemplate.query(
                selectRolesWithPropertiesByNames,
                Collections.singletonMap("roleNames", rolesNames),
                ROLES_WITH_PROPERTIES_RESULT_EXTRACTOR
        ).values();
    }

    @Override
    public List<RoleProperty> loadNewProperties(Integer roleId) {
        return namedJdbcTemplate.query(LOAD_NEW_ROLE_PROPERTIES, Collections.singletonMap("roleId", roleId), rolePropertyRowMapper);
    }

}
