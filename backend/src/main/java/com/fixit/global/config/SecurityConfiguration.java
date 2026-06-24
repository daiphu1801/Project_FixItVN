package com.fixit.global.config;

import com.fixit.global.security.CustomUserDetailsService;
import com.fixit.global.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    private static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/api/v1/services/**",
            "/api/v1/webhooks/**"

            // // DEV ONLY: cho phép test assignment bằng X-Debug-Worker-Id
            // "/api/v1/workers/me/home",
            // "/api/v1/workers/me/status",
            // "/api/v1/workers/me/location",
            // "/api/v1/workers/me/schedule",
            //
            // "/api/v1/workers/me/assignments/pending",
            // "/api/v1/bookings/*/assignments/*/accept",
            // "/api/v1/bookings/*/assignments/*/reject",
            // "/api/v1/bookings/*/assignments/*/miss",
            //
            // "/api/v1/bookings/*/start-moving",
            // "/api/v1/bookings/*/arrive",
            // "/api/v1/bookings/*/start-survey",
            // "/api/v1/bookings/*/start-repair",
            //
            // "/api/v1/workers/me/profile",
            // "/api/v1/workers/me/skills",
            //
            // "/api/v1/bookings/*/worker-complete",
            // "/api/v1/workers/me/history",
            // "/api/v1/workers/me/stats",
            //
            // "/api/v1/workers/me/wallet",
            // "/api/v1/workers/me/wallet/transactions",
            // "/api/v1/workers/me/wallet/deposits",
            // "/api/v1/workers/me/wallet/deposits/*",
            // "/api/v1/workers/me/wallet/deposits/*/qr",
            //
            // "/api/v1/workers/me/bank-accounts",
            // "/api/v1/workers/me/bank-accounts/*",
            // "/api/v1/workers/me/bank-accounts/*/default"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/change-password").authenticated()
                        .requestMatchers(WHITE_LIST_URL).permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
