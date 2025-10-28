package com.example.examtp.configuration;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import io.swagger.v3.oas.models.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.annotation.Secured;
import jakarta.annotation.security.RolesAllowed;
import jakarta.annotation.security.PermitAll;

import java.lang.annotation.Annotation;

@Configuration
public class OpenApiConfig {

    private static final String SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            if (requiresAuth(handlerMethod)) {
                operation.addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME));
            }
            return operation;
        };
    }

    private boolean requiresAuth(HandlerMethod handler) {
        // Explicitly permit-all -> no lock
        if (hasAnnotation(handler, PermitAll.class)) {
            return false;
        }

        // If any security annotations are present -> require auth (add lock)
        if (hasAnnotation(handler, PreAuthorize.class)
                || hasAnnotation(handler, Secured.class)
                || hasAnnotation(handler, RolesAllowed.class)) {
            return true;
        }

        // Default: do not add lock automatically. Change to 'true' if you want all endpoints locked.
        return false;
    }

    private boolean hasAnnotation(HandlerMethod handler, Class<? extends Annotation> annotation) {
        return handler.hasMethodAnnotation(annotation) || handler.getBeanType().isAnnotationPresent(annotation);
    }
}

