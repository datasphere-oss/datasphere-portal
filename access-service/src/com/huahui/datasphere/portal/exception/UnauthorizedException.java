package com.huahui.datasphere.portal.exception;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Unauthorized")
public class UnauthorizedException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
}
