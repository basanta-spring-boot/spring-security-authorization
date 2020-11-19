package com.javatechie.spring.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info =
@Info(title = "User Auth", version = "2.0", description = "Group-Management Microservice")
)
public class SpringSecurityRoleBaseAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityRoleBaseAuthApplication.class, args);
    }

}
