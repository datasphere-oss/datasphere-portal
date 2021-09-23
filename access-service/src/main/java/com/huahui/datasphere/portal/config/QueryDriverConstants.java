package com.huahui.datasphere.portal.config;

public class QueryDriverConstants
{
    public static final String COLLECTION = "queries";
    public static final String FIELD_DATA = "data";
    public static final String FIELD_QUERY = "query";
    public static final String FIELD_CREATED_AT = "createdAt";
    public static final String FIELD_COMPLETED_AT = "completedAt";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_PID = "pid";
    public static final String FIELD_ERROR = "error";
    public static final String DRIVER_NAME = "QueryDriver.jar";
    public static final String CONF_CLASSPATH = "df_batch_classpath";
    
    public enum QueryDataStatus
    {
        pending, 
        running, 
        success, 
        failed, 
        canceled;
    }
}
