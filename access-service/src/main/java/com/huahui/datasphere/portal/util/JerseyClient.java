package com.huahui.datasphere.portal.util;

import org.json.simple.parser.*;
import org.json.simple.*;
import java.util.*;
import org.springframework.http.*;

import com.huahui.datasphere.portal.security.po.User;
import com.huahui.datasphere.portal.security.po.UserRoles;
import com.sun.jersey.api.client.*;
import org.slf4j.*;

public class JerseyClient
{
    private static final Logger logger;
    
    public static User getUser(final String url) {
        try {
            final User user = new User();
            final String userStr = (String)get(url, null, null).getEntity((Class)String.class);
            final JSONObject obj = (JSONObject)new JSONParser().parse(userStr);
            final Object isError = obj.get("error");
            if (isError != null) {
                JerseyClient.logger.debug("ERROR: " + obj.toString());
                return null;
            }
            final JSONObject result = (JSONObject)obj.get("result");
            final JSONObject profile = (JSONObject)result.get("profile");
            user.setEmaild((String)profile.get("email"));
            user.setName((String)profile.get("name"));
            final JSONArray roles = (JSONArray)result.get("roles");
            final List<String> userRoles = new ArrayList<String>();
            for (final Object o : roles) {
                final String role = (String)o;
                if (role.equals(UserRoles.modeller.name())) {
                    userRoles.add(UserRoles.modeller.getValue());
                }
                else if (role.equals(UserRoles.admin.name())) {
                    userRoles.add(UserRoles.admin.getValue());
                }
                else {
                    if (!role.equals(UserRoles.analyst.name())) {
                        continue;
                    }
                    userRoles.add(UserRoles.analyst.getValue());
                }
            }
            user.setRoles((List)userRoles);
            return user;
        }
        catch (Exception e) {
            JerseyClient.logger.error("Failed in parsing user object.", (Throwable)e);
            e.printStackTrace();
            return null;
        }
    }
    
    public static ClientResponse get(String url, final Map<String, String> headers, final Map<String, String> params) throws Exception {
        url += getParamString(params);
        JerseyClient.logger.info("GET Url: " + url);
        final Client client = Client.create();
        final WebResource webResource = client.resource(url);
        final WebResource.Builder builder = webResource.accept(new String[] { MediaType.APPLICATION_JSON.toString() });
        setHeaders(builder, headers);
        return (ClientResponse)builder.get((Class)ClientResponse.class);
    }
    
    public static ClientResponse post(String url, final Map<String, String> headers, final Map<String, String> params, final String body) throws Exception {
        url += getParamString(params);
        JerseyClient.logger.info("POST Url: " + url);
        final Client client = Client.create();
        final WebResource webResource = client.resource(url);
        final WebResource.Builder builder = webResource.accept(new String[] { MediaType.APPLICATION_JSON.toString() });
        setHeaders(builder, headers);
        return (ClientResponse)builder.post((Class)ClientResponse.class, body);
    }
    
    public static ClientResponse put(String url, final Map<String, String> headers, final Map<String, String> params, final String body) throws Exception {
        url += getParamString(params);
        JerseyClient.logger.info("PUT Url: " + url);
        final Client client = Client.create();
        final WebResource webResource = client.resource(url);
        final WebResource.Builder builder = webResource.accept(new String[] { MediaType.APPLICATION_JSON.toString() });
        setHeaders(builder, headers);
        return (ClientResponse)builder.put((Class)ClientResponse.class, body);
    }
    
    public static ClientResponse delete(String url, final Map<String, String> headers, final Map<String, String> params) throws Exception {
        url += getParamString(params);
        JerseyClient.logger.info("DELETE Url: " + url);
        final Client client = Client.create();
        final WebResource webResource = client.resource(url);
        final WebResource.Builder builder = webResource.accept(new String[] { MediaType.APPLICATION_JSON.toString() });
        setHeaders(builder, headers);
        return (ClientResponse)builder.delete((Class)ClientResponse.class);
    }
    
    private static void setHeaders(final WebResource.Builder builder, final Map<String, String> headers) {
        if (headers != null) {
            for (final String key : headers.keySet()) {
                builder.header(key, headers.get(key));
            }
        }
    }
    
    private static String getParamString(final Map<String, String> params) {
        String paramStr = "";
        if (params != null) {
            paramStr = "?";
            for (final String key : params.keySet()) {
                paramStr = paramStr + key + "=" + params.get(key) + "&";
            }
            paramStr = paramStr.substring(0, paramStr.length() - 1);
        }
        return paramStr;
    }
    
    static {
        logger = LoggerFactory.getLogger((Class)JerseyClient.class);
    }
}
