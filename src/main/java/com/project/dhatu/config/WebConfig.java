package com.project.dhatu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer viewControllerConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("forward:/index.html");
                registry.addViewController("/home").setViewName("forward:/index.html");
                registry.addViewController("/chat").setViewName("forward:/chat.html");
                registry.addViewController("/admin").setViewName("forward:/admin.html");
            }
        };
    }
}
