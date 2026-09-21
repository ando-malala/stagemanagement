package mg.mef.stagemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import mg.mef.stagemanagement.security.SessionAuthenticationFilter;

@Configuration
@EnableMethodSecurity
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

                                // Authentification
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/auth/login"
                                ).permitAll()

                                .requestMatchers("/error").permitAll()

                                // Bloquer les anciens CRUD génériques des données sensibles
                                .requestMatchers(
                                        "/api/sessions-utilisateur",
                                        "/api/sessions-utilisateur/**",
                                        "/api/utilisateurs-role",
                                        "/api/utilisateurs-role/**",
                                        "/api/historique-connexions",
                                        "/api/historique-connexions/**",
                                        "/api/utilisateur-statut-historique",
                                        "/api/utilisateur-statut-historique/**",
                                        "/api/journal-audit",
                                        "/api/journal-audit/**"
                                ).denyAll()

                                // Consulter les utilisateurs
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/utilisateurs",
                                        "/api/utilisateurs/**"
                                ).hasRole("ADMIN")

                                // Créer un utilisateur
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/utilisateurs"
                                ).hasRole("ADMIN")

                                // Interdire les autres opérations sur les utilisateurs
                                .requestMatchers(
                                        "/api/utilisateurs",
                                        "/api/utilisateurs/**"
                                ).denyAll()

                                // Référentiel des rôles : consultation ADMIN uniquement
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/roles",
                                        "/api/roles/**"
                                ).hasRole("ADMIN")

                                .requestMatchers(
                                        "/api/roles",
                                        "/api/roles/**"
                                ).denyAll()

                                // Protection temporaire des autres API
                                .anyRequest().authenticated()
                        )

                        .addFilterBefore(
                                sessionAuthenticationFilter,
                                UsernamePasswordAuthenticationFilter.class
                        );

                return http.build();
        }
}