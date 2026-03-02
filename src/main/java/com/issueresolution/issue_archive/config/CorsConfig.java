package com.issueresolution.issue_archive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Allow React frontend (default React dev server runs on port 3000)
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));

        // Allow all HTTP methods
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // ← FIXED: Changed from setAllowedMethods to setAllowedHeaders
        corsConfiguration.setAllowedHeaders(List.of("*"));

        // Allow credentials (for Basic Auth)
        corsConfiguration.setAllowCredentials(true);

        // Expose Authorization header so frontend can read it
        corsConfiguration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}