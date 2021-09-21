package com.huahui.datasphere.portal.exception;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource Not Found")
public class ResourceNotFoundException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
}
