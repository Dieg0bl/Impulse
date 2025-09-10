package com.impulse.lean.infrastructure.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * IMPULSE LEAN v1 - OpenAPI/Swagger Configuration
 * 
 * Complete API documentation with JWT authentication support
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:impulse-lean}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("IMPULSE LEAN v1 API")
                .description("IMPULSE LEAN v1 - Challenge/Validation Platform API\n\n" +
                    "## Features\n" +
                    "- 🏆 **Challenge Management**: Create, participate, and complete challenges\n" +
                    "- 📸 **Evidence Submission**: Upload and validate challenge evidence\n" +
                    "- 👥 **Peer Validation**: Community-driven validation system\n" +
                    "- 🎯 **Gamification**: Points, streaks, levels, and achievements\n" +
                    "- 🔐 **Security**: JWT RS256 + RBAC authentication\n" +
                    "- 📊 **Analytics**: Comprehensive user and challenge analytics\n\n" +
                    "## Authentication\n" +
                    "All protected endpoints require a valid JWT token in the Authorization header:\n" +
                    "```\nAuthorization: Bearer <your-jwt-token>\n```\n\n" +
                    "## Rate Limiting\n" +
                    "- Authentication endpoints: 5 requests per minute\n" +
                    "- API endpoints: 100 requests per minute\n\n" +
                    "## User Roles\n" +
                    "- **GUEST**: Limited read access\n" +
                    "- **USER**: Full participation in challenges\n" +
                    "- **VALIDATOR**: Can validate evidence + USER permissions\n" +
                    "- **MODERATOR**: Content moderation + VALIDATOR permissions\n" +
                    "- **ADMIN**: System administration + MODERATOR permissions\n" +
                    "- **SUPER_ADMIN**: Full system access")
                .version("1.0.0")
                .contact(new Contact()
                    .name("IMPULSE Team")
                    .email("dev@impulse-lean.com")
                    .url("https://impulse-lean.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(Arrays.asList(
                new Server().url("http://localhost:8080").description("Development Server"),
                new Server().url("https://api.impulse-lean.com").description("Production Server")))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token for API authentication")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
