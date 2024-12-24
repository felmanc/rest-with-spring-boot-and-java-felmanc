package br.com.felmanc.config;



import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration: Indica ao SpringBoot ao carregar a aplicação que deve ler esta classe que contém o comportamento da aplicação
@Configuration
public class WebConfig implements WebMvcConfigurer{

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		// https://www.baeldung.com/spring-mvc-content-negotiation-json-xml
		// Via EXTENSION. http://localhost:8080/api/person/v1.xml DEPRECATED on SpringBoot 2.6
		
		WebMvcConfigurer.super.configureContentNegotiation(
		// Via QUERY PARAM. http://localhost:8080/api/person/v1?mediaType=xml
				
				// favorParameter(true): aceita parâmetros
				configurer.favorParameter(true).parameterName("mediaType").ignoreAcceptHeader(true).
				useRegisteredExtensionsOnly(false)
				.defaultContentType(MediaType.APPLICATION_JSON)
					.mediaType("json", MediaType.APPLICATION_JSON)
					.mediaType("xml", MediaType.APPLICATION_XML));
	}

}
