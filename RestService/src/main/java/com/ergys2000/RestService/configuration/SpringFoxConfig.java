package com.ergys2000.RestService.configuration;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.ergys2000")).paths(PathSelectors.any()).build()
				.apiInfo(apiDetails());
	}

	/**
	 * Defines the ApiInfo configuration for the Docket object
	 * @returns the api details object
	 */
	private ApiInfo apiDetails() {
		return new ApiInfo("Lufthansa Test Project API", "This is the rest service for the lufthansa test application",
				"1.0", "Free to use",
				new springfox.documentation.service.Contact("Ergys Rrjolli", "https://github.com/Ergys2000",
						"rrjolligys@gmail.com"),
				"API License", "https://github.com/Ergys2000", Collections.emptyList());
	}
}
