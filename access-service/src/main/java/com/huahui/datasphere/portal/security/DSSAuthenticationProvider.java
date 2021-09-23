/*
 * Apache License
 * 
 * Copyright (c) 2020 HuahuiData
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

package com.huahui.datasphere.portal.security;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;
import com.huahui.datasphere.portal.client.HttpRestClient;
import com.huahui.datasphere.portal.model.UserToken;
import com.huahui.datasphere.portal.security.aes.AES256;
import com.huahui.datasphere.portal.security.po.User;
import com.huahui.datasphere.portal.util.JerseyClient;


@Component
public class DSSAuthenticationProvider implements AuthenticationProvider
{
    private static final Logger logger;
    String hostName;
    String port;
    HttpRestClient client;
    Map<String, UserToken> tokenMap;
    
    public DSSAuthenticationProvider() {
        this.hostName = "localhost";
        this.port = "2999";
        this.tokenMap = new HashMap<String, UserToken>();
    }
    
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        final String password = authentication.getCredentials().toString();
        String token = null;
        try {
            if (username.equals("datasphere-token")) {
//            	Base64Decoder 改为 Base64.getDecoder()
                token = AES256.decryptData(Base64.getDecoder().decode(password));
                final String[] tokenParts = token.split(":");
                if (tokenParts.length != 2) {
                    throw new UsernameNotFoundException("Invalid token, user not found.");
                }
                username = tokenParts[0];
                token = password;
            }
            else {
                UserToken userToken = this.tokenMap.get(username);
                if (userToken == null) {
                    userToken = this.getUserToken(username, password);
                    this.tokenMap.put(username, userToken);
                }
                else {
                    final Date currentTime = new Date();
                    if (currentTime.getTime() - userToken.getCreatedAt().getTime() > 15000L) {
                        userToken = this.getUserToken(username, password);
                        this.tokenMap.put(username, userToken);
                    }
                }
                token = userToken.getToken();
            }
            token = URLEncoder.encode(token, "UTF-8");
        }
        catch (Exception e) {
            DSSAuthenticationProvider.logger.error("Failed to get user auth token", (Throwable)e);
            Throwables.propagate((Throwable)e);
        }
        final String url = "http://" + this.hostName + ":" + this.port + "/v1.1/user/profile.json?user=" + username + "&auth_token=" + token;
        final User user = JerseyClient.getUser(url);
        if (user == null) {
            DSSAuthenticationProvider.logger.error("Authentication failed for user: " + username);
            throw new BadCredentialsException("Bad Credentials");
        }
        DSSAuthenticationProvider.logger.info("Successfully Authenticated user: " + username + " with role: " + user.getRoles());
        final List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        for (final String role : user.getRoles()) {
            roles.add((GrantedAuthority)new SimpleGrantedAuthority(role));
        }
        return (Authentication)new UsernamePasswordAuthenticationToken(username, null, (Collection)roles);
    }
    
    private UserToken getUserToken(final String username, final String password) throws IOException, ParseException {
        try {
            final String authTokenUrl = "http://" + this.hostName + ":" + this.port + "/v1.1/user/auth_token.json?user=" + username + "&pass=" + password;
            final HttpGet httpGet = new HttpGet(new URI(authTokenUrl));
            httpGet.addHeader("Content-Type", "application/json");
            httpGet.addHeader("Connection", "keep-alive");
            final CloseableHttpResponse httpResponse = HttpClients.createDefault().execute((HttpUriRequest)httpGet);
            final int statusCode = httpResponse.getStatusLine().getStatusCode();
            final String response = EntityUtils.toString(httpResponse.getEntity());
            DSSAuthenticationProvider.logger.info("Response = " + response);
            if (statusCode != 200) {
                throw new IOException("Invalid response " + statusCode + " with response \n" + response);
            }
            final JSONParser parser = new JSONParser();
            final JSONObject object = (JSONObject)parser.parse(response);
            String error = (String)object.get("error");
            if (error != null) {
                final String error_desc = (String)object.get("error_desc");
                if (error_desc != null) {
                    error = error + ": " + error_desc;
                }
                throw new BadCredentialsException(error);
            }
            final String token = object.get("result").toString();
            return new UserToken(token);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public boolean supports(final Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
    
    public List<GrantedAuthority> getAuthorities(final List<String> roles) {
        final List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
        for (final String role : roles) {
            authList.add((GrantedAuthority)new SimpleGrantedAuthority(role));
        }
        return authList;
    }
    
    static {
        logger = LoggerFactory.getLogger((Class)DSSAuthenticationProvider.class);
    }
}
