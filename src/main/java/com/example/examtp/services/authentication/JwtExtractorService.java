package com.example.examtp.services.authentication;

import com.example.examtp.exceptions.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Map;
import java.util.Collections;

@Service
public class JwtExtractorService {

    private Optional<Jwt> extractJwt(Authentication authentication) {
        if (authentication == null) {
            return Optional.empty();
        }

        try {
            if (authentication.getPrincipal() instanceof Jwt jwt) {
                return Optional.of(jwt);
            }

            if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                return Optional.of(jwtAuth.getToken());
            }

        } catch (Exception e) {
            throw new AppException("Failed to extract JWT from authentication : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Optional.empty();
    }

    private <T> Optional<T> getClaim(Authentication authentication, String claimName, Class<T> type) {
        return extractJwt(authentication)
                .map(jwt -> {
                    try {
                        return jwt.getClaim(claimName);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(value -> {
                    try {
                        if (type.isInstance(value)) {
                            return type.cast(value);
                        }
                    } catch (Exception e) {
                        throw new AppException("Failed to extract JWT from authentication : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    return null;
                });
    }

    public String getUsername(Authentication authentication) {
        return extractJwt(authentication)
                .map(Jwt::getSubject)
                .orElse(null);
    }

    public String getEmail(Authentication authentication) {
        return getClaim(authentication, "email", String.class)
                .orElse(null);
    }

    public String getPreferredUsername(Authentication authentication) {
        return getClaim(authentication, "preferred_username", String.class)
                .orElse(null);
    }

    public Map<String, Object> getAllClaims(Authentication authentication) {
        return extractJwt(authentication)
                .map(Jwt::getClaims)
                .orElse(Collections.emptyMap());
    }
}