package com.huahui.datasphere.portal.configuration;

import org.springframework.web.servlet.config.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.support.*;
import org.springframework.context.annotation.*;
import org.springframework.boot.web.servlet.error.ErrorAttributes;

@Configuration
@EnableWebMvc
public class PortalWebMvcConfigurer extends WebMvcConfigurerAdapter
{
    @Value("${server.contextPath}")
    private String serverContextPath;
    
    @Bean(name = { "messageSource" })
    public ReloadableResourceBundleMessageSource getMessageSource() {
        final ReloadableResourceBundleMessageSource resource = new ReloadableResourceBundleMessageSource();
        resource.setBasename("classpath:locale/messages");
        resource.setDefaultEncoding("UTF-8");
        return resource;
    }
    
    @Bean
    public ErrorAttributes errorAttributes() {
        return (ErrorAttributes)new PortalWebMvcConfigurer();
    }
}
