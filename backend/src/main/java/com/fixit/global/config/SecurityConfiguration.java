//package com.fixit.global.config;
//
//import com.fixit.global.security.JwtAuthenticationFilter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//@EnableMethodSecurity
//public class SecurityConfiguration {
//
//    private final JwtAuthenticationFilter jwtAuthFilter;
//    private final AuthenticationProvider authenticationProvider;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/api/v1/auth/**",
//                                "/api/v1/services/**",
//                                "/api/v1/workers/*/profile",
//                                "/v3/api-docs/**",
//                                "/swagger-ui/**",
//                                "/swagger-ui.html"
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .authenticationProvider(authenticationProvider)
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}

package com.fixit.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // DEV ONLY: cho phép test assignment bằng X-Debug-Worker-Id
                        .requestMatchers(
                                "/api/v1/workers/me/home",
                                "/api/v1/workers/me/status",
                                "/api/v1/workers/me/location",
                                "/api/v1/workers/me/schedule",

                                "/api/v1/workers/me/assignments/pending",
                                "/api/v1/bookings/*/assignments/*/accept",
                                "/api/v1/bookings/*/assignments/*/reject",
                                "/api/v1/bookings/*/assignments/*/miss",

                                "/api/v1/bookings/*/start-moving",
                                "/api/v1/bookings/*/arrive",
                                "/api/v1/bookings/*/start-survey",
                                "/api/v1/bookings/*/start-repair",

                                "/api/v1/workers/me/profile",
                                "/api/v1/workers/me/skills",

                                "/api/v1/bookings/*/worker-complete",
                                "/api/v1/workers/me/history",
                                "/api/v1/workers/me/stats",

                                "/api/v1/workers/me/wallet",
                                "/api/v1/workers/me/wallet/transactions",
                                "/api/v1/workers/me/wallet/deposits",
                                "/api/v1/workers/me/wallet/deposits/*",
                                "/api/v1/workers/me/wallet/deposits/*/qr",

                                "/api/v1/workers/me/bank-accounts",
                                "/api/v1/workers/me/bank-accounts/*",
                                "/api/v1/workers/me/bank-accounts/*/default"
                        ).permitAll()

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}