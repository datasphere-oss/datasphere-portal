package com.huahui.datasphere.portal.util;

import java.io.*;
import org.json.simple.parser.*;
import org.json.simple.*;
import infoworks.tools.jwt.*;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;
import java.util.*;
import org.slf4j.*;

public class HTTPClient
{
    private static final Logger logger;
    
    public static IWUser getUser(final String url) {
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
            final IWUser user = new IWUser();
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
                if (role.equals(IWUserRoles.modeller.name())) {
                    userRoles.add(IWUserRoles.modeller.getValue());
                }
                else if (role.equals(IWUserRoles.admin.name())) {
                    userRoles.add(IWUserRoles.admin.getValue());
                }
                else {
                    if (!role.equals(IWUserRoles.analyst.name())) {
                        continue;
                    }
                    userRoles.add(IWUserRoles.analyst.getValue());
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