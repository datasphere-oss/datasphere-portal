package com.huahui.datasphere.portal.security.jwt;

public enum UserRoles
{
    modeller("ROLE_MODELER"), 
    admin("ROLE_ADMIN"), 
    analyst("ROLE_ANALYST");
    
    private String name;
    
    private UserRoles(final String name) {
        this.name = name;
    }
    
    public String getValue() {
        return this.name;
    }
}
