package com.hogiabao7725.hotelbooking.security.config;

import com.hogiabao7725.hotelbooking.security.handler.CustomAccessDeniedHandler;
import com.hogiabao7725.hotelbooking.security.handler.CustomAuthenticationEntrypoint;
import com.hogiabao7725.hotelbooking.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntrypoint authenticationEntrypoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntrypoint)
                        .accessDeniedHandler(accessDeniedHandler))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityEndpoints.PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

}