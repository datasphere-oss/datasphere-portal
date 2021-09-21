package com.huahui.datasphere.portal.security.authorization.ranger.clients;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.huahui.datasphere.portal.ExceptionHandling.DataSphereException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


public class ClientUtils
{
    private static final Logger LOGGER;
    private static String authString;
    private static final String baseUrl;
    
    public static WebResource.Builder prepareWebResourceBuilder(final String url) throws UnsupportedEncodingException {
        final byte[] bytesEncoded = Base64.getEncoder().encode(ClientUtils.authString.getBytes("UTF-8"));
        String authStringBase64 = new String(bytesEncoded);
        authStringBase64 = authStringBase64.trim();
        final Client client = Client.create();
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Basic " + authStringBase64);
        final WebResource webResource = client.resource(url);
        WebResource.Builder webResourceBuilder = webResource.accept(new String[] { "application/json" });
        for (final String key : headers.keySet()) {
            webResourceBuilder = (WebResource.Builder)webResourceBuilder.header(key, headers.get(key));
        }
        return webResourceBuilder;
    }
    
    public static boolean createPolicy(final String policyName, final String policyJson) {
        if (policyExists(policyName)) {
            try {
                final JSONObject newPolicy = new JSONObject(policyJson);
                final String newUser = ((JSONArray)((JSONObject)((JSONArray)newPolicy.get("permMapList")).get(0)).get("userList")).getString(0);
                if (userBelongsToPolicy(policyName, newUser)) {
                    return false;
                }
                addUserToPolicy(policyName, newUser);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        final String url = ClientUtils.baseUrl;
        try {
            ClientResponse response = null;
            final WebResource.Builder webResourceBuilder = prepareWebResourceBuilder(url);
            response = (ClientResponse)webResourceBuilder.post((Class)ClientResponse.class, policyJson);
            if (response.getStatus() != 200) {
                throw new DataSphereException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        catch (Exception e2) {
            throw new DataSphereException("Error in creating policy ", e2);
        }
        return true;
    }
    
    public static void addUserToPolicy(final String policyName, final String userName) {
        final int policyId = getPolicyId(policyName);
        final String policyJsonStr = getPolicyDetail(policyName);
        try {
            final JSONObject policyJson = new JSONObject(policyJsonStr);
            final JSONArray users = (JSONArray)((JSONObject)((JSONArray)policyJson.get("permMapList")).get(0)).get("userList");
            users.put(userName);
            final String url = ClientUtils.baseUrl + "/" + policyId;
            ClientResponse response = null;
            final WebResource.Builder webResourceBuilder = prepareWebResourceBuilder(url);
            response = (ClientResponse)webResourceBuilder.put((Class)ClientResponse.class, policyJson.toString());
            if (response.getStatus() != 200) {
                throw new DataSphereException("Failed : HTTP error code : " + response.getStatus() + ", response:" + response);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
    }
    
    public static String getPolicyDetail(final String policyName) {
        final int policyId = getPolicyId(policyName);
        final String url = ClientUtils.baseUrl + "/" + policyId;
        try {
            ClientResponse response = null;
            final WebResource.Builder webResourceBuilder = prepareWebResourceBuilder(url);
            response = (ClientResponse)webResourceBuilder.get((Class)ClientResponse.class);
            if (response.getStatus() != 200) {
                throw new DataSphereException("Failed : HTTP error code : " + response.getStatus());
            }
            return (String)response.getEntity((Class)String.class);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new DataSphereException("Error in searching policy ", e);
        }
    }
    
    public static int getPolicyId(final String policyName) {
        final String url = ClientUtils.baseUrl + "?policyName=" + policyName;
        try {
            ClientResponse response = null;
            final WebResource.Builder webResourceBuilder = prepareWebResourceBuilder(url);
            response = (ClientResponse)webResourceBuilder.get((Class)ClientResponse.class);
            if (response.getStatus() != 200) {
                throw new DataSphereException("Failed : HTTP error code : " + response.getStatus());
            }
            final String respDataStr = (String)response.getEntity((Class)String.class);
            final JSONObject respData = new JSONObject(respDataStr);
            final JSONArray policies = (JSONArray)respData.get("vXPolicies");
            if (policies.length() > 0) {
                return ((JSONObject)policies.get(0)).getInt("id");
            }
            return -1;
        }
        catch (Exception e) {
            ClientUtils.LOGGER.error(Throwables.getStackTraceAsString((Throwable)e));
            throw new DataSphereException("Error in searching policy ", e);
        }
    }
    
    public static boolean deletePolicy(final String policyName) {
        if (!policyExists(policyName)) {
            return false;
        }
        final int policyId = getPolicyId(policyName);
        if (policyId == -1) {
            return false;
        }
        final String url = ClientUtils.baseUrl + "/" + policyId;
        try {
            ClientResponse response = null;
            final WebResource.Builder webResourceBuilder = prepareWebResourceBuilder(url);
            response = (ClientResponse)webResourceBuilder.delete((Class)ClientResponse.class);
            if (response.getStatus() != 204) {
                throw new DataSphereException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new DataSphereException("Error in deleting policy " + e.getMessage());
        }
        return true;
    }
    
    public static boolean policyExists(final String policyName) {
        final String url = ClientUtils.baseUrl + "?policyName=" + policyName;
        try {
            ClientResponse response = null;
            final WebResource.Builder webResourceBuilder = prepareWebResourceBuilder(url);
            response = (ClientResponse)webResourceBuilder.get((Class)ClientResponse.class);
            if (response.getStatus() != 200) {
                throw new DataSphereException("Failed : HTTP error code : " + response.getStatus());
            }
            final String respDataStr = (String)response.getEntity((Class)String.class);
            final JSONObject respData = new JSONObject(respDataStr);
            final JSONArray policies = (JSONArray)respData.get("vXPolicies");
            return policies.length() > 0;
        }
        catch (Exception e) {
            ClientUtils.LOGGER.error(Throwables.getStackTraceAsString((Throwable)e));
            throw new DataSphereException("Error in searching policy ", e);
        }
    }
    
    public static boolean userBelongsToPolicy(final String policyName, final String newUser) {
        if (policyExists(policyName)) {
            final String policyDetail = getPolicyDetail(policyName);
            try {
                final JSONObject policy = new JSONObject(policyDetail);
                final JSONArray users = (JSONArray)((JSONObject)((JSONArray)policy.get("permMapList")).get(0)).get("userList");
                for (int i = 0; i < users.length(); ++i) {
                    if (users.getString(i).compareTo(newUser) == 0) {
                        return true;
                    }
                }
                return false;
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(ClientUtils.class.getName());
        ClientUtils.authString = "dss_security_ranger_admin_user" + ":" + "dss_security_ranger_admin_passwd";
        baseUrl = "http://" + "dss_security_ranger_ip" + ":" + "dss_security_ranger_port" + "/service/public/api/policy";
    }
}
