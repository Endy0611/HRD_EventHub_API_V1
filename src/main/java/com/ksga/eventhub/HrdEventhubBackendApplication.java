package com.ksga.eventhub;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(
        info = @Info(
                title = "HRD EventHub API V1",
                version = "V1",
                description = """
                        HRD EventHub is a backend service for managing alumni, students,
                        and organizational events across generations and workspaces.
                        """
        ),
        servers = {
                @Server(url = "http://localhost:9090", description = "Local Development Server"),
                @Server(url = "https://api.yourdomain.com", description = "Production Server")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class HrdEventhubBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(HrdEventhubBackendApplication.class, args);
    }
}