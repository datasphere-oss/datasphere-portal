package com.huahui.datasphere.portal.model;

import java.util.*;

public class UserToken
{
    private String token;
    private Date createdAt;
    
    public UserToken(final String token) {
        this.token = token;
        this.createdAt = new Date();
    }
    
    public String getToken() {
        return this.token;
    }
    
    public void setToken(final String token) {
        this.token = token;
    }
    
    public Date getCreatedAt() {
        return this.createdAt;
    }
    
    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }
}
