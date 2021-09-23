package com.huahui.datasphere.portal.security.authorization.sentry;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.huahui.datasphere.portal.configuration.DSSConf;
import com.huahui.datasphere.portal.security.authentication.AuthenticationUtil;
import com.huahui.datasphere.portal.security.authorization.api.PolicyUtilsAPI;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class PolicyUtils implements PolicyUtilsAPI
{
    private String jobId;
    private SentryHiveClient sentryHiveClient;
    private static final String DSS_ROLENAME;
    private static final String DSS_GROUP;
    private static final String DSS_HIVE_SERVER_NAME;
    public static Logger logger;
    
    public PolicyUtils(final String mongoJobId) throws IOException {
        this.jobId = "";
        this.sentryHiveClient = null;
        this.jobId = mongoJobId;
        this.sentryHiveClient = new SentryHiveClient(this.jobId, "SECURITY");
    }
    
    private String getUserName() throws IOException {
        if (AuthenticationUtil.isImpersonationEnabled()) {
            return AuthenticationUtil.getJobUser(this.jobId);
        }
        return UserGroupInformation.getCurrentUser().getUserName();
    }
    
    private String getRoleName() {
        return PolicyUtils.DSS_ROLENAME;
    }
    
    private String getGroup() {
        return PolicyUtils.DSS_GROUP;
    }
    
    private String getServerName() {
        return PolicyUtils.DSS_HIVE_SERVER_NAME;
    }
    
    private String getSchemaName(final String sourceId) {
        final BasicDBObject sourceObj = (BasicDBObject)store.getObjectById("sources", new ObjectId(sourceId));
        return sourceObj.getString("hive_schema");
    }
    
 
    
    private void revokeAllOnDB(final String dbName) {
        PolicyUtils.logger.debug("Skipping revoke for sentry");
    }
    
    
    
    static {
        DSS_ROLENAME = DSSConf.getConfig().getString("dss_security_sentry_default_role_name", "dss-role");
        DSS_GROUP = DSSConf.getConfig().getString("dss_security_sentry_default_group", "infoworks");
        DSS_HIVE_SERVER_NAME = DSSConf.getConfig().getString("dss_security_sentry_default_hive_server_name", "server1");
        PolicyUtils.logger = LoggerFactory.getLogger(PolicyUtils.class.getName());
    }
}
