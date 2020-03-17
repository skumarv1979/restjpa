package com.omrtb.restjpa.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
//@WebFilter("/api/authops/*")
public class AddResponseHeaderFilter implements Filter {

	@Value("${strava.client_id}")
	private String clientId;
	
	private static Logger LOGGER = LogManager.getLogger(AddResponseHeaderFilter.class);
	
	public AddResponseHeaderFilter() {
		
	}
	public AddResponseHeaderFilter(String clientId) {
		this.clientId = clientId;
	}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
      FilterChain chain) throws IOException, ServletException {
    	LOGGER.debug("Is filter called :: "+clientId);
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader(
          "clientId", clientId);
        chain.doFilter(request, response);
    }
 
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // ...
    }
 
    @Override
    public void destroy() {
        // ...
    }
}