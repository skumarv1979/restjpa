package com.omrtb.restjpa.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.google.common.collect.ImmutableList;

@Configuration
@EnableWebSecurity
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {
 
	@Value("#{'${allowed.origins}'.split(',')}")
	private List<String> rawOrigins;

	@Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private MySavedRequestAwareAuthenticationSuccessHandler mySuccessHandler;

    @Autowired
    public UserDetailsService userDetailsService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }
    
    private SimpleUrlAuthenticationFailureHandler myFailureHandler = new SimpleUrlAuthenticationFailureHandler();
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	    /*auth.inMemoryAuthentication()
	        .withUser("admin").password(encoder().encode("adminPass")).roles("ADMIN")
	        .and()
	        .withUser("user").password(encoder().encode("userPass")).roles("USER");*/
	    auth.authenticationProvider(authenticationProvider());
	}
	@Bean
	public PasswordEncoder  encoder() {
	    return new BCryptPasswordEncoder() {
	    	@Override
	    	public String encode(CharSequence rawPassword) {
	    		// TODO Auto-generated method stub
	    		String tmp = super.encode(rawPassword);
	    		return tmp;
	    	}
	    };
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception { 
	    http.httpBasic().and()
	    .cors().and()
	    .csrf().disable()
	    .exceptionHandling()
	    .authenticationEntryPoint(restAuthenticationEntryPoint)
	    .and()
	    .authorizeRequests()
	    .antMatchers("/").permitAll()
	    .antMatchers("/login").permitAll()
	    .antMatchers("/api/unauthops/**").permitAll()
	    .antMatchers("/favicon.ico").permitAll()
	    //.antMatchers("/api/events/**").permitAll()
	    .antMatchers("/api/authops/events/create").permitAll()
	    //.antMatchers("/api/user/**").authenticated()
	    .antMatchers("/api/authops/admin/**").hasRole("ADMIN")
	    .anyRequest().authenticated()// /api/events
	    .and()
	    .formLogin()
	    .successHandler(mySuccessHandler)
	    .failureHandler(myFailureHandler)
	    .and()
	    .logout();
	}
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
   
        configuration.setAllowedOrigins(rawOrigins);
        configuration.setAllowedMethods(ImmutableList.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type",
        		"Access-Control-Allow-Headers", "Origin","Accept", "X-Requested-With", "Access-Control-Request-Method", 
        		"Access-Control-Request-Headers"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}