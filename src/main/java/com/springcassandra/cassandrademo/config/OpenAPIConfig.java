package com.springcassandra.cassandrademo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI myOpenAPI() {

        Contact contact = new Contact();

        contact.setEmail("pradhumn1.porwal@paytmbank.com");
        contact.setName("Pradhumn Porwal");

        Info info = new Info()
                .title("Cassandra Employee Management APIs")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage employees.");

        return new OpenAPI().info(info);
    }
}
