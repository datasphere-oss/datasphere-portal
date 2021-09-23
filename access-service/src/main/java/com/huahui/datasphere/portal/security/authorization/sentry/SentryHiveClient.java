package com.huahui.datasphere.portal.security.authorization.sentry;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SentryHiveClient
{
    private String jobId;
    private final String jobType = "SECURITY";
    private String username;
    private static Logger logger;
    
    public SentryHiveClient(final String jobId, final String username) {
        this.jobId = null;
        this.username = null;
        this.jobId = jobId;
        this.username = username;
    }
    
    
    static {
        SentryHiveClient.logger = LoggerFactory.getLogger(SentryHiveClient.class.getName());
    }
    
    private class SentryPolicyEntry
    {
        public String database;
        public String table;
        public String partition;
        public String column;
        public String principal_name;
        public String principal_type;
        public String privilege;
        public String grant_option;
        public String grant_time;
        public String grantor;
        
        public SentryPolicyEntry(final String database, final String table, final String partition, final String column, final String principal_name, final String principal_type, final String privilege, final String grant_option, final String grant_time, final String grantor) {
            this.init(database, table, partition, column, principal_name, principal_type, privilege, grant_option, grant_time, grantor);
        }
        
        public void init(final String database, final String table, final String partition, final String column, final String principal_name, final String principal_type, final String privilege, final String grant_option, final String grant_time, final String grantor) {
            this.database = database;
            this.table = table;
            this.partition = partition;
            this.column = column;
            this.principal_name = principal_name;
            this.principal_type = principal_type;
            this.privilege = privilege;
            this.grant_option = grant_option;
            this.grant_time = grant_time;
            this.grantor = grantor;
        }
        
        @Override
        public String toString() {
            return new ToStringBuilder(this).append("database", this.database).append("table", this.table).append("partition", this.partition).append("column", this.column).append("principal_name", this.principal_name).append("principal_type", this.principal_type).append("privilege", this.privilege).append("grant_option", this.grant_option).append("grant_time", this.grant_time).append("grantor", this.grantor).toString();
        }
    }
}
