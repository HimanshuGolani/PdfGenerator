package com.pdfgen.pdfgen.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pdfGenOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PDF Generation API")
                        .description("Spring Boot REST API for Dynamic Invoice PDF Generation")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Your Name")
                                .email("your.email@example.com")
                                .url("https://your-portfolio.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project Wiki Documentation")
                        .url("https://github.com/your-repo/pdfgen-docs"));
    }
}
