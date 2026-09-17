package mg.mef.stagemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

import mg.mef.stagemanagement.security.SessionAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SessionAuthenticationFilter sessionAuthenticationFilter
    ) throws Exception {

        http.csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(
                                        (request, response, authException) ->
                                                response.sendError(
                                                        HttpServletResponse.SC_UNAUTHORIZED
                                                )
                                )
                                .accessDeniedHandler(
                                        (request, response, accessDeniedException) ->
                                                response.sendError(
                                                        HttpServletResponse.SC_FORBIDDEN
                                                )
                                )
                )

                .authorizeHttpRequests(auth -> auth

                        // Connexion accessible sans token
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login"
                        ).permitAll()

                        .requestMatchers("/error").permitAll()

                        // Tout le reste nécessite une connexion
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        sessionAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}