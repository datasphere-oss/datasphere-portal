package com.huahui.datasphere.portal.security;

import org.springframework.security.web.*;
import org.springframework.stereotype.*;
import javax.servlet.http.*;
import org.springframework.security.core.*;
import java.io.*;
import javax.servlet.*;

@Component
public class IWBasicAuthenticationEntryPoint implements AuthenticationEntryPoint
{
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException, ServletException {
        response.sendError(401, "Unauthorized");
    }
}
