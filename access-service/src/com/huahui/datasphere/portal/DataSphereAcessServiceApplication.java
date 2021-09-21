package com.huahui.datasphere.portal;

import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.*;
import org.springframework.boot.*;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class DataSphereAcessServiceApplication
{
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(new Class[] { DataSphereAcessServiceApplication.class });
    }
    
    public static void main(final String[] args) {
        SpringApplication.run(DataSphereAcessServiceApplication.class, args);
    }
}
