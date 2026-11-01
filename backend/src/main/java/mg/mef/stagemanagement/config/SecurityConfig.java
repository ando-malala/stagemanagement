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

                        .requestMatchers(HttpMethod.GET, "/api/stagiaires/{id}/dossier", "/api/stage-participants/{id}/bilan.pdf")
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR", "STAGIAIRE")
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/pilotage", "/api/dashboard/priorites",
                                "/api/exports/participations.csv", "/api/exports/statistiques.csv", "/api/exports/alertes.csv")
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

                        .requestMatchers(HttpMethod.GET, "/api/portail/me").hasRole("STAGIAIRE")
                        .requestMatchers(HttpMethod.GET, "/api/stagiaires/{id}/compte").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/stagiaires/{id}/compte").hasRole("ADMIN")
                        // Personal reads reuse the existing APIs and resource policy.
                        .requestMatchers(HttpMethod.GET,
                                "/api/stagiaires", "/api/stagiaires/{id}", "/api/stagiaires/{id}/academiques",
                                "/api/stagiaires/{id}/fichiers", "/api/stagiaires/{id}/participations", "/api/stage-participants", "/api/stage-participants/{id}",
                                "/api/stage-participants/{id}/fichiers", "/api/stage-participants/{id}/documents-obligatoires",
                                "/api/stage-participants/{id}/historique-statuts", "/api/stage-participants/{id}/evenements",
                                "/api/stage-participants/{id}/periodes-rapports", "/api/stage-participants/{id}/actions",
                                "/api/stages", "/api/stages/{id}", "/api/stages/{id}/participants",
                                "/api/encadreurs", "/api/encadreurs/{id}", "/api/encadreurs/{id}/charge",
                                "/api/fichiers/{id}/telecharger", "/api/rapports-activite", "/api/rapports-activite/{id}",
                                "/api/rapports-activite/participation/{stageParticipantId}", "/api/rapports-activite/{id}/observations",
                                "/api/referentiels/suivi/types-documents", "/api/referentiels/suivi/statuts/{type}",
                                "/api/referentiels/suivi/transitions/{type}/{source}")
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR", "STAGIAIRE")
                        .requestMatchers(HttpMethod.POST, "/api/rapports-activite")
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR", "STAGIAIRE")
                        .requestMatchers(HttpMethod.PATCH, "/api/rapports-activite/{id}", "/api/rapports-activite/{id}/statut")
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR", "STAGIAIRE")

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
                                "/api/utilisateur-statut-historique/**"
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
                        
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/stagiaires",
                                "/api/stagiaires/{id}",
                                "/api/stagiaires/{id}/academiques"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/stagiaires",
                                "/api/stagiaires/{id}/academiques"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/stagiaires/{id}"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/encadreurs",
                                "/api/encadreurs/{id}"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/encadreurs"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/encadreurs/{id}",
                                "/api/encadreurs/{id}/statut"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/stages",
                                "/api/stages/{id}",
                                "/api/stages/{id}/participants",
                                "/api/stage-participants",
                                "/api/stage-participants/{id}"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/stages",
                                "/api/stage-participants"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/stages/{id}",
                                "/api/stage-participants/{id}"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE")
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/stage-participants/{id}/statut"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/stage-participants/{id}/prolonger"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/stage-participants/{id}/suspendre",
                                "/api/stage-participants/{id}/reprendre",
                                "/api/stage-participants/{id}/abandonner",
                                "/api/stage-participants/{id}/annuler",
                                "/api/stage-participants/{id}/cloturer"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/stagiaires/{id}/fichiers",
                                "/api/stage-participants/{id}/fichiers",
                                "/api/fichiers/{id}/telecharger"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/stagiaires/{id}/fichiers",
                                "/api/stage-participants/{id}/fichiers"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/rapports-activite",
                                "/api/rapports-activite/{id}",
                                "/api/rapports-activite/participation/{stageParticipantId}",
                                "/api/rapports-activite/{id}/observations"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/rapports-activite"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/rapports-activite/{id}"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/rapports-activite/{id}/observations"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/rapports-activite/{id}/statut"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/soutenances",
                                "/api/soutenances/{id}"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/soutenances"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/soutenances/{id}"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/evaluations",
                                "/api/evaluations/{id}",
                                "/api/evaluations/participation/{stageParticipantId}",
                                "/api/evaluations/criteres",
                                "/api/evaluations/{id}/notes"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/evaluations",
                                "/api/evaluations/{id}/notes",
                                "/api/evaluations/{id}/finaliser"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/evaluations/{id}"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/alertes",
                                "/api/alertes/non-lues"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/alertes"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/alertes/{id}/lire",
                                "/api/alertes/tout-lire"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/dashboard/resume"
                        )
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/journal-audit",
                                "/api/journal-audit/utilisateur/{utilisateurId}",
                                "/api/journal-audit/entite/{entite}/{entiteId}"
                        )
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/alertes/detection/stages-depasses"
                        )
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/alertes/detection/fins-stages-proches"
                        )
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/alertes/detection/rapports-manquants"
                        )
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/alertes/detection/test-orchestration"
                        )
                        .hasRole("ADMIN")
                        
                        .requestMatchers(HttpMethod.GET,
                                "/api/referentiels/suivi/types-documents",
                                "/api/referentiels/suivi/types-evenements",
                                "/api/referentiels/suivi/types-alertes",
                                "/api/referentiels/suivi/statuts/{type}",
                                "/api/referentiels/suivi/transitions/{type}/{source}",
                                "/api/stage-participants/{id}/documents-obligatoires",
                                "/api/stage-participants/{id}/historique-statuts",
                                "/api/stage-participants/{id}/evenements",
                                "/api/stage-participants/{id}/periodes-rapports",
                                "/api/stage-participants/{id}/actions",
                                "/api/encadreurs/{id}/charge")
                        .hasAnyRole("ADMIN", "RESPONSABLE_STAGE", "ENCADREUR")

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
