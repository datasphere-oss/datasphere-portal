package com.huahui.datasphere.portal.security.aes;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.huahui.datasphere.portal.ExceptionHandling.DataSphereException;


public class AES256
{
    private static final Logger LOGGER;
    private static final String SECRET_KEY;
    private static final String PADDING = "{";
    private static final int BLOCK_SIZE = 32;
    
    private static byte[] getSalt() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Preconditions.checkNotNull(AES256.SECRET_KEY, "Secret key cannot be null");
        final MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
        return mDigest.digest(AES256.SECRET_KEY.getBytes("UTF-8"));
    }
    
    private static String addPaddingToData(String data) {
        data += StringUtils.repeat("{", 32 - data.length() % 32);
        return data;
    }
    
    private static String removePaddingFromData(final String data) {
        Preconditions.checkNotNull("{", "Padding character for AES encryption cannot be null");
        return StringUtils.stripEnd(data, "{");
    }
    
    private static SecretKeySpec getSecretKey() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Preconditions.checkNotNull("{", "Padding character for AES encryption cannot be null");
        Preconditions.checkNotNull(32, "Block size for AES encryption cannot be null");
        return new SecretKeySpec(getSalt(), "AES");
    }
    
    public static byte[] encryptData(final String data) {
        try {
            Preconditions.checkNotNull(data, "Data to be encrypted cannot be null");
            AES256.LOGGER.info("Attempting to encrypt data using AES-256");
            final Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "SunJCE");
            final SecretKeySpec key = getSecretKey();
            cipher.init(1, key);
            return cipher.doFinal(addPaddingToData(data).getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException nsaEx) {
            AES256.LOGGER.error("Exception: No such algorithm");
            AES256.LOGGER.error(Throwables.getStackTraceAsString((Throwable)nsaEx));
            throw new DataSphereException("Error while encrypting data", nsaEx);
        }
        catch (NoSuchProviderException nsprEx) {
            AES256.LOGGER.error("Exception: No such provider");
            AES256.LOGGER.error(Throwables.getStackTraceAsString((Throwable)nsprEx));
            throw new DataSphereException("Error while encrypting data", nsprEx);
        }
        catch (NoSuchPaddingException nspaEx) {
            AES256.LOGGER.error("Exception: Padding error");
            AES256.LOGGER.error(Throwables.getStackTraceAsString((Throwable)nspaEx));
            throw new DataSphereException("Error while encrypting data", nspaEx);
        }
        catch (InvalidKeyException ikEx) {
            AES256.LOGGER.error("Exception: Invalid key error");
            AES256.LOGGER.error(Throwables.getStackTraceAsString((Throwable)ikEx));
            throw new DataSphereException("Error while encrypting data", ikEx);
        }
        catch (IllegalBlockSizeException ibEx) {
            AES256.LOGGER.error("Exception: Invalid block size error");
            AES256.LOGGER.error(Throwables.getStackTraceAsString((Throwable)ibEx));
            throw new DataSphereException("Error while encrypting data", ibEx);
        }
        catch (BadPaddingException bpEx) {
            AES256.LOGGER.error("Exception: Bad padding error");
            AES256.LOGGER.error(Throwables.getStackTraceAsString((Throwable)bpEx));
            throw new DataSphereException("Error while encrypting data", bpEx);
        }
        catch (UnsupportedEncodingException uEx) {
            AES256.LOGGER.error("Exception: Unsupported encoding error");
            AES256.LOGGER.error(Throwables.getStackTraceAsString((Throwable)uEx));
            throw new DataSphereException("Error while encrypting data", uEx);
        }
    }
    
    public static String decryptData(final byte[] cipherText) {
        try {
            Preconditions.checkNotNull(cipherText, "Data to be decrypted cannot be null");
            AES256.LOGGER.info("Attempting to decrypt data using AES-256");
	
          final Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "SunJCE");
            
            final SecretKeySpec key = getSecretKey();
            cipher.init(2, key);
            return removePaddingFromData(new String(cipher.doFinal(cipherText), "UTF-8"));
        }
        catch (NoSuchAlgorithmException nsaEx) {
            AES256.LOGGER.error("Exception: No such algorithm");
            AES256.LOGGER.error(Throwables.getStackTraceAsString((Throwable)nsaEx));
            throw new DataSphereException("Error while decrypting data", nsaEx);
        }
        catch (NoSuchProviderException nsprEx) {
            AES256.LOGGER.error("Exception: No such provider");
            AES256.LOGGER.error(Throwables.getStackTraceAsString((Throwable)nsprEx));
            throw new DataSphereException("Error while decrypting data", nsprEx);
        }
        catch (NoSuchPaddingException nspaEx) {
            AES256.LOGGER.error("Exception: Padding error");
            AES256.LOGGER.error(Throwables.getStackTraceAsString((Throwable)nspaEx));
            throw new DataSphereException("Error while decrypting data", nspaEx);
        }
        catch (InvalidKeyException ikEx) {
            AES256.LOGGER.error("Exception: Invalid key error");
            AES256.LOGGER.error(Throwables.getStackTraceAsString((Throwable)ikEx));
            throw new DataSphereException("Error while decrypting data", ikEx);
        }
        catch (IllegalBlockSizeException ibEx) {
            AES256.LOGGER.error("Exception: Invalid block size error");
            AES256.LOGGER.error(Throwables.getStackTraceAsString((Throwable)ibEx));
            throw new DataSphereException("Error while decrypting data", ibEx);
        }
        catch (BadPaddingException bpEx) {
            AES256.LOGGER.error("Exception: Bad padding error");
            AES256.LOGGER.error(Throwables.getStackTraceAsString((Throwable)bpEx));
            throw new DataSphereException("Error while decrypting data", bpEx);
        }
        catch (UnsupportedEncodingException uEx) {
            AES256.LOGGER.error("Exception: Unsupported encoding error");
            AES256.LOGGER.error(Throwables.getStackTraceAsString((Throwable)uEx));
            throw new DataSphereException("Error while decrypting data", uEx);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(AES256.class.getName());
        SECRET_KEY = EncryptionConstants.IW_SECRET_KEY;
    }
}
