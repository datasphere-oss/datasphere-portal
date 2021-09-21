package com.huahui.datasphere.portal.security;

import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.*;

@EnableWebSecurity
@Configuration
class IWWebSecurityConfigurer extends WebSecurityConfigurerAdapter
{
    protected void configure(final HttpSecurity http) throws Exception {
        ((HttpSecurity)((HttpSecurity)((HttpSecurity)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)http.authorizeRequests().antMatchers(new String[] { "/api/ping" })).permitAll().antMatchers(new String[] { "/api/keys/**" })).hasRole("ADMIN").antMatchers(new String[] { "/**" })).authenticated().and()).httpBasic().and()).sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()).csrf().disable();
    }
}
