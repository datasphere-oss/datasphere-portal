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

package com.huahui.datasphere.portal.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.huahui.datasphere.portal.security.po.Label;
import com.huahui.datasphere.portal.security.po.LabelAttribute;
import com.huahui.datasphere.portal.security.po.Resource;
import com.huahui.datasphere.portal.security.po.Right;
import com.huahui.datasphere.portal.security.po.Role;
import com.huahui.datasphere.portal.security.po.RoleProperty;
import com.huahui.datasphere.portal.security.po.RolePropertyValue;


/**
 * The Interface RoleDao.
 */
public interface RoleDao {

	/**
	 * Insert a new role.
	 *
	 * @param role
	 *            the role to save
	 * @return the role po save role
	 */
	Role create(Role role);

	/**
	 * Find by name.
	 *
	 * @param name
	 *            the name
	 * @return the role po
	 */
	Role findByName(String name);

	/**
	 * Find right by name.
	 *
	 * @param name
	 *            the name
	 * @return the right po
	 */
	Right findRightByName(String name);

	/**
	 * Find resource by name.
	 *
	 * @param name
	 *            the name
	 * @return the resource po
	 */
	Resource findResourceByName(String name);

	/**
	 * Update.
	 *
	 * @param name
	 *            the name
	 * @param role
	 *            the role
	 * @return the role po
	 */
	Role update(String name, Role role);

	/**
	 * Delete.
	 *
	 * @param name
	 *            the name
	 */
	void delete(String name);

	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	List<Role> getAll();

	/**
     * Gets combined roles, rights and resources by user login name.
     * @param login the user login
     * @return list of roles.
     */
    List<Role> findRolesByUserLogin(String login);

	/**
	 * Gets the all secured res.
	 *
	 * @return the all secured res
	 */
	List<Resource> getAllSecurityResources();

	/**
	 * Gets the all security labels.
	 *
	 * @return the all security labels
	 */
	List<Label> getAllSecurityLabels();

	/**
	 * Gets the security label by name.
	 *
	 * @param name
	 *            the name
	 * @return the security label by name
	 */
	Label findSecurityLabelByName(String name);

	/**
	 * Delete security label by name.
	 *
	 * @param name
	 *            the name
	 */
	void deleteSecurityLabelByName(String name);

	/**
	 * Creates the security label.
	 *
	 * @param label
	 *            the new label
	 */
	void createSecurityLabel(Label label);

	/**
	 * Update security label by name.
	 *
	 * @param name
	 *            the name
	 * @param label
	 *            the label
	 */
	void updateSecurityLabelByName(String name, Label label);

	/**
	 * Adds the label attribute.
	 *
	 * @param toAdd
	 *            the to add
	 */
	void addLabelAttribute(LabelAttribute toAdd);

	/**
	 * Remove advanced security feature temporarily.
	 *
	 * @param existingName
	 *            the role name
	 * @param newRole
	 *            the to update
	 * @param labelNames
	 *            the label names
	 */
//	void update(String existingName, Role newRole, List<SecurityLabel> securityLabels);

	/**
	 * Determines is user in role.
	 *
	 * @param userName
	 *            User name
	 * @param roleName
	 *            Role name
	 * @return <code>true</code> if user in role, otherwise<code>false</code>
	 */
	boolean isUserInRole(String userName, String roleName);

	/**
	 * Create secured resources.
	 *
	 * @param resources
	 *            list with secured resources.
	 */
	void createResources(List<Resource> resources);
	/**
	 * Delete resource by name.
	 * @param resourceName resource name.
	 */
	void deleteResource(String resourceName);

	/**
	 * Find security labels by role name.
	 *
	 * @param roleName
	 *            the role name
	 * @return list with labels.
	 */
	List<Label> findSecurityLabelsByRoleName(String roleName);
	/**
	 * Remove advanced security feature temporarily.
	 * 
	 * @param categories the categories to drop
	 */
//	void dropResources(SecuredResourceCategory... categories);

	/**
	 * Update display name for security resource by name
	 * @param resourceName security resource name
	 * @param resourceDisplayName security resource display name
	 * @return true if success, else false
	 */
	boolean updateResourceDisplayName(String resourceName, String resourceDisplayName);

	/**
	 * Load list of all user properties.
	 * @return
     */
	List<RoleProperty> loadAllProperties();

	/**
	 * Load property by name.
	 * @param name
	 * @return
     */
	RoleProperty loadPropertyByName(String name);

    /**
     * Load property by display name.
     * @param displayName    Property display name.
     * @return role property object
     */
    RoleProperty loadPropertyByDisplayName(String displayName);

	/**
	 *
	 * @param property
     */
	void saveProperty(RoleProperty property);

	/**
	 *
	 * @param id
     */
	void deleteProperty(long id);

	/**
	 *
	 * @param propertyValues
     */
	void saveRolePropertyValues(Collection<RolePropertyValue> propertyValues);

	/**
	 *
	 * @param ids
     */
	void deleteRolePropertyValuesByIds(Collection<Long> ids);

	/**
	 *
	 * @param roleId
     */
	void deleteRolePropertyValuesByRoleId(long roleId);
	/**
	 * Gets property values for role id
	 * @param roleId
	 * @return
	 */
	List<RolePropertyValue> loadRolePropertyValuesByRoleId(Integer roleId);
	/**
	 *
	 * @param roleIds
	 * @return
     */
    Map<Integer, List<RolePropertyValue>> loadRolePropertyValuesByRoleIds(Collection<Integer> roleIds);

    List<Role> fetchRolesFullInfo();

    void removeRolesByName(List<String> roles);

    void cleanRolesDataByName(List<String> roles);

    Collection<Role> loadRoles(List<String> rolesName);

    /**
     * new properties of role
     *
     * @param roleId
     * @return
     */
    List<RoleProperty> loadNewProperties(Integer roleId);
}