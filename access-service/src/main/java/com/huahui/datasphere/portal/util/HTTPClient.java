package com.huahui.datasphere.portal.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huahui.datasphere.portal.security.jwt.User;
import com.huahui.datasphere.portal.security.jwt.UserRoles;

public class HTTPClient
{
    private static final Logger logger;
    
    public static User getUser(final String url) {
        try {
            final CloseableHttpClient httpClient = HttpClients.createDefault();
            final HttpGet request = new HttpGet(url);
            setHeaders(request);
            final CloseableHttpResponse httpResponse = httpClient.execute((HttpUriRequest)request);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            final StringBuffer response = new StringBuffer();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
            httpClient.close();
            final User user = new User();
            final JSONObject obj = (JSONObject)new JSONParser().parse(response.toString());
            final String isError = (String)obj.get("error");
            if (isError != null) {
                HTTPClient.logger.debug("ERROR: " + obj.get("error"));
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
            HTTPClient.logger.error("failed to parse user object returned from REST service", (Throwable)e);
            return null;
        }
    }
    
    public static void post(final String url, final String body) throws Exception {
    }
    
    private static void setHeaders(final HttpGet request) {
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Connection", "keep-alive");
    }
    
    static {
        logger = LoggerFactory.getLogger((Class)HTTPClient.class);
    }
}
