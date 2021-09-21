package com.huahui.datasphere.portal.ExceptionHandling;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class DataSphereException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    private ErrorCode errorCode;
    private final Map<String, Object> properties;
    
    public static DataSphereException wrap(final Throwable exception, final ErrorCode errorCode) {
        if (!(exception instanceof DataSphereException)) {
            return new DataSphereException(exception.getMessage(), exception, errorCode);
        }
        final DataSphereException se = (DataSphereException)exception;
        if (errorCode != null && errorCode != se.getErrorCode()) {
            return new DataSphereException(exception.getMessage(), exception, errorCode);
        }
        return se;
    }
    
    public static void main(final String[] args) {
        throw new DataSphereException("Fuck");
    }
    
    public static DataSphereException wrap(final Throwable exception) {
        return wrap(exception, null);
    }
    
    public DataSphereException(final ErrorCode errorCode) {
        this.properties = new TreeMap<String, Object>();
        this.errorCode = errorCode;
    }
    
    public DataSphereException(final Throwable t) {
        super(t);
        this.properties = new TreeMap<String, Object>();
    }
    
    public DataSphereException(final String message) {
        super(message);
        this.properties = new TreeMap<String, Object>();
    }
    
    public DataSphereException(final String message, final Throwable e) {
        super(message, e);
        this.properties = new TreeMap<String, Object>();
    }
    
    public DataSphereException() {
        this.properties = new TreeMap<String, Object>();
    }
    
    public DataSphereException(final String message, final ErrorCode errorCode) {
        super(message);
        this.properties = new TreeMap<String, Object>();
        this.errorCode = errorCode;
    }
    
    public DataSphereException(final Throwable cause, final ErrorCode errorCode) {
        super(cause);
        this.properties = new TreeMap<String, Object>();
        this.errorCode = errorCode;
    }
    
    public DataSphereException(final String message, final Throwable cause, final ErrorCode errorCode) {
        super(message, cause);
        this.properties = new TreeMap<String, Object>();
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
    
    public DataSphereException setErrorCode(final ErrorCode errorCode) {
        this.errorCode = errorCode;
        return this;
    }
    
    public Map<String, Object> getProperties() {
        return this.properties;
    }
    
    public <T> T get(final String name) {
        return (T)this.properties.get(name);
    }
    
    public DataSphereException set(final String name, final Object value) {
        this.properties.put(name, value);
        return this;
    }
    
    public DataSphereException setMissingProperty(final Object value) {
        this.properties.put("MISSING_FIELD", value);
        return this;
    }
    
    public static String getUserText(final ErrorCode errorCode) {
        if (errorCode == null) {
            return null;
        }
        final String key = errorCode.getClass().getSimpleName() + "__" + errorCode;
        try {
            final Class cls = Class.forName(DataSphereException.class.getName());
            final ClassLoader cLoader = cls.getClassLoader();
            final InputStream is = cLoader.getResourceAsStream("exceptions");
            final Properties pros = new Properties();
            pros.load(is);
            final String value = (String)pros.get(key);
            if (value == null || value.isEmpty()) {
                return "Error message missing from properties file";
            }
            return value;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Exception while getting user text for error code " + key;
        }
    }
}
