package com.huahui.datasphere.portal.security.aes;


public class EncryptionConstants
{
    public static final String IW_SECRET_KEY;
    public static final String PADDING_CHARACTER = "{";
    public static final int BLOCK_SIZE = 32;
    
    static {
        IW_SECRET_KEY = "datasphere";
    }
}
