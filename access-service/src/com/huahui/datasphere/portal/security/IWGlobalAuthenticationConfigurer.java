package com.huahui.datasphere.portal.security;

import org.springframework.security.config.annotation.authentication.configurers.*;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.*;

@Configuration
public class IWGlobalAuthenticationConfigurer extends GlobalAuthenticationConfigurerAdapter
{
    @Autowired
    IWAuthenticationProvider iwAuthenticationProvider;
    
    public void init(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider((AuthenticationProvider)this.iwAuthenticationProvider);
    }
}
