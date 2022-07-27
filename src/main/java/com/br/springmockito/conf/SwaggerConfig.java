package com.br.springmockito.conf;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configurable
public class SwaggerConfig {
	
	@Bean
	public Docket api() {
		   
		return new Docket(DocumentationType.SWAGGER_2)
	               .select()
	               .apis(RequestHandlerSelectors.basePackage("com.br.springmockito.controller"))
	         //    .paths(PathSelectors.ant("contas/*")) passando a rota 
	               .paths(PathSelectors.any()) //qualquer rota
	               .build();
	}
}
