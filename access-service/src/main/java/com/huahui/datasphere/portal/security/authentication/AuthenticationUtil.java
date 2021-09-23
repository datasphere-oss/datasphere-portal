package com.huahui.datasphere.portal.security.authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.security.UserGroupInformation;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.huahui.datasphere.portal.ExceptionHandling.DataSphereException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import infoworks.tools.config.IWConf;
import infoworks.tools.config.IngestionConfig;
import infoworks.tools.config.IngestionGlobalParams;
import infoworks.tools.utils.IWUtil;

public class AuthenticationUtil
{
    private static final Logger LOGGER;
    private static final AtomicBoolean isLoggedIn;
    
    public static boolean isKerberosEnabled() {
        return Boolean.valueOf("false");
    }
    
    public static boolean isImpersonationEnabled() {
        return Boolean.valueOf("false");
    }
    
    public static String getJobUser(final String jobId) throws IOException {
        final DBObject jobsObj = store.getObjectById("jobs", new ObjectId(jobId));
        return getUserProfileSystemUserName((String)jobsObj.get("createdBy"));
    }
    
   
    
  
    
    private static Pair<String, String> getDefaultKerberosLoginInfo() {
        if (!isKerberosEnabled()) {
            throw new DataSphereException("Invalid call. Should not have reached");
        }
        final String kerberosPrincipal = "iw_security_kerberos_default_principal";
        if (kerberosPrincipal == null || kerberosPrincipal.isEmpty()) {
            throw new DataSphereException("Could not resolve default principal");
        }
        final String kerberosKeyTabFile = "iw_security_kerberos_default_keytab_file";
        if (kerberosKeyTabFile == null || kerberosKeyTabFile.isEmpty()) {
            throw new DataSphereException("Could not default  resolve key tab file");
        }
        return (Pair<String, String>)Pair.of(kerberosPrincipal, kerberosKeyTabFile);
    }
    
