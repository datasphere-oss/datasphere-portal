package com.huahui.datasphere.portal.security.aes;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;

public class EncryptionUtil {
  public static String decryptPassword(String encryptedPassword) {
    return AES256.decryptData(Base64.getDecoder().decode(encryptedPassword)); 
  }
  
  public static String getMetadbPassword() {
    return decryptPassword("metadbPassword");
  }
  
  public static byte[] getRandomNonce(int numBytes) {
    byte[] nonce = new byte[numBytes];
    (new SecureRandom()).nextBytes(nonce);
    return nonce;
  }
  
  public static SecretKey getAESKeyFromPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
    SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    return secret;
  }
}
