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

package com.huahui.datasphere.portal.security.po;

import java.io.Serializable;

/**
 * The persistent class for the s_password database table.
 * 
 * @author theseusyang
 */
public class Password extends BaseSecurity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant TABLE_NAME. */
	public static final String TABLE_NAME = "s_password";
	/** The id. */
	private Integer id;

	/** The password text. */
	private String passwordText;
	/** Is password active. */
	private Boolean active;
	/** Activation code. */
	private String activationCode;
	// bi-directional one-to-one association to User
	/** The S user. */
	private User user;

	/**
	 * Instantiates a new password po.
	 */
	public Password() {
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the password text.
	 *
	 * @return the password text
	 */
	public String getPasswordText() {
		return this.passwordText;
	}

	/**
	 * Sets the password text.
	 *
	 * @param passwordText
	 *            the new password text
	 */
	public void setPasswordText(String passwordText) {
		this.passwordText = passwordText;
	}

	/**
	 * Gets the active.
	 *
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * Sets the active.
	 *
	 * @param active
	 *            the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	/**
	 * Gets the s user.
	 *
	 * @return the s user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * Sets the s user.
	 *
	 * @param user
	 *            the new s user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * The Class Fields.
	 */
	public static final class Fields extends BaseSecurity.Fields {

		/**
		 * Instantiates a new fields.
		 */
		private Fields() {
			super();
		}

		/** The Constant PASSWORD_TEXT. */
		public static final String PASSWORD_TEXT = "PASSWORD_TEXT";

		/** The Constant ACTIVE. */
		public static final String ACTIVE = "ACTIVE";
		
		/** The constant ACTIVATION_CODE*/
		public static final String ACTIVATION_CODE = "ACTIVATION_CODE";

		/** The Constant S_USER_ID. */
		public static final String S_USER_ID = "S_USER_ID";

		/** All fields combined. */
		public static final String ALL = String.join(DELIMETER,  PASSWORD_TEXT, ACTIVE, ACTIVATION_CODE, S_USER_ID, CREATED_AT,
				UPDATED_AT, CREATED_BY, UPDATED_BY);
		/** All fields combined. */
		public static final String ALL_WITH_TABLE_NAME = String.join(
				DELIMETER, 
				String.join(DOT, TABLE_NAME, ID),
				String.join(DOT, TABLE_NAME, PASSWORD_TEXT), 
				String.join(DOT, TABLE_NAME, ACTIVE),
				String.join(DOT, TABLE_NAME, ACTIVATION_CODE),
				String.join(DOT, TABLE_NAME, S_USER_ID), 
				String.join(DOT, TABLE_NAME, CREATED_AT), 
				String.join(DOT, TABLE_NAME, UPDATED_AT),
				String.join(DOT, TABLE_NAME, CREATED_BY), 
				String.join(DOT, TABLE_NAME, UPDATED_BY));
		/** All fields combined, used for update queries. */
		public static final String ALL_TO_UPDATE = String.join(
				DELIMETER, 
				String.join(EQUALS,  PASSWORD_TEXT, PASSWORD_TEXT), 
				String.join(EQUALS, ACTIVE, ACTIVE),
				String.join(EQUALS, ACTIVATION_CODE, ACTIVE),
				String.join(EQUALS, S_USER_ID, S_USER_ID), 
				String.join(EQUALS, CREATED_AT, CREATED_AT), 
				String.join(EQUALS, UPDATED_AT, UPDATED_AT),
				String.join(EQUALS,  CREATED_BY, CREATED_BY), 
				String.join(EQUALS, UPDATED_BY, UPDATED_BY));
		public static final String ALL_TO_INSERT = String.join(
				DELIMETER, 
				String.join("",DOTS,  PASSWORD_TEXT), 
				String.join("",DOTS,  ACTIVE),
				String.join("",DOTS,  ACTIVATION_CODE),
				String.join("",DOTS,  S_USER_ID), 
				String.join("",DOTS,  CREATED_AT), 
				String.join("",DOTS,  UPDATED_AT),
				String.join("",DOTS,  CREATED_BY), 
				String.join("",DOTS,  UPDATED_BY));
	}

	/**
	 * The Class Queries.
	 */
	public static final class Queries {

		/**
		 * Instantiates a new queries.
		 */
		private Queries() {

		}

		/** The Constant SELECT_BY_ID. */
		public static final String SELECT_BY_ID = "SELECT " + Fields.ALL_WITH_TABLE_NAME + " FROM " + TABLE_NAME + " WHERE " + Fields.ID
				+ " = :" + Fields.ID;

		/** The Constant SELECT_BY_USER_ID. */
		public static final String SELECT_BY_USER_ID = "SELECT " + Fields.ALL_WITH_TABLE_NAME + " FROM " + TABLE_NAME + " WHERE "
				+ Fields.S_USER_ID + " = :" + Fields.S_USER_ID;
		/** The Constant SELECT_BY_USER_ID_ACTIVE_ONLY. */
		public static final String SELECT_BY_USER_ID_ACTIVE_ONLY = "SELECT " + Fields.ALL_WITH_TABLE_NAME + " FROM " + TABLE_NAME
				+ " WHERE " + Fields.S_USER_ID + " = :" + Fields.S_USER_ID + " AND " + Fields.ACTIVE + " IS TRUE";
		/** The Constant DELETE_BY_NAME. */
		public static final String DELETE_BY_ID = 
				"DELETE FROM "+TABLE_NAME
					+ " WHERE "+Fields.ID+" = :"+Fields.ID;
		/** The Constant DELETE_BY_USER_NAME. */
		public static final String DELETE_BY_USER_ID = 
				"DELETE FROM "+TABLE_NAME
					+ " WHERE "+Fields.S_USER_ID+" = :"+Fields.S_USER_ID;
		public static final String INSERT_NEW =
				"INSERT INTO "+TABLE_NAME+"("+Fields.ALL+") VALUES ("+Fields.ALL_TO_INSERT+")";
		public static final String UPDATE_BY_ID = 
				"UPDATE " +TABLE_NAME
					+ " SET "+ Fields.ALL_TO_UPDATE 
					+ " WHERE " + Fields.ID + " = :" + Fields.ID;
		public static final String DEACTIVATE_BY_USER_ID = 
				"UPDATE " +TABLE_NAME
					+ " SET "+ Fields.ACTIVE+" = FALSE"
					+ " WHERE " + Fields.S_USER_ID + " = :" + Fields.S_USER_ID;
		public static final String DEACTIVATE_ACTIVATION_CODES_BY_USER_ID =
				"UPDATE " +TABLE_NAME
						+ " SET "+ Fields.ACTIVE+" = FALSE, "
  						+ Fields.ACTIVATION_CODE + " = NULL "
						+ " WHERE " + Fields.S_USER_ID + " = :" + Fields.S_USER_ID
						+ " AND " +Fields.ACTIVATION_CODE + " IS NOT NULL";
		public static final String ACTIVATE_PASSWORD =
				"UPDATE " +TABLE_NAME
					+ " SET "+ Fields.ACTIVE+" = TRUE" 
					+ " WHERE " + Fields.S_USER_ID + " = :" + Fields.S_USER_ID + " AND "+ Fields.ACTIVATION_CODE+" = :"+Fields.ACTIVATION_CODE;
	}
}