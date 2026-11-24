package com.revenueos;

import com.revenueos.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class RevenueOsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RevenueOsApplication.class, args);
    }
}
