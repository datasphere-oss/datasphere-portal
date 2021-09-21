package com.huahui.datasphere.portal.security.authorization.ranger.clients;

import com.huahui.datasphere.portal.configuration.DSSConf;

public class HDFSClient
{
    private final String POLICY_NAME_PREFIX = "DSS-hdfs-";
    
    public boolean createPolicy(final String policyName, final String policyDesc, String folderPath, final String userName) {
        if (folderPath.length() > 0) {
            while (folderPath.charAt(folderPath.length() - 1) == '/') {
                folderPath = folderPath.substring(0, folderPath.length() - 1);
            }
        }
        final String prefixedPolicyName = "DSS-hdfs-" + policyName;
        final String inputJsonStr = "{\"policyName\":\"" + prefixedPolicyName + "\",\"resourceName\":\"" + folderPath + "\",\"description\":\"" + policyDesc + "\",\"repositoryName\":\"" + DSSConf.getConfig().getString("iw_security_ranger_hdfs_repo", "") + "\",\"repositoryType\":\"hdfs\",\"permMapList\":[{\"userList\":[\"" + userName + "\"],\"permList\":[\"Read\",\"Write\",\"Execute\"]}],\"isEnabled\":true,\"isRecursive\":true,\"isAuditEnabled\":true,\"version\":\"1\",\"replacePerm\":false}";
        return ClientUtils.createPolicy(prefixedPolicyName, inputJsonStr);
    }
    
    public void addUserToPolicy(final String policyName, final String userName) {
        ClientUtils.addUserToPolicy("DSS-hdfs-" + policyName, userName);
    }
    
    public String getPolicyDetail(final String policyName) {
        return ClientUtils.getPolicyDetail("DSS-hdfs-" + policyName);
    }
    
    public boolean deletePolicy(final String policyName) {
        return ClientUtils.deletePolicy("DSS-hdfs-" + policyName);
    }
    
    public boolean checkIfPolicyExists(final String policyName) {
        return ClientUtils.policyExists("DSS-hdfs-" + policyName);
    }
    
    public static void main(final String[] args) {
        final HDFSClient hdfsClient = new HDFSClient();
        hdfsClient.createPolicy("test_abhi_1", "", "/temp/xyz/abcd", "testuser");
    }
}
