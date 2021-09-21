package com.huahui.datasphere.portal.model;

import java.io.*;
import com.fasterxml.jackson.annotation.*;
import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessResponse implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Date timestamp;
    private Integer status;
    private String error;
    private String message;
    private String exception;
    private String token;
    private String path;
    
    public AccessResponse() {
        this.timestamp = new Date();
    }
    
    public AccessResponse(final Integer status, final String message) {
        this.timestamp = new Date();
        this.status = status;
        this.message = message;
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(final Integer status) {
        this.status = status;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
    
    public Date getTimestamp() {
        return this.timestamp;
    }
    
    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getError() {
        return this.error;
    }
    
    public void setError(final String error) {
        this.error = error;
    }
    
    public String getException() {
        return this.exception;
    }
    
    public void setException(final String exception) {
        this.exception = exception;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public void setToken(final String token) {
        this.token = token;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setPath(final String path) {
        this.path = path;
    }
}
