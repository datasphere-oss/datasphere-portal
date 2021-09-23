package com.huahui.datasphere.portal.security.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.huahui.datasphere.portal.security.po.User;
import com.huahui.datasphere.portal.security.po.UserRoles;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


public class JwtTokenUtil
{
    private static Logger logger;
    private static Map<String, String> userToken;
    
    public static String getToken(final User user) {
        JwtTokenUtil.logger.debug("getToken function called.");
        String token = JwtTokenUtil.userToken.get(user.getEmaild());
        try {
            if (token == null || JwtTokenValidator.isTokenExpired(token)) {
                JwtTokenUtil.logger.debug("generating new token ...");
                token = JwtTokenGenerator.generateToken(user);
                JwtTokenUtil.userToken.put(user.getEmaild(), token);
            }
        }
        catch (Exception e) {
            JwtTokenUtil.logger.error("Failed getting jwt token.", (Throwable)e);
        }
        return token;
    }
    
    public static String getToken(final String jobId) {
        JwtTokenUtil.logger.debug(("getToken function called with jobId: " + jobId));
        String token = null;
        try {
            final DBObject jobsObj = store.getObjectById("jobs", new ObjectId(jobId));
            final String userId = (String)jobsObj.get("createdBy");
            token = getToken(getIWUser(userId));
        }
        catch (Exception e) {
            JwtTokenUtil.logger.error("Failed getting jwt token.", (Throwable)e);
        }
        return token;
    }
    
    public static void refreshToken(final String jobId) {
        JwtTokenUtil.logger.debug(("refreshToken function called with jobId: " + jobId));
        String token = null;
        try {
            final DBObject jobsObj = store.getObjectById("jobs", new ObjectId(jobId));
            final String userId = (String)jobsObj.get("createdBy");
            final User user = getIWUser(userId);
            token = JwtTokenGenerator.generateToken(user);
            JwtTokenUtil.userToken.put(user.getEmaild(), token);
        }
        catch (Exception e) {
            JwtTokenUtil.logger.error("Failed refreshing jwt token.", (Throwable)e);
        }
    }
    
    private static User getIWUser(final String userId) throws Exception {
        JwtTokenUtil.logger.debug(("getIWUser function called with userId: " + userId));
        if (userId == null || userId.isEmpty()) {
            throw new Exception("Invalid userId");
        }
        final BasicDBObject query = new BasicDBObject();
        query.put("_id", userId);
        final BasicDBObject userObject = (BasicDBObject)store.findOne("users", (DBObject)query);
        if (userObject == null || userObject.isEmpty()) {
            throw new IOException("Could not find user with the given userId: " + userId);
        }
        final BasicDBObject profileObject = (BasicDBObject)userObject.get("profile");
        final BasicDBList rolesArr = (BasicDBList)userObject.get("roles");
        if (profileObject == null || profileObject.isEmpty() || rolesArr == null) {
            throw new IOException("Missing user profile/roles");
        }
        final User user = new User();
        final String email = profileObject.getString("email");
        final List<String> userRoles = new ArrayList<String>();
        for (int i = 0; i < rolesArr.size(); ++i) {
            final String role = (String)rolesArr.get(i);
            if (role.equals(UserRoles.modeller.name())) {
                userRoles.add(UserRoles.modeller.getValue());
            }
            else if (role.equals(UserRoles.admin.name())) {
                userRoles.add(UserRoles.admin.getValue());
            }
            else if (role.equals(UserRoles.analyst.name())) {
                userRoles.add(UserRoles.analyst.getValue());
            }
        }
        user.setEmaild(email);
        user.setRoles(userRoles);
        return user;
    }
    
    static {
        JwtTokenUtil.logger = Logger.getLogger((Class)JwtTokenUtil.class);
        JwtTokenUtil.userToken = new HashMap<String, String>();
    }
}
