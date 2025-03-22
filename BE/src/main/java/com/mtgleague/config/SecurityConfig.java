package com.mtgleague.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    private static final String[] WHITE_LIST_URL = {
            "/.well-known/pki-validation/DA9BB67B67D4A1C592713663F2ACCAD3.txt",
            "/auth/**",
            "/ranking",
            "/leagues",
            "/leagues/new",
            "/players/profile",
            "/forgotPassword/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(new RegexRequestMatcher("/leagues/[0-9]+", null)).permitAll()
                        .requestMatchers(new RegexRequestMatcher("/leagues/[0-9]+/ranking", null)).permitAll()
                        .requestMatchers(new RegexRequestMatcher("/leagues/[0-9]+/events", null)).permitAll()
                        .requestMatchers(new RegexRequestMatcher("/leagues/[0-9]+/members", null)).permitAll()
                        .requestMatchers(new RegexRequestMatcher("/events/[0-9]+", null)).permitAll()
                        .requestMatchers(new RegexRequestMatcher("/events/[0-9]+/ranking", null)).permitAll()
                        .requestMatchers(WHITE_LIST_URL).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout((logout) -> logout.permitAll());

        return http.build();
    }
}
