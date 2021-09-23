package com.huahui.datasphere.portal.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huahui.datasphere.portal.security.jwt.JwtTokenUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class JerseyRestClient implements RestClient
{
    String jobId;
    private boolean retryGet;
    private static final Logger LOGGER;
    
    public JerseyRestClient(final String jobId) {
        this.retryGet = false;
        this.jobId = jobId;
        this.init();
    }
    
    private void init() {
    }
    
    @Override
    public String executeGetRequest(final String url) throws IOException {
        try {
            final Client client = Client.create();
            final WebResource webResource = client.resource(url);
            return this.execute(webResource.getRequestBuilder(), "GET", null);
        }
        catch (IOException e) {
            throw new IOException("Failed to execute get request for url " + url);
        }
    }
    
    @Override
    public String executePostRequest(final String url, final String postRequestData) throws IOException {
        try {
            final Client client = Client.create();
            final WebResource webResource = client.resource(url);
            return this.execute(webResource.getRequestBuilder(), "POST", postRequestData);
        }
        catch (IOException e) {
            throw new IOException("Failed to execute post request for url " + url);
        }
    }
    
    @Override
    public String executePutRequest(final String url, final String putRequestData) throws IOException {
        try {
            final Client client = Client.create();
            final WebResource webResource = client.resource(url);
            return this.execute(webResource.getRequestBuilder(), "PUT", putRequestData);
        }
        catch (IOException e) {
            throw new IOException("Failed to execute put request for url " + url);
        }
    }
    
    @Override
    public String executeDeleteRequest(final String url) throws IOException {
        try {
            final Client client = Client.create();
            final WebResource webResource = client.resource(url);
            return this.execute(webResource.getRequestBuilder(), "DELETE", null);
        }
        catch (IOException e) {
            throw new IOException("Failed to execute delete request for url " + url);
        }
    }
    
    public String execute(final WebResource.Builder builder, final String requestType, final String body) throws IOException {
        ClientResponse response = null;
        try {
            this.setHeaders(builder);
            final long start = System.currentTimeMillis();
            if (requestType.equalsIgnoreCase("GET")) {
                response = (ClientResponse)builder.get((Class)ClientResponse.class);
            }
            else if (requestType.equalsIgnoreCase("POST")) {
                response = (ClientResponse)builder.post((Class)ClientResponse.class, (Object)body);
            }
            else if (requestType.equalsIgnoreCase("PUT")) {
                response = (ClientResponse)builder.put((Class)ClientResponse.class, (Object)body);
            }
            else {
                if (!requestType.equalsIgnoreCase("DELETE")) {
                    JerseyRestClient.LOGGER.debug("Http method not supported. " + requestType);
                    return null;
                }
                response = (ClientResponse)builder.delete((Class)ClientResponse.class);
            }
            final long end = System.currentTimeMillis();
            final int statusCode = response.getStatus();
            String responseString = (String)response.getEntity((Class)String.class);
            JerseyRestClient.LOGGER.debug("Response status code " + statusCode + "\nResponse : " + responseString + "\nTime for call in ms: " + (end - start));
            if (statusCode == 401) {
                JwtTokenUtil.refreshToken(this.jobId);
                if (this.retryGet) {
                    this.retryGet = false;
                    throw new IOException("Invalid response " + statusCode + " with response \n" + responseString);
                }
                this.retryGet = true;
                responseString = this.execute(builder, requestType, body);
            }
            else if (statusCode != 200) {
                throw new IOException("Invalid response " + statusCode + " with response \n" + responseString);
            }
            this.retryGet = false;
            return responseString;
        }
        finally {
            if (response != null) {
                response.close();
            }
        }
    }
    
    public void setHeaders(final WebResource.Builder builder) {
        builder.header("Content-Type", (Object)"application/json");
        builder.header("Connection", (Object)"keep-alive");
        builder.header("Authorization", (Object)("Bearer " + JwtTokenUtil.getToken(this.jobId)));
    }
    
    @Override
    public void close() throws IOException {
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)JerseyRestClient.class);
    }
}
