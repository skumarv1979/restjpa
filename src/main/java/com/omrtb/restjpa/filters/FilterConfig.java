package com.omrtb.restjpa.filters;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<AddResponseHeaderFilter> registerFilter(AddResponseHeaderFilter filter) { // Inject it
        FilterRegistrationBean<AddResponseHeaderFilter> registration = new FilterRegistrationBean<AddResponseHeaderFilter>();
        registration.setFilter(filter); // Use it
        registration.addUrlPatterns("/api/authops/*");
        return registration;
   }
}