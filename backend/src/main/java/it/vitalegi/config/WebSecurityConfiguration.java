package it.vitalegi.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Log4j2
public class WebSecurityConfiguration {

    @Value("${security.cors.allowedOrigins}")
    List<String> allowedOrigins;
    @Value("${security.cors.allowedMethods}")
    List<String> allowedMethods;
    @Value("${security.cors.allowCredentials}")
    Boolean allowCredentials;
    @Value("${security.cors.allowedHeaders}")
    List<String> allowedHeaders;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((requests) -> requests //
                                                                 .requestMatchers("/v3/api-docs", "/v3/api-docs/**",
                                                                         "/v3/api-docs.yaml", "/swagger-ui/*")
                                                                 .permitAll() //
                                                                 .anyRequest().authenticated() //

                   )//
                   .cors(withDefaults()).oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults())).build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        log.info("CORS configuration");
        log.info("allowedOrigins={}", allowedOrigins);
        log.info("allowedMethods={}", allowedMethods);
        log.info("allowCredentials={}", allowCredentials);
        log.info("allowedHeaders={}", allowedHeaders);
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowCredentials(allowCredentials);
        configuration.setAllowedHeaders(allowedHeaders);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
