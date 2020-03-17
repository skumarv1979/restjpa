package com.omrtb.restjpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

//@Configuration
//@EnableWebMvc
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Value("#{'${allowed.origins}'.split(',')}")
	private List<String> rawOrigins;

	public String[] getOrigin() {
	    int size = rawOrigins.size();
	    String[] originArray = new String[size];
	    return rawOrigins.toArray(originArray);
	}

	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof org.springframework.http.converter.json.MappingJackson2HttpMessageConverter) {
                ObjectMapper mapper = ((MappingJackson2HttpMessageConverter) converter).getObjectMapper();
                mapper.registerModule(new Hibernate5Module());
                // replace Hibernate4Module() with the proper class for your hibernate version.
            }
        }
    }
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		/*registry.addMapping("/**")
			//.allowedOrigins(getOrigin())
			.allowedMethods("PUT", "DELETE", "GET", "POST", "HEAD", "OPTIONS")
			//	.allowedHeaders("header1", "header2", "header3")
			//.exposedHeaders("header1", "header2")
			//.allowCredentials(false)
			.maxAge(3600);*/
        registry.addMapping("/**")
        .allowedOrigins(getOrigin())
        	.allowedHeaders("*")
        	.allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
        	.maxAge(4800)   // add maxAge
        	.allowCredentials(false)
        ;

	}
	  /*@Override
	  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	    configurer.favorPathExtension(false);
	  }*/
}