    public static void shellLoginWithDefaultKerberosCredentials() throws IOException, InterruptedException {
        if (!isKerberosEnabled()) {
            return;
        }
        final String logincmd = getDefaultKerberosTicketInitCommand();
        final ProcessBuilder pb = new ProcessBuilder(new String[] { "bash", "-c", logincmd });
        final Process process = pb.start();
        final StringBuilder out = new StringBuilder();
        final BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = br.readLine()) != null) {
            out.append(line).append('\n');
            AuthenticationUtil.LOGGER.debug("CmdOutput: " + line);
        }
        process.waitFor();
    }
    
    public static void loginWithDefaultKerberosCredentials(final String jobId, final String jobProcess) {
        loginWithDefaultKerberosCredentials();
    }
    
    public static synchronized void loginWithDefaultKerberosCredentials() {
        if (AuthenticationUtil.isLoggedIn.get()) {
            return;
        }
        final Pair<String, String> defaultKerberosLoginInfo = getDefaultKerberosLoginInfo();
        final String kerberosPrincipal = (String)defaultKerberosLoginInfo.getLeft();
        final String kerberosKeyTabFile = (String)defaultKerberosLoginInfo.getRight();
        AuthenticationUtil.LOGGER.info("Trying to login using Principal " + kerberosPrincipal + ", Keytabfile : " + kerberosKeyTabFile);
        try {
            UserGroupInformation.loginUserFromKeytab(kerberosPrincipal, kerberosKeyTabFile);
        }
        catch (IOException e) {
            AuthenticationUtil.LOGGER.error("Error while Trying to login using Principal " + kerberosPrincipal + ", Keytabfile : " + kerberosKeyTabFile);
            AuthenticationUtil.LOGGER.error(Throwables.getStackTraceAsString((Throwable)e));
            throw new DataSphereException(e);
        }
        AuthenticationUtil.isLoggedIn.set(true);
    }
    
    public static void reloginUserWithDefaultKerberosCredentials(final String jobId, final String jobProcess) {
        reloginUserWithDefaultKerberosCredentials();
    }
    
    public static void reloginUserWithDefaultKerberosCredentials() {
        if (!AuthenticationUtil.isLoggedIn.get()) {
            loginWithDefaultKerberosCredentials();
            return;
        }
        AuthenticationUtil.LOGGER.debug("Trying to relogin the user");
        try {
            UserGroupInformation.getCurrentUser().checkTGTAndReloginFromKeytab();
        }
        catch (IOException e) {
            AuthenticationUtil.LOGGER.debug("Error while trying to relogin");
            AuthenticationUtil.LOGGER.debug(Throwables.getStackTraceAsString((Throwable)e));
            throw new DataSphereException(e);
        }
    }
    
    private static Pair<String, String> getKerberosLoginInfo(final String jobId) throws IOException {
        final DBObject jobsObj = store.getObjectById("jobs", new ObjectId(jobId));
        final String userId = (String)jobsObj.get("createdBy");
        final BasicDBObject profileObject = getUserProfileObject(userId);
        final String kerberosPrincipal = profileObject.getString("kerberos_principal");
        if (kerberosPrincipal == null || kerberosPrincipal.isEmpty()) {
            throw new IOException("Could not resolve principal");
        }
        final String kerberosKeyTabFile = profileObject.getString("kerberos_keytab_file");
        if (kerberosKeyTabFile == null || kerberosKeyTabFile.isEmpty()) {
            throw new IOException("Could not resolve key tab file");
        }
        return (Pair<String, String>)Pair.of(kerberosPrincipal, kerberosKeyTabFile);
    }
    
    public static String getDefaultKerberosTicketInitCommand() throws IOException {
        if (!isKerberosEnabled()) {
            throw new DataSphereException("Invalid call. Should not have reached");
        }
        final Pair<String, String> kerberosLoginInfo = getDefaultKerberosLoginInfo();
        final String cmd = "kinit -kt " + (String)kerberosLoginInfo.getRight() + " " + (String)kerberosLoginInfo.getLeft();
        return cmd;
    }
    
    public static <T> T doAs(final String mongoJobId, final String jobProcess, final PrivilegedExceptionAction<T> pea) throws Exception {
        return doAs(mongoJobId, jobProcess, pea, false);
    }
    
    public static <T> T doAs(final String mongoJobId, final String jobProcess, final PrivilegedExceptionAction<T> pea, final boolean forceDefaultUser) throws Exception {
        final boolean impersonationEnabled = isImpersonationEnabled();
        final boolean kerberosEnabled = isKerberosEnabled();
        if (!impersonationEnabled || forceDefaultUser) {
            if (kerberosEnabled) {
                reloginUserWithDefaultKerberosCredentials(mongoJobId, jobProcess);
            }
            return pea.run();
        }
        String userName = null;
        UserGroupInformation ugi = null;
        if (!kerberosEnabled) {
            userName = getJobUser(mongoJobId);
            ugi = UserGroupInformation.createProxyUser(userName, UserGroupInformation.getLoginUser());
        }
        else {
            final DBObject jobsObj = store.getObjectById("jobs", new ObjectId(mongoJobId));
            final String userid = (String)jobsObj.get("createdBy");
            final Pair<String, String> kerberosLoginInfo = getKerberosLoginInfo(mongoJobId);
            final String kerberosPrincipal = (String)kerberosLoginInfo.getLeft();
            final String kerberosKeyTabFile = (String)kerberosLoginInfo.getRight();
            AuthenticationUtil.LOGGER.debug("User id " + userid + ", Principal " + kerberosPrincipal + ", Keytabfile : " + kerberosKeyTabFile);
            ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(kerberosPrincipal, kerberosKeyTabFile);
            userName = kerberosPrincipal;
        }
        AuthenticationUtil.LOGGER.debug("Running job as: " + userName);
        try {
            return (T)ugi.doAs((PrivilegedExceptionAction)pea);
        }
        finally {
            if (kerberosEnabled) {
                reloginUserWithDefaultKerberosCredentials(mongoJobId, jobProcess);
            }
        }
    }
    
    public static String getUserProfileSystemUserName(final String userId) throws IOException {
        final BasicDBObject profileObject = getUserProfileObject(userId);
        final String userName = profileObject.getString("systemusername");
        if (userName == null || userName.isEmpty()) {
            throw new IOException("Could not resolve username");
        }
        return userName;
    }
    
    private static BasicDBObject getUserProfileObject(final String userId) throws IOException {
        if (userId == null || userId.isEmpty()) {
            throw new IOException("Could not read userId for the submitted job");
        }
        final BasicDBObject userObject = (BasicDBObject)store.getObjectById("users", userId);
        if (userObject == null || userObject.isEmpty()) {
            throw new IOException("Could not find user with the given userId for the submitted job");
        }
        final BasicDBObject profileObject = (BasicDBObject)userObject.get("profile");
        if (profileObject == null || profileObject.isEmpty()) {
            throw new IOException("Missing user profile");
        }
        return profileObject;
    }
    
    public static String getHiveKerberosPrincipal(final String jobId) throws IOException {
        if (!isKerberosEnabled()) {
            throw new DataSphereException("Invalid call. Should not have reached");
        }
        return "iw_security_kerberos_hiveserver_principal";
    }
    
    public static String getImpalaKerberosPrincipal(final String jobId) throws IOException {
        if (!isKerberosEnabled()) {
            throw new DataSphereException("Invalid call. Should not have reached");
        }
        return "iw_security_kerberos_impalaserver_principal";
    }
    
    public static String getSSLTrustStorePath() throws IOException {
        if (!isSSLEnabled()) {
            throw new DataSphereException("SSL Disabled. Should not have reached");
        }
        return DSSConf.getConfig().getString("iw_hive_ssl_truststore_path", "");
    }
    
    public static String getSSLTrustStorePassword() throws IOException {
        if (!isSSLEnabled()) {
            throw new DataSphereException("SSL Disabled. Should not have reached");
        }
        return IWUtil.getSSLTrustPassword();
    }
    
 
    
 
    

    
  
    
    public static void main(final String[] args) {
        try {
            if (args.length != 1) {
                throw new IOException("Invalid number of arguments.");
            }
            final String cmd = args[0];
            if (!cmd.equals("loginWithDefaultKerberosCredentials")) {
                throw new IOException("Invalid command");
            }
            shellLoginWithDefaultKerberosCredentials();
        }
        catch (Exception e) {
            System.err.println("Error while copying : \n" + Throwables.getStackTraceAsString((Throwable)e));
            System.exit(1);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AuthenticationUtil.class);
        isLoggedIn = new AtomicBoolean(false);
    }
}
