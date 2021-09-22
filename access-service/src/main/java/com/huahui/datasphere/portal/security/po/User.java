/*
 * Apache License
 * 
 * Copyright (c) 2020 HuahuiData
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class User extends BaseUser implements Serializable {
	
	 /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The Constant TABLE_NAME. */
    public static final String TABLE_NAME = "s_user";
    
	private String fullname;
	
	 // bi-directional one-to-one association to PasswordPO
    /** The S passwords. */
    private List<Password> password;
    
    private String email;
    
    private String mobile;
    
    private String userState;
    
    private Long deptId;
    
    private String deptName;
    
    // bi-directional many-to-many association to RolePO
    /** The S roles. */
    private List<Role> roles = new ArrayList<>();
    
    /** The auth type. */
    private String authType;
    
    /** The locale. */
    private String locale;
    
    /** The notes. */
    private String notes;
    
    /** The active. */
    private boolean active;
    
    /** The admin. */
    private boolean admin;
    

    /**
     * User external provider source name.
     */
    private String source;

    /**
     * External user mark.
     */
    private boolean external;

   
    /**
     * Email notification
     */
    private Boolean emailNotification;
    
  

    /**
     * Property values.
     */
    private final transient List<UserPropertyValue> properties = new ArrayList<>();
    
    
    // accessible data domains
    private List<String> accessibleDomains;
    private Date expires;
    
   

    /** The apis. */
    private List<Api> apis = new ArrayList<>();

    /** The label attribute values. */
    private List<LabelAttributeValue> labelAttributeValues = new ArrayList<>();

    
    public User() {
    	
    }
    
    
 
	public String getFullname() {
		return fullname;
	}



	public void setFullname(String fullname) {
		this.fullname = fullname;
	}



	public List<Password> getPassword() {
		return password;
	}



	public void setPassword(List<Password> password) {
		this.password = password;
	}



	public String getSource() {
		return source;
	}



	public void setSource(String source) {
		this.source = source;
	}



	public boolean isExternal() {
		return external;
	}



	public void setExternal(boolean external) {
		this.external = external;
	}



	public Boolean getEmailNotification() {
		return emailNotification;
	}



	public void setEmailNotification(Boolean emailNotification) {
		this.emailNotification = emailNotification;
	}



	public List<UserPropertyValue> getProperties() {
		return properties;
	}



	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}





	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUserState() {
		return userState;
	}

	public void setUserState(String userState) {
		this.userState = userState;
	}

	

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}





	

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public List<Role> getRoles() {
		return roles;
	}

	

	public List<Api> getApis() {
		return apis;
	}



	public void setApis(List<Api> apis) {
		this.apis = apis;
	}



	public void setLabelAttributeValues(List<LabelAttributeValue> labelAttributeValues) {
		this.labelAttributeValues = labelAttributeValues;
	}



	public List<LabelAttributeValue> getLabelAttributeValues() {
		return labelAttributeValues;
	}

	public List<String> getAccessibleDomains() {
        return this.accessibleDomains;
    }
    
    public void setAccessibleDomains(final List<String> accessibleDomains) {
        this.accessibleDomains = accessibleDomains;
    }
    
    public Date getExpires() {
        return this.expires;
    }
    
    public void setExpires(final Date expires) {
        this.expires = expires;
    }
    
    @Override
    public String toString() {
        return "User [emaild=" + this.email + ", name=" + this.fullname + ", roles=" + this.roles + ", accessibleDomains=" + this.accessibleDomains + ", expires=" + this.expires + "]";
    }
}
