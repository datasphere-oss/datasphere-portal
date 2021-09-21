package com.huahui.datasphere.portal.security.authorization.ranger.utils;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.huahui.datasphere.portal.ExceptionHandling.DataSphereException;
import com.huahui.datasphere.portal.configuration.DSSConf;
import com.huahui.datasphere.portal.security.authentication.AuthenticationUtil;
import com.mongodb.DBObject;


public class PolicyUtils implements com.huahui.datasphere.portal.security.authorization.api.PolicyUtilsAPI
{
    private final int RANGER_POLICY_SYNC_INTERVAL_SECS;
    private final int RANGER_POLICY_SYNC_INTERVAL_MILLIS;
    private static final Logger LOGGER;
    private String userName;
    private String jobId;
    
    static {
        LOGGER = LoggerFactory.getLogger(PolicyUtils.class.getName());
    }
    
    public PolicyUtils(final String mongoJobId) throws IOException {
        this.RANGER_POLICY_SYNC_INTERVAL_SECS = DSSConf.getConfig().getInt("iw_security_ranger_policy_poll_interval_secs");
        this.RANGER_POLICY_SYNC_INTERVAL_MILLIS = this.RANGER_POLICY_SYNC_INTERVAL_SECS * 1000;
        this.userName = "";
        this.jobId = "";
        this.jobId = mongoJobId;
        this.userName = AuthenticationUtil.getJobUser(this.jobId);
    }
    
   
 
    
    private void waitForPolicySync(final boolean aPolicyUpdated) {
        try {
            if (aPolicyUpdated) {
                PolicyUtils.LOGGER.info("Waiting for " + this.RANGER_POLICY_SYNC_INTERVAL_SECS + " second(s) for ranger policies to set in.");
                Thread.sleep(this.RANGER_POLICY_SYNC_INTERVAL_MILLIS);
            }
            else {
                PolicyUtils.LOGGER.info("Policy already exists. ");
            }
        }
        catch (InterruptedException e) {
            PolicyUtils.LOGGER.error(Throwables.getStackTraceAsString((Throwable)e));
            throw new DataSphereException(e);
        }
    }
    
   
    
    private ObjectId getTableId(final Object tableIdObj) {
        ObjectId tableId = null;
        try {
            final DBObject tableObj = (DBObject)tableIdObj;
            tableId = (ObjectId)tableObj.get("_id");
        }
        catch (ClassCastException e) {
            e.printStackTrace();
            throw new DataSphereException(e);
        }
        return tableId;
    }
    
  
   
}
