package com.huahui.datasphere.portal.ExceptionHandling;

public enum GenricException implements ErrorCode
{
    DATE_PARSE_EXCEPTION(501), 
    DECRYPT_PASSWORD_EXCEPTION(502);
    
    private final int number;
    
    private GenricException(final int number) {
        this.number = number;
    }
    
    @Override
    public int getNumber() {
        return this.number;
    }
}
