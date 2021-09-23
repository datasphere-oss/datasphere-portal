/*
 * Apache License
 * 
 * Copyright (c) 2021 HuahuiData
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huahui.datasphere.portal.client;

import org.apache.http.impl.client.*;
import java.net.*;
import org.apache.http.impl.conn.*;
import org.apache.http.conn.routing.*;
import java.io.*;
import org.apache.http.entity.*;
import org.apache.http.*;
import org.apache.http.util.*;
import org.apache.http.client.methods.*;
import org.slf4j.*;

import com.huahui.datasphere.portal.security.jwt.JwtTokenUtil;

public class HttpRestClient implements RestClient
{
    String jobId;
    private CloseableHttpClient client;
    private String proxyHost;
    private int proxyPort;
    private boolean retryGet;
    private static final Logger LOGGER;
    
    HttpRestClient(final String jobId) {
        this.proxyHost = null;
        this.retryGet = false;
        this.jobId = jobId;
        this.init();
    }
    
    private void init() {
        if (this.proxyHost != null && !this.proxyHost.isEmpty()) {
            final HttpHost proxy = new HttpHost(this.proxyHost, this.proxyPort);
            HttpRestClient.LOGGER.debug("Proxy : " + proxy);
            this.client = HttpClients.custom().setProxy(proxy).build();
        }
        else {
            final SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
            this.client = HttpClients.custom().setRoutePlanner((HttpRoutePlanner)routePlanner).build();
        }
    }
    
    @Override
    public String executeDeleteRequest(final String url) throws IOException {
        final HttpDelete method = new HttpDelete(url);
        try {
            return this.execute((HttpUriRequest)method);
        }
        catch (IOException e) {
            throw new IOException("Failed to execute delete request for url " + url);
        }
        finally {
            method.reset();
        }
    }
    
    @Override
    public String executeGetRequest(final String url) throws IOException {
        final HttpGet method = new HttpGet(url);
        try {
            return this.execute((HttpUriRequest)method);
        }
        catch (IOException e) {
            throw new IOException("Failed to execute get request for url " + url);
        }
        finally {
            method.reset();
        }
    }
    
    @Override
    public String executePostRequest(final String url, final String postRequestData) throws IOException {
        final HttpPost method = new HttpPost(url);
        try {
            method.setEntity((HttpEntity)new StringEntity(postRequestData));
            return this.execute((HttpUriRequest)method);
        }
        catch (IOException e) {
            throw new IOException("Failed to execute post request for url " + url + " with request body \n" + postRequestData, e);
        }
        finally {
            method.reset();
        }
    }
    
    @Override
    public String executePutRequest(final String url, final String postRequestData) throws IOException {
        final HttpPut method = new HttpPut(url);
        try {
            method.setEntity((HttpEntity)new StringEntity(postRequestData));
            return this.execute((HttpUriRequest)method);
        }
        catch (IOException e) {
            throw new IOException("Failed to execute put request for url " + url + " with request body \n" + postRequestData, e);
        }
        finally {
            method.reset();
        }
    }
    
    public String execute(final HttpUriRequest request) throws IOException {
        CloseableHttpResponse response = null;
        try {
            this.setHeaders(request);
            final long start = System.currentTimeMillis();
            response = this.client.execute(request);
            final long end = System.currentTimeMillis();
            final int statusCode = response.getStatusLine().getStatusCode();
            String responseString = EntityUtils.toString(response.getEntity());
            HttpRestClient.LOGGER.debug("Response status code " + statusCode + "\nResponse : " + responseString + "\nTime for call in ms: " + (end - start));
            if (statusCode == 401) {
                JwtTokenUtil.refreshToken(this.jobId);
                if (this.retryGet) {
                    this.retryGet = false;
                    throw new IOException("Invalid response " + statusCode + " with response \n" + responseString);
                }
                this.retryGet = true;
                responseString = this.execute(request);
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
    
    public void setHeaders(final HttpUriRequest request) {
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Connection", "keep-alive");
        request.setHeader("Authorization", "Bearer " + JwtTokenUtil.getToken(this.jobId));
    }
    
    @Override
    public void close() throws IOException {
        if (this.client != null) {
            this.client.close();
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)HttpRestClient.class);
    }
}
