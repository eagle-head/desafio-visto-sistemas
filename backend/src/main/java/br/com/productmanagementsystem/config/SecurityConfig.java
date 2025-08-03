package br.com.productmanagementsystem.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;

/**
 * Configuration for security headers and CORS policy
 */
@Configuration
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (development and production)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",  // Angular dev server
            "http://localhost",       // Docker frontend
            "http://localhost:80"     // Docker frontend explicit port
        ));
        
        // Allow all standard HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Set max age for preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }

    @Bean
    public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilter() {
        FilterRegistrationBean<SecurityHeadersFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SecurityHeadersFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(2); // After CORS filter
        return registrationBean;
    }

    public static class SecurityHeadersFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, 
                           FilterChain chain) throws IOException, ServletException {
            
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            // Prevent clickjacking attacks
            httpResponse.setHeader("X-Frame-Options", "DENY");
            
            // Prevent MIME type sniffing
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            
            // Enable XSS filtering in browsers (legacy protection)
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            
            // Basic Content Security Policy for API
            httpResponse.setHeader("Content-Security-Policy", 
                "default-src 'self'; frame-ancestors 'none'");
            
            // Control referrer information
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            
            // Prevent caching of sensitive API responses
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setHeader("Expires", "0");
            
            chain.doFilter(request, response);
        }
    }
}