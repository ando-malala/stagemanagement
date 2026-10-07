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

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, error) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                        .accessDeniedHandler((request, response, error) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN)
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // AUTHENTIFICATION
                        // =========================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login"
                        )
                        .permitAll()

                        .requestMatchers("/error")
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/auth/me"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/logout"
                        )
                        .authenticated()

                        // =========================
                        // ROUTES SENSIBLES BLOQUÉES
                        // =========================

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
                        )
                        .denyAll()

                        // =========================
                        // UTILISATEURS — ADMIN
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/utilisateurs",
                                "/api/utilisateurs/{id}"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/utilisateurs"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/utilisateurs/{id}/statut",
                                "/api/utilisateurs/{id}/mot-de-passe"
                        )
                        .hasRole("ADMIN")

                        // =========================
                        // RÔLES DES UTILISATEURS — ADMIN
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/utilisateurs/{id}/roles"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/utilisateurs/{id}/roles"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/utilisateurs/{id}/roles/{roleCode}"
                        )
                        .hasRole("ADMIN")

                        // =========================
                        // DÉPARTEMENTS DES RESPONSABLES — ADMIN
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/utilisateurs/{id}/departements"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/utilisateurs/{id}/departements"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/utilisateurs/{id}/departements/{departementId}"
                        )
                        .hasRole("ADMIN")

                        // Tout autre accès aux utilisateurs est interdit.
                        .requestMatchers(
                                "/api/utilisateurs",
                                "/api/utilisateurs/**"
                        )
                        .denyAll()

                        // =========================
                        // RÔLES — ADMIN EN LECTURE
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/roles",
                                "/api/roles/{id}"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                "/api/roles",
                                "/api/roles/**"
                        )
                        .denyAll()

                        // =========================
                        // AUTRES ROUTES
                        // =========================
                        // Les autres CRUD générés restent fermés
                        // jusqu'à ce qu'on ajoute leurs DTO et règles métier.

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/referentiels/academiques/**"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/referentiels/academiques/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/referentiels/academiques/**"
                        )
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/referentiels/organisation/**"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/referentiels/organisation/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/referentiels/organisation/**"
                        )
                        .hasRole("ADMIN")
                        
                        .anyRequest()
                        .denyAll()
                )

                .addFilterBefore(
                        sessionAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}