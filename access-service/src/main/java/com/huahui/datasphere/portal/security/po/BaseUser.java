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

import java.sql.Date;
import java.util.List;

/**
 * @author theseusyang
 */
public abstract class BaseUser extends BaseSecurity {
    /**
     * The id.
     */
    private Integer id;
    /**
     * The login address.
     */
    private String login;
    /**
     * The login date.
     */
    private Date loginDate;
    
    
    /**
     * The S tokens.
     */
    private List<BaseToken> tokens;

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
     * @param id the new id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the login.
     *
     * @return the login
     */
    public String getLogin() {
        return this.login;
    }

    /**
     * Sets the login.
     *
     * @param login the new login
     */
    public void setLogin(String login) {
        this.login = login;
    }
    
    

    /**
     * Gets the s tokens.
     *
     * @return the s tokens
     */
    public List<BaseToken> getTokens() {
        return this.tokens;
    }

    /**
     * Sets the s tokens.
     *
     * @param tokens the new s tokens
     */
    public void setTokens(List<BaseToken> tokens) {
        this.tokens = tokens;
    }

    /**
     * Adds the s token.
     *
     * @param token the token
     * @return the token po
     */
    public BaseToken addToken(BaseToken token) {
        getTokens().add(token);
        token.setUser(this);

        return token;
    }

    public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}



	/**
     * Removes the s token.
     *
     * @param token the s token
     * @return the token po
     */
    public Token removeToken(Token token) {
        getTokens().remove(token);
        token.setUser(null);

        return token;
    }

    public static class Fields extends BaseSecurity.Fields {

        /**
         * Instantiates a new fields.
         */
        protected Fields() {
            super();
        }


        /** The Constant LOGIN. */
        public static final String LOGIN = "LOGIN";
    }
}
