package com.huahui.datasphere.portal.security.authorization.ranger.clients;

import com.huahui.datasphere.portal.configuration.DSSConf;

public class HiveClient
{
    private final String POLICY_NAME_PREFIX = "-hive-";
    
    public boolean createPolicy(final String policyName, final String policyDesc, final String dbName, final String userName) {
        final String prefixedPolicyName = "-hive-" + policyName;
        final String inputJsonStr = "{\"policyName\":\"" + prefixedPolicyName + "\",\"databases\":\"" + dbName + "\",\"tables\":\"*\",\"columns\":\"*\",\"udfs\":\"\",\"tableType\":\"Inclusion\", \"columnType\":\"Inclusion\",\"description\":\"" + policyDesc + "\",\"repositoryName\":\"" + DSSConf.getConfig().getString("dss_security_ranger_hive_repo", "") + "\",\"repositoryType\":\"hive\",\"permMapList\":[{\"userList\":[\"" + userName + "\"],\"permList\":[\"select\", \"update\", \"Create\", \"Drop\", \"Alter\", \"Index\", \"Lock\", \"All\"]}],\"isEnabled\":true,\"isAuditEnabled\":true, \"version\":\"1\"}";
        return ClientUtils.createPolicy(prefixedPolicyName, inputJsonStr);
    }
    
    public boolean createPolicy(final String policyName, final String policyDesc, final String dbName, final String tableList, final String userName) {
        System.out.println("DBNAME is:" + dbName);
        System.out.println("Table list is: " + tableList);
        final String prefixedPolicyName = "-hive-" + policyName;
        final String inputJsonStr = "{\"policyName\":\"" + prefixedPolicyName + "\",\"databases\":\"" + dbName + "\",\"tables\":\"" + tableList + "\",\"columns\":\"*\",\"udfs\":\"\",\"tableType\":\"Inclusion\", \"columnType\":\"Inclusion\",\"description\":\"" + policyDesc + "\",\"repositoryName\":\"" + DSSConf.getConfig().getString("dss_security_ranger_hive_repo", "") + "\",\"repositoryType\":\"hive\",\"permMapList\":[{\"userList\":[\"" + userName + "\"],\"permList\":[\"select\", \"update\", \"Create\", \"Drop\", \"Alter\", \"Index\", \"Lock\", \"All\"]}],\"isEnabled\":true,\"isAuditEnabled\":true, \"version\":\"1\"}";
        return ClientUtils.createPolicy(prefixedPolicyName, inputJsonStr);
    }
    
    public void addUserToPolicy(final String policyName, final String userName) {
        ClientUtils.addUserToPolicy("-hive-" + policyName, userName);
    }
    
    public String getPolicyDetail(final String policyName) {
        return ClientUtils.getPolicyDetail("-hive-" + policyName);
    }
    
    public boolean deletePolicy(final String policyName) {
        return ClientUtils.deletePolicy("-hive-" + policyName);
    }
    
    public static void main(final String[] args) {
        final HiveClient hiveClient = new HiveClient();
        hiveClient.createPolicy("ranger-test-1234", "", "dss_source_weather_csv", "testuser");
    }
}
