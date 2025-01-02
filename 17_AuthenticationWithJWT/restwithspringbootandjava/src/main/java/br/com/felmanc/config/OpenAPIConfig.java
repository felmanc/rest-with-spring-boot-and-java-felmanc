package br.com.felmanc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenAPIConfig {
	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Restful with Java 23 and Spring Boot 3")
						.version("v1")
						.description("Some description about this API")
						.termsOfService("htts://")
						.license(
								new License()
								.name("Apache 2.0")
								.url("htts://"))
						);
	}
}
