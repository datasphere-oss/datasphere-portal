package com.huahui.datasphere.portal.security.jwt;

import java.util.Date;
import java.util.List;

public class User
{
    private String emaild;
    private String name;
    private List<String> roles;
    private List<String> accessibleDomains;
    private Date expires;
    
    public User() {
    }
    
    public User(final String emailId, final List<String> roles) {
        this.emaild = emailId;
        this.roles = roles;
    }
    
    public String getEmaild() {
        return this.emaild;
    }
    
    public void setEmaild(final String emaild) {
        this.emaild = emaild;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public List<String> getRoles() {
        return this.roles;
    }
    
    public void setRoles(final List<String> roles) {
        this.roles = roles;
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
        return "User [emaild=" + this.emaild + ", name=" + this.name + ", roles=" + this.roles + ", accessibleDomains=" + this.accessibleDomains + ", expires=" + this.expires + "]";
    }
}
