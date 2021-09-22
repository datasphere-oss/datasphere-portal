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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.huahui.datasphere.portal.dto.UserPropertyDTO;
import com.huahui.datasphere.portal.security.po.Api;
import com.huahui.datasphere.portal.security.po.Password;
import com.huahui.datasphere.portal.security.po.Token;
import com.huahui.datasphere.portal.security.po.User;
import com.huahui.datasphere.portal.security.po.UserEvent;
import com.huahui.datasphere.portal.security.po.UserProperty;
import com.huahui.datasphere.portal.security.po.UserPropertyValue;
import com.huahui.datasphere.portal.type.security.SecurityLabelInf;


/**
 * The Interface UserDao.
 */
public interface UserDao {

	
	/**
     * Save.
     *
     * @param user
     *            the user
     * @param userLabels
     *            the user labels
     * @return the user po
     */
	User create(User user);
	
    /**
     * add advanced security label feature.
     *
     * @param user
     *            the user
     * @param userLabels
     *            the user labels
     * @return the user po
     */
    User create(User user, List<SecurityLabelInf> userLabels);

    /**
     * Find by login.
     *
     * @param login
     *            the login
     * @return the user po
     */
    User findByLogin(String login);

    /**
     * Find by email.
     *
     * @param email
     *            the email
     * @param source
     *            external provider source name.
     * @return the user po
     */
    User findByEmail(String email, String source);

    /**
     * Update.
     *
     * @param login
     *            the login
     * @param locale
     *            the user locale
     *  true if success, else false
     */
    boolean updateLocale(String login, String locale);

    /**
     * Update.
     *
     * @param login
     *            the login
     * @param user
     *            the user
     * @param userLabels
     *            the user labels
     * @return the user po
     */
    User update(String login, User user);

    
    /**
     * add advanced security label feature.
     *
     * @param login
     *            the login
     * @param user
     *            the user
     * @param userLabels
     *            the user labels
     * @return the user po
     */
    User update(String login, User user, List<SecurityLabelInf> userLabels);

    /**
     * Gets the all.
     *
     * @return the all
     */
    List<User> getAll();

    /**
     * Load list of all user properties.
     *
     * @return the list
     */
    List<UserProperty> loadAllProperties();

    /**
     * Load list of all user properties.
     *
     * @param name
     *            the name
     * @return the user property po
     */
    UserProperty loadPropertyByName(String name);

    /**
     * Load list of all user properties.
     *
     * @param displayName
     *            the display name
     * @return the user property po
     */
    UserProperty loadPropertyByDisplayName(String displayName);

    /**
     * Save property.
     *
     * @param property
     *            the property
     */
    void saveProperty(UserProperty property);

    /**
     * Delete property with all user values.
     *
     * @param id
     *            the id
     */
    void deleteProperty(long id);

    /**
     * Save properties.
     *
     * @param properties
     *            the properties
     * @param login
     *            the login
     */
    void saveProperties(List<UserPropertyDTO> properties, String login);

    /**
     * Insert or update user property values (w/o delete).
     *
     * @param propertyValues
     *            the property values
     */
    void saveUserPropertyValues(Collection<UserPropertyValue> propertyValues);

    /**
     * Delete property values by property value ID's.
     *
     * @param ids
     *            the ids
     */
    void deleteUserPropertyValuesByIds(Collection<Long> ids);

    /**
     * Delete property values by property value ID's.
     *
     * @param userId
     *            the user id
     */
    void deleteUserPropertyValuesByUserId(long userId);

    /**
     * Loads user property values by user id
     *
     * @param userId
     *            the id
     * @return property values
     */
    List<UserPropertyValue> loadUserPropertyValuesByUserId(Integer userId);

    /**
     * Load user property values by user ID's.
     *
     * @param userIds
     *            the user ids
     * @return the map
     */
    Map<Integer, List<UserPropertyValue>> loadUserPropertyValuesByUserIds(Collection<Integer> userIds);

    /**
     * Checks if is last admin.
     *
     * @return true, if is last admin
     */
    boolean isLastAdmin();

    /**
     * Save token.
     *
     * @param token
     *            the token po
     */
    void saveToken(Token token);

    /**
     * Delete token.
     *
     * @param tokenString
     *            security token as string
     */
    void deleteToken(String tokenString);

    /**
     * Loads user events for a login name, starting from date.
     *
     * @param login
     *            user login
     * @param from
     *            the date to start loading events from (e. g. all events will
     *            be younger than the given date)
     * @param page
     *            the page number
     * @param count
     *            the records count
     * @return list
     */
    List<UserEvent> loadUserEvents(String login, Date from, int page, int count);

    /**
     * Count user events for a login name.
     *
     * @param login
     *            user login
     * @return the records count
     */
    Long countUserEvents(String login);

    /**
     * Deletes an event.
     *
     * @param eventId
     *            the event id
     * @return true on success, false otherwise
     */
    boolean deleteUserEvent(String eventId);

    /**
     * Deletes several events at once.
     *
     * @param eventIds
     *            the ids
     * @return true, if successful, false otherwise
     */
    boolean deleteUserEvents(List<String> eventIds);

    /**
     * Deletes all events of a user, which are older then the given date.
     *
     * @param login
     *            user login
     * @param to
     *            the to
     * @return true, if successful, false otherwise
     */
    boolean deleteAllUserEvents(String login, Date to);

    /**
     * Creates user event.
     *
     * @param event
     *            the event to create
     * @return updated event
     */
    UserEvent create(UserEvent event);

    /**
     * Creates user event.
     *
     * @param event
     *            the event
     * @param login
     *            user login
     * @return updated event
     */
    UserEvent create(UserEvent event, String login);

    /**
     * Checks if user exist.
     *
     * @param login
     *            the login name.
     * @return <code>true</code> if user exist, otherwise <code>false</code>
     */
    boolean isExist(String login);

    /**
     * List of available APIs(e.g REST, SOAP)
     *
     * @return List of available APIs(e.g REST, SOAP)
     */
    List<Api> getAPIList();

    /**
     * Load all users with full information
     * @return List of users
     */
    List<User> fetchUsersFullInfo();

    void deleteUsersByLogin(List<String> logins);

    void saveUsers(List<User> users, Map<String, List<SecurityLabelInf>> userLabels);

    List<Password> fetchAllUsersPasswords();

    void saveUsersPasswords(List<Password> passwords);

    List<Password> fetchLastUserPasswords(long userId, int lastCount);

    /**
     * update password
     * @param userId
     * @param passwords
     * @param temp 
     */
    void updatePassword(Integer userId, Password passwords, boolean temp);

    void addUsersRoles(Map<String, Set<String>> userRoleNames);

    /**
     * activate temporary password
     * @param activationCode
     */
	void activatePassword(String activationCode);
}