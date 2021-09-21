package com.huahui.datasphere.portal.exception;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerError extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public InternalServerError() {
    }
    
    public InternalServerError(final String msg) {
        super(msg);
    }
}
