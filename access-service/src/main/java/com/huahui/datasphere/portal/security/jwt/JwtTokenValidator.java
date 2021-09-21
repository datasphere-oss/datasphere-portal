package com.huahui.datasphere.portal.security.jwt;

import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtTokenValidator
{
    private static Logger logger;
    private static final long EXPIRY_PADDING = 30000L;
    
    public static User parseToken(final String token) {
        User parsedUser = null;
        try {
            final Claims claims = (Claims)Jwts.parser().setSigningKey((Key)getPublicKey()).parseClaimsJws(token).getBody();
            final ObjectMapper mapper = new ObjectMapper();
            if (isTokenExpired(claims)) {
                JwtTokenValidator.logger.error("Claims not valid");
                return null;
            }
            parsedUser = (User)mapper.readValue(claims.get("user").toString(), (Class)User.class);
        }
        catch (Exception e) {
            JwtTokenValidator.logger.error("ERROR: Failed parsing jwt token. ", (Throwable)e);
        }
        return parsedUser;
    }
    
    public static boolean isTokenExpired(final String token) {
        try {
            final Claims body = (Claims)Jwts.parser().setSigningKey((Key)getPublicKey()).parseClaimsJws(token).getBody();
            return isTokenExpired(body);
        }
        catch (Exception e) {
            JwtTokenValidator.logger.error("ERROR: Failed parsing jwt token. ", (Throwable)e);
            return false;
        }
    }
    
    private static boolean isTokenExpired(final Claims claims) {
        final Date nbfTime = claims.getNotBefore();
        if (nbfTime != null && nbfTime.compareTo(new Date()) > 0) {
            JwtTokenValidator.logger.error("Token can not be used before nbf time.");
            return true;
        }
        final Date expTime = claims.getExpiration();
        if (expTime != null) {
            final Date paddedExp = new Date(expTime.getTime() - 30000L);
            if (paddedExp.compareTo(new Date()) < 0) {
                JwtTokenValidator.logger.error("Token has been expired.");
                return true;
            }
        }
        return false;
    }
    
    private static PublicKey getPublicKey() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        final String fileName = new Properties().getProperty("publickey_path");
        return KeyManager.getPublicKeyFromFile(fileName);
    }
    
    static {
        JwtTokenValidator.logger = Logger.getLogger((Class)JwtTokenValidator.class);
    }
}
