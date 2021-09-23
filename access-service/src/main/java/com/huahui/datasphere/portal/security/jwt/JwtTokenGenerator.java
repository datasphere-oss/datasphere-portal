package com.huahui.datasphere.portal.security.jwt;

import java.security.Key;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Properties;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Preconditions;
import com.huahui.datasphere.portal.configuration.DSSConf;
import com.huahui.datasphere.portal.security.po.User;
import com.sun.research.ws.wadl.Application;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenGenerator
{
    public static String generateToken(final User user) throws Exception {
        final Claims claims = Jwts.claims().setSubject("DataSphere Access Token");
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonUser = mapper.writeValueAsString(user);
        claims.put("user", jsonUser);
        Long expOffset = Long.parseLong("5");
        expOffset *= 60000L;
        claims.setExpiration(new Date(new Date().getTime() + expOffset));
        claims.setNotBefore(new Date());
        claims.setIssuedAt(new Date());
//        claims.setAudience();
//        claims.setIssuer();
        final String keystoreFile = DSSConf.getConfig().getString("keystore",null);
        Preconditions.checkNotNull(keystoreFile, "Keystore file is not defined");
        final PrivateKey privateKey = KeyManager.getPrivateKeyFromKeystore(keystoreFile);
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.RS256, (Key)privateKey).compact();
    }
}
