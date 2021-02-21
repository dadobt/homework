package com.dadobt.homework.integartiontests.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-integration.properties")
@Getter
@Setter
@ToString
public class IntegrationTestConfigurationProperties {

    @Value("${spring.datasource.username}")
    public String username;

    @Value("${spring.datasource.password}")
    public String password;

    @Value("${spring.datasource.url}")
    public String url;

    @Value("${spring.datasource.driver-class-name}")
    public String driver;
}
