package com.finverse.securityapp.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class SpringFoxConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())//metadata
                .securityContexts(Arrays.asList(securityContext())) //configures security model (jwt)
                .securitySchemes(Arrays.asList(apiKey())) //declares type of auth (jwt)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.finverse.securityapp")) //tells swagger which controllers to scan
                .paths(PathSelectors.any()) //applied to all url paths in the app
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Authentication Service API",
                "Authentication Service API description.",
                "1.0",
                "Terms of service",
                new Contact("Ganeev Kaur", "www.wolverinesolution.ca", "wolverine3r44@gmail.com"),
                "License of API",
                "API license URL",
                Collections.emptyList());
    }

//    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    } //Sets up a SecurityContext to apply JWT security globally

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    } //Now all requests in Swagger UI require the JWT token, which is entered once and applied to all endpoints
}
