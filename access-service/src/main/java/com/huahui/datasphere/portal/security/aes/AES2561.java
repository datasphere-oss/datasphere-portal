package com.huahui.datasphere.portal.security.aes;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.huahui.datasphere.portal.ExceptionHandling.DataSphereException;

public class AES2561 {
  private static final Logger LOGGER = LoggerFactory.getLogger(AES256.class.getName());
  
  private static final String SECRET_KEY = EncryptionConstants.IW_SECRET_KEY;
  
  private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
  
  private static final int TAG_LENGTH_BIT = 128;
  
  private static final int IV_LENGTH_BYTE = 12;
  
  private static final int SALT_LENGTH_BYTE = 16;
  
  private static final Charset UTF_8 = StandardCharsets.UTF_8;
  
  public static byte[] encryptData(String pText) {
    try {
      byte[] salt = EncryptionUtil2.getRandomNonce(16);
      byte[] iv = EncryptionUtil2.getRandomNonce(12);
      SecretKey aesKeyFromPassword = EncryptionUtil2.getAESKeyFromPassword(SECRET_KEY.toCharArray(), salt);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(1, aesKeyFromPassword, new GCMParameterSpec(128, iv));
      byte[] cipherText = cipher.doFinal(pText.getBytes());
      byte[] cipherTextWithIvSalt = ByteBuffer.allocate(salt.length + iv.length + cipherText.length).put(salt).put(iv).put(cipherText).array();
      return Base64.getEncoder().encode(cipherTextWithIvSalt);
    } catch (NoSuchAlgorithmException nsaEx) {
      LOGGER.error("Exception: No such algorithm");
      LOGGER.error(Throwables.getStackTraceAsString(nsaEx));
      throw new DataSphereException("Error while decrypting data", nsaEx);
    } catch (NoSuchPaddingException nspaEx) {
      LOGGER.error("Exception: Padding error");
      LOGGER.error(Throwables.getStackTraceAsString(nspaEx));
      throw new DataSphereException("Error while decrypting data", nspaEx);
    } catch (InvalidKeyException ikEx) {
      LOGGER.error("Exception: Invalid key error");
      LOGGER.error(Throwables.getStackTraceAsString(ikEx));
      throw new DataSphereException("Error while decrypting data", ikEx);
    } catch (IllegalBlockSizeException ibEx) {
      LOGGER.error("Exception: Invalid block size error");
      LOGGER.error(Throwables.getStackTraceAsString(ibEx));
      throw new DataSphereException("Error while decrypting data", ibEx);
    } catch (BadPaddingException bpEx) {
      LOGGER.error("Exception: Bad padding error");
      LOGGER.error(Throwables.getStackTraceAsString(bpEx));
      throw new DataSphereException("Error while decrypting data", bpEx);
    } catch (InvalidKeySpecException invld) {
      LOGGER.error("Exception: invalid key spec exception");
      LOGGER.error(Throwables.getStackTraceAsString(invld));
      throw new DataSphereException("Error while encrypting data", invld);
    } catch (InvalidAlgorithmParameterException invpm) {
      LOGGER.error("Exception: invalid algorithm parameter exception");
      LOGGER.error(Throwables.getStackTraceAsString(invpm));
      throw new DataSphereException("Error while encrypting data", invpm);
    } 
  }
  
  public static String decryptData(byte[] cText) {
    try {
      if (cText != null && cText.length == 0)
        return ""; 
      ByteBuffer bb = ByteBuffer.wrap(cText);
      byte[] salt = new byte[16];
      bb.get(salt);
      byte[] iv = new byte[12];
      bb.get(iv);
      byte[] cipherText = new byte[bb.remaining()];
      bb.get(cipherText);
      SecretKey aesKeyFromPassword = EncryptionUtil2.getAESKeyFromPassword(SECRET_KEY.toCharArray(), salt);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(2, aesKeyFromPassword, new GCMParameterSpec(128, iv));
      byte[] plainText = cipher.doFinal(cipherText);
      return new String(plainText, UTF_8);
    } catch (NoSuchAlgorithmException nsaEx) {
      LOGGER.error("Exception: No such algorithm");
      LOGGER.error(Throwables.getStackTraceAsString(nsaEx));
      throw new DataSphereException("Error while decrypting data", nsaEx);
    } catch (NoSuchPaddingException nspaEx) {
      LOGGER.error("Exception: Padding error");
      LOGGER.error(Throwables.getStackTraceAsString(nspaEx));
      throw new DataSphereException("Error while decrypting data", nspaEx);
    } catch (InvalidKeyException ikEx) {
      LOGGER.error("Exception: Invalid key error");
      LOGGER.error(Throwables.getStackTraceAsString(ikEx));
      throw new DataSphereException("Error while decrypting data", ikEx);
    } catch (IllegalBlockSizeException ibEx) {
      LOGGER.error("Exception: Invalid block size error");
      LOGGER.error(Throwables.getStackTraceAsString(ibEx));
      throw new DataSphereException("Error while decrypting data", ibEx);
    } catch (BadPaddingException bpEx) {
      LOGGER.error("Exception: Bad padding error");
      LOGGER.error(Throwables.getStackTraceAsString(bpEx));
      throw new DataSphereException("Error while decrypting data", bpEx);
    } catch (InvalidKeySpecException invld) {
      LOGGER.error("Exception: invalid key spec exception");
      LOGGER.error(Throwables.getStackTraceAsString(invld));
      throw new DataSphereException("Error while decrypting data", invld);
    } catch (InvalidAlgorithmParameterException invpm) {
      LOGGER.error("Exception: invalid algorithm parameter exception");
      LOGGER.error(Throwables.getStackTraceAsString(invpm));
      throw new DataSphereException("Error while decrypting data", invpm);
    } 
  }
}
