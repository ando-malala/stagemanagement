package mg.mef.stagemanagement;

import mg.mef.stagemanagement.service.AlerteOrchestrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "MEF_TEST_DB_URL", matches = "jdbc:postgresql://(localhost|127\\.0\\.0\\.1):[0-9]+/mef_integration_[a-f0-9]{32}")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "server.address=127.0.0.1", "spring.jpa.hibernate.ddl-auto=validate", "logging.level.root=ERROR",
        "logging.level.org.springframework=ERROR", "logging.level.mg.mef=ERROR", "spring.jpa.show-sql=false"
})
class CompleteIntegrationTests {
    @DynamicPropertySource static void properties(DynamicPropertyRegistry registry) {
        String url = System.getenv("MEF_TEST_DB_URL");
        if (url == null || !url.matches("jdbc:postgresql://(localhost|127\\.0\\.0\\.1):[0-9]+/mef_integration_[a-f0-9]{32}"))
            throw new IllegalStateException("A dedicated test database is required");
        registry.add("spring.datasource.url", () -> url);
        registry.add("spring.datasource.username", () -> System.getenv("MEF_TEST_DB_USER"));
        registry.add("spring.datasource.password", () -> System.getenv("MEF_TEST_DB_PASSWORD"));
        registry.add("app.upload.dir", () -> "target/integration-uploads/" + url.substring(url.lastIndexOf('/') + 1));
    }
    @Value("${local.server.port}") int port;
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder encoder;
    @MockitoBean AlerteOrchestrationService schedulerOnly;
    final JsonMapper json = JsonMapper.builder().build();
    final HttpClient http = HttpClient.newHttpClient();
    String token;
    static final String PASSWORD = "integration-password-only";

    @Test void fullWorkflowAgainstIsolatedPostgres() throws Exception {
        // Only the initial administrator is bootstrapped through SQL, in the guarded empty test database.
        jdbc.update("insert into utilisateurs(identifiant,mot_de_passe_hash,statut_id) select ?,?,s.id from statuts s join types_statut t on t.id=s.type_statut_id where t.code='UTILISATEUR' and s.code='ACTIF'", "integration-admin", encoder.encode(PASSWORD));
        jdbc.update("insert into utilisateurs_role(utilisateur_id,role_id) select u.id,r.id from utilisateurs u cross join roles r where u.identifiant='integration-admin' and r.code='ADMIN'");
        request("GET", "/referentiels/suivi/types-documents", null, 401);
        token = login("integration-admin");
        assertEquals(5, get("/referentiels/suivi/types-documents").size());
        assertEquals(6, get("/referentiels/suivi/types-evenements").size());
        assertTrue(get("/referentiels/suivi/types-alertes").size() > 0);
        request("GET", "/referentiels/suivi/statuts/INCONNU", null, 404);
        request("GET", "/stage-participants/99999/evenements", null, 404);
        int department = create("/referentiels/organisation/departements", Map.of("libelle", "Integration department"));
        int structure = create("/referentiels/organisation/structures-accueil", Map.of("libelle", "Integration structure", "departementId", department, "actif", true));
        int cadre = create("/referentiels/organisation/cadres-stage", Map.of("themeStage", "Integration theme", "actif", true));
        int school = create("/referentiels/academiques/etablissements", Map.of("libelle", "Integration school", "actif", true));
        int stream = create("/referentiels/academiques/filieres", Map.of("libelle", "Integration stream", "actif", true));
        int level = create("/referentiels/academiques/niveaux-etude", Map.of("libelle", "Integration level", "actif", true));
        int month = create("/referentiels/academiques/mois-stage", Map.of("libelle", "Integration month", "actif", true));
        request("POST", "/stagiaires", Map.of("nom", "Integration", "prenoms", "Trainee"), 400);
        int trainee = create("/stagiaires", Map.of("nom", "Integration", "prenoms", "Trainee", "adresse", "Integration address"));
        int academic = create("/stagiaires/" + trainee + "/academiques", Map.of("niveauEtudeId", level, "filiereId", stream, "etablissementId", school, "anneeEtudes", "2026-2027"));
        int mentor = create("/encadreurs", Map.of("nom", "Integration", "prenoms", "Mentor", "departementId", department, "capaciteMaxStagiaires", 1));
        int stage = create("/stages", Map.of("libelle", "Integration stage", "structureAccueilId", structure));
        LocalDate today = LocalDate.now(ZoneId.of("Indian/Antananarivo"));
        var participationBody = Map.of("stageId", stage, "stagiaireAcademiqueId", academic, "cadreStageId", cadre, "encadreurId", mentor,
                "dateDebutStage", today.minusDays(21).toString(), "dateFinStage", today.plusDays(7).toString());
        int participation = create("/stage-participants", participationBody);
        String base = "/stage-participants/" + participation;
        request("POST", base + "/cloturer", Map.of(), 409);
        request("PATCH", base + "/statut", Map.of("statutCode", "EN_COURS"), 200);
        assertEquals(1, get("/encadreurs/" + mentor + "/charge").get("participationsActives").asInt());
        assertEquals(1, get("/encadreurs/" + mentor + "/charge").get("capaciteMax").asInt());
        request("PATCH", base + "/statut", Map.of("statutCode", "SUSPENDUE"), 409);
        request("POST", base + "/suspendre", Map.of("dateEffet", today.minusDays(3).toString()), 200);
        request("POST", base + "/reprendre", Map.of("dateEffet", today.minusDays(4).toString()), 409);
        request("POST", base + "/reprendre", Map.of("dateEffet", today.minusDays(2).toString()), 200);
        request("POST", base + "/prolonger", Map.of("nouvelleDateFin", today.plusDays(14).toString()), 200);
        assertEquals(3, get(base + "/evenements").size());
        JsonNode period = get(base + "/periodes-rapports").get(0);
        assertTrue(period.get("manquante").asBoolean());
        int report = create("/rapports-activite", Map.of("stageParticipantId", participation, "moisStageId", month,
                "debutSemaine", period.get("debut").asString(), "finSemaine", period.get("fin").asString(), "activite", "Integration work"));
        JsonNode conflict = request("POST", "/rapports-activite", Map.of("stageParticipantId", participation, "moisStageId", month,
                "debutSemaine", period.get("debut").asString(), "finSemaine", LocalDate.parse(period.get("fin").asString()).plusDays(1).toString()), 409);
        assertFalse(conflict.toString().contains("SQL"));
        request("PATCH", "/rapports-activite/" + report + "/statut", Map.of("statutCode", "SOUMIS"), 200);
        request("PATCH", "/rapports-activite/" + report + "/statut", Map.of("statutCode", "A_CORRIGER"), 200);
        request("POST", "/rapports-activite/" + report + "/observations", Map.of("typeObservation", "DEMANDE_CORRECTION", "commentaire", "Integration correction", "visibleStagiaire", true), 201);
        request("PATCH", "/rapports-activite/" + report + "/statut", Map.of("statutCode", "SOUMIS"), 200);
        request("PATCH", "/rapports-activite/" + report + "/statut", Map.of("statutCode", "VALIDE"), 200);
        assertTrue(get(base + "/periodes-rapports").get(0).get("validee").asBoolean());
        int evaluation = create("/evaluations", Map.of("stageParticipantId", participation, "encadreurId", mentor));
        request("POST", "/evaluations/" + evaluation + "/finaliser", Map.of(), 409);
        request("POST", "/evaluations/" + evaluation + "/notes", Map.of("critereId", get("/evaluations/criteres").get(0).get("id").asInt(), "note", 15), 400);
        for (JsonNode criterion : get("/evaluations/criteres"))
            request("POST", "/evaluations/" + evaluation + "/notes", Map.of("critereId", criterion.get("id").asInt(), "note", 5), 201);
        assertTrue(request("POST", "/evaluations/" + evaluation + "/finaliser", Map.of(), 200).get("estFinalisee").asBoolean());
        request("POST", "/evaluations/" + evaluation + "/notes", Map.of("critereId", get("/evaluations/criteres").get(0).get("id").asInt(), "note", 4), 409);
        create("/soutenances", Map.of("stageParticipantId", participation, "dateSoutenance", today.atTime(14, 0).toString(), "lieu", "Integration room"));
        int alert = create("/alertes", Map.of("typeAlerteCode", "DOSSIER_INCOMPLET", "stageParticipantId", participation, "message", "Integration alert", "priorite", "AVERTISSEMENT"));
        request("PATCH", "/alertes/" + alert + "/lire", Map.of(), 200);
        request("PATCH", base + "/statut", Map.of("statutCode", "A_CLOTURER"), 200);
        request("PATCH", base + "/statut", Map.of("statutCode", "CLOTUREE"), 409);
        assertEquals(3, get(base + "/evenements").size());
        for (JsonNode doc : get(base + "/documents-obligatoires")) {
            assertFalse(doc.get("present").asBoolean());
            upload(base + "/fichiers", doc.get("code").asString(), 200);
        }
        upload(base + "/fichiers", "CONVENTION_STAGE", 409);
        JsonNode file = get(base + "/fichiers").get(0);
        var binary = http.send(builder("/fichiers/" + file.get("id").asInt() + "/telecharger").GET().build(), HttpResponse.BodyHandlers.ofByteArray());
        assertEquals(200, binary.statusCode()); assertTrue(new String(binary.body(), 0, 5, StandardCharsets.US_ASCII).equals("%PDF-"));
        request("POST", base + "/cloturer", Map.of(), 409); // Documents alone are not sufficient.
        for (JsonNode missingPeriod : get(base + "/periodes-rapports")) {
            if (LocalDate.parse(missingPeriod.get("fin").asString()).isAfter(today) || missingPeriod.get("validee").asBoolean()) continue;
            int requiredReport = create("/rapports-activite", Map.of("stageParticipantId", participation, "moisStageId", month,
                    "debutSemaine", missingPeriod.get("debut").asString(), "finSemaine", missingPeriod.get("fin").asString()));
            request("PATCH", "/rapports-activite/" + requiredReport + "/statut", Map.of("statutCode", "SOUMIS"), 200);
            request("PATCH", "/rapports-activite/" + requiredReport + "/statut", Map.of("statutCode", "VALIDE"), 200);
        }
        JsonNode closed = request("POST", base + "/cloturer", Map.of("motif", "Integration closure"), 200);
        request("PATCH", base, participationBody, 409);
        request("POST", base + "/prolonger", Map.of("nouvelleDateFin", today.plusDays(30).toString()), 409);
        assertEquals("CLOTUREE", closed.get("statut").asString()); assertFalse(closed.get("dateCloture").isNull());
        assertEquals("CLOTUREE", get(base).get("statut").asString());
        assertEquals("CLOTURE", get(base + "/evenements").get(3).get("code").asString());
        assertEquals(5, get(base + "/historique-statuts").size());
        assertEquals(0, get(base + "/actions").size());
        request("POST", base + "/cloturer", Map.of(), 409);
        assertEquals(0, get("/encadreurs/" + mentor + "/charge").get("participationsActives").asInt());
        assertNotNull(jdbc.queryForObject("select date_cloture from stage_participant where id=?", java.sql.Timestamp.class, participation));
        assertThrows(org.springframework.dao.DataAccessException.class, () -> jdbc.update("update stage_participant set statut_id=(select s.id from statuts s join types_statut t on t.id=s.type_statut_id where t.code='PARTICIPATION' and s.code='EN_COURS') where id=?", participation));
        assertThrows(org.springframework.dao.DataAccessException.class, () -> jdbc.update("update stage_participant set date_fin_stage=date_debut_stage-1 where id=?", participation));
        assertTrue(get("/journal-audit").size() > 0);
        int defaultMentor = create("/encadreurs", Map.of("nom", "Integration", "prenoms", "Default capacity", "departementId", department));
        assertEquals(5, get("/encadreurs/" + defaultMentor + "/charge").get("capaciteMax").asInt());
        var extraBody = new HashMap<String, Object>(participationBody);
        extraBody.put("dateDebutStage", today.plusDays(40).toString());
        extraBody.put("dateFinStage", today.plusDays(50).toString());
        int cancelled = create("/stage-participants", extraBody);
        request("POST", "/stage-participants/" + cancelled + "/annuler", Map.of(), 200);
        assertEquals("ANNULEE", get("/stage-participants/" + cancelled).get("statut").asString());
        extraBody.put("dateDebutStage", today.minusDays(24).toString());
        extraBody.put("dateFinStage", today.plusDays(7).toString());
        int capacityTrainee = create("/stagiaires", Map.of("nom", "Capacity", "prenoms", "One", "adresse", "test"));
        int capacityAcademic = create("/stagiaires/" + capacityTrainee + "/academiques", Map.of("niveauEtudeId", level, "filiereId", stream, "etablissementId", school, "anneeEtudes", "2026-2027"));
        extraBody.put("stagiaireAcademiqueId", capacityAcademic);
        int firstActive = create("/stage-participants", extraBody);
        extraBody.put("dateDebutStage", today.minusDays(23).toString());
        request("POST", "/stage-participants", extraBody, 409);
        int capacitySecond = create("/stagiaires", Map.of("nom", "Capacity", "prenoms", "Two", "adresse", "test"));
        int capacitySecondAcademic = create("/stagiaires/" + capacitySecond + "/academiques", Map.of("niveauEtudeId", level, "filiereId", stream, "etablissementId", school, "anneeEtudes", "2026-2027"));
        extraBody.put("stagiaireAcademiqueId", capacitySecondAcademic);
        extraBody.put("dateDebutStage", today.minusDays(23).toString());
        int secondActive = create("/stage-participants", extraBody);
        for (int id : List.of(firstActive, secondActive)) request("PATCH", "/stage-participants/" + id + "/statut", Map.of("statutCode", "EN_COURS"), 200);
        assertTrue(get("/encadreurs/" + mentor + "/charge").get("surcharge").asBoolean());
        for (int id : List.of(firstActive, secondActive)) request("POST", "/stage-participants/" + id + "/abandonner", Map.of(), 200);
        assertEquals("ABANDONNEE", get("/stage-participants/" + firstActive).get("statut").asString());
        assertFalse(get("/encadreurs/" + mentor + "/charge").get("surcharge").asBoolean());
        var mentorStatuses = get("/referentiels/suivi/statuts/ENCADREUR");
        String currentMentorStatus = get("/encadreurs/" + mentor).get("statut").asString();
        for (JsonNode status : mentorStatuses) {
            if (!currentMentorStatus.equals(status.get("code").asString())) {
                request("PATCH", "/encadreurs/" + mentor + "/statut", Map.of("statutCode", status.get("code").asString(), "motif", "Integration status"), 200);
                break;
            }
        }
        String adminToken = token;
        for (String role : List.of("RESPONSABLE_STAGE", "ENCADREUR", "STAGIAIRE")) {
            token = adminToken;
            create("/utilisateurs", Map.of("identifiant", "integration-" + role, "motDePasse", PASSWORD, "roleCode", role));
            token = login("integration-" + role);
            int expected = "STAGIAIRE".equals(role) ? 403 : 200;
            for (String path : List.of("/referentiels/suivi/types-documents", "/referentiels/suivi/types-evenements", "/referentiels/suivi/types-alertes",
                    "/referentiels/suivi/statuts/PARTICIPATION", "/referentiels/suivi/transitions/PARTICIPATION/A_CLOTURER",
                    base + "/documents-obligatoires", base + "/evenements", base + "/historique-statuts", base + "/periodes-rapports", base + "/actions", "/encadreurs/" + mentor + "/charge"))
                request("GET", path, null, path.startsWith("/referentiels/") ? ("STAGIAIRE".equals(role) && (path.endsWith("types-evenements") || path.endsWith("types-alertes")) ? 403 : 200) : 403);
            request("GET", "/utilisateurs", null, 403);
            request("POST", base + "/cloturer", Map.of(), 403);
        }
        request("POST", "/auth/logout", Map.of(), 204);
        request("GET", "/auth/me", null, 401);
    }
    @Test void departmentAndAssignmentIsolationOverHttp() throws Exception {
        String prefix = "scope-" + UUID.randomUUID().toString().substring(0, 8);
        int admin = seedUser(prefix + "-admin", "ADMIN");
        token = login(prefix + "-admin");
        int cadre = create("/referentiels/organisation/cadres-stage", Map.of("themeStage", prefix, "actif", true));
        int school = create("/referentiels/academiques/etablissements", Map.of("libelle", prefix, "actif", true));
        int stream = create("/referentiels/academiques/filieres", Map.of("libelle", prefix, "actif", true));
        int level = create("/referentiels/academiques/niveaux-etude", Map.of("libelle", prefix, "actif", true));
        int month = create("/referentiels/academiques/mois-stage", Map.of("libelle", prefix, "actif", true));
        int[] studentUsers = new int[2];
        int[] p = new int[2], t = new int[2], e = new int[2], r = new int[2], f = new int[2], a = new int[2], v = new int[2], d = new int[2];
        for (int i = 0; i < 2; i++) {
            int department = create("/referentiels/organisation/departements", Map.of("libelle", prefix + i));
            int manager = seedUser(prefix + "-manager" + i, "RESPONSABLE_STAGE");
            jdbc.update("insert into responsable_departements(utilisateur_id,departement_id) values (?,?)", manager, department);
            int mentorUser = seedUser(prefix + "-mentor" + i, "ENCADREUR");
            studentUsers[i] = seedUser(prefix + "-trainee" + i, "STAGIAIRE");
            int structure = create("/referentiels/organisation/structures-accueil", Map.of("libelle", prefix + i, "departementId", department, "actif", true));
            int stage = create("/stages", Map.of("libelle", prefix + i, "structureAccueilId", structure));
            t[i] = create("/stagiaires", Map.of("nom", prefix, "prenoms", "T" + i, "adresse", "test", "departementId", department));
            request("PATCH", "/stagiaires/" + t[i] + "/compte", Map.of("utilisateurId", studentUsers[i]), 200);
            int academic = create("/stagiaires/" + t[i] + "/academiques", Map.of("niveauEtudeId", level, "filiereId", stream, "etablissementId", school, "anneeEtudes", "2026-2027"));
            e[i] = create("/encadreurs", Map.of("nom", prefix, "prenoms", "E" + i, "departementId", department, "utilisateurId", mentorUser));
            p[i] = create("/stage-participants", Map.of("stageId", stage, "stagiaireAcademiqueId", academic, "cadreStageId", cadre, "encadreurId", e[i], "dateDebutStage", "2026-10-01", "dateFinStage", "2026-10-31"));
            r[i] = create("/rapports-activite", Map.of("stageParticipantId", p[i], "moisStageId", month, "debutSemaine", "2026-10-01", "finSemaine", "2026-10-04"));
            v[i] = create("/evaluations", Map.of("stageParticipantId", p[i], "encadreurId", e[i]));
            d[i] = create("/soutenances", Map.of("stageParticipantId", p[i], "dateSoutenance", "2026-10-30T14:00:00"));
            a[i] = create("/alertes", Map.of("typeAlerteCode", "DOSSIER_INCOMPLET", "stageParticipantId", p[i], "message", prefix, "priorite", "AVERTISSEMENT"));
            upload("/stage-participants/" + p[i] + "/fichiers", "CONVENTION_STAGE", 200);
            f[i] = get("/stage-participants/" + p[i] + "/fichiers").get(0).get("id").asInt();
        }
        request("PATCH", "/stagiaires/" + t[1] + "/compte", Map.of("utilisateurId", studentUsers[0]), 409);
        request("PATCH", "/stagiaires/" + t[0] + "/compte", Map.of("utilisateurId", admin), 409);
        for (int i = 0; i < 2; i++) {
            token = login(prefix + "-mentor" + i);
            request("POST", "/rapports-activite/" + r[i] + "/observations", Map.of("typeObservation", "OBSERVATION", "commentaire", "private", "visibleStagiaire", false), 201);
            request("POST", "/rapports-activite/" + r[i] + "/observations", Map.of("typeObservation", "OBSERVATION", "commentaire", "public", "visibleStagiaire", true), 201);
            token = login(prefix + "-trainee" + i);
            assertEquals(t[i], get("/portail/me").get("stagiaireId").asInt());
            assertEquals(1, get("/stagiaires").size()); assertEquals(1, get("/stage-participants").size());
            assertEquals(1, get("/stagiaires/" + t[i] + "/participations").size());
            assertEquals(1, get("/rapports-activite/" + r[i] + "/observations").size());
            assertEquals("public", get("/rapports-activite/" + r[i] + "/observations").get(0).get("commentaire").asString());
            request("GET", "/stagiaires/" + t[1-i], null, 403);
            request("GET", "/stage-participants/" + p[1-i] + "/periodes-rapports", null, 403);
            request("GET", "/rapports-activite/" + r[1-i] + "/observations", null, 403);
            assertEquals(200, http.send(builder("/fichiers/" + f[i] + "/telecharger").GET().build(), HttpResponse.BodyHandlers.discarding()).statusCode());
            assertEquals(403, http.send(builder("/fichiers/" + f[1-i] + "/telecharger").GET().build(), HttpResponse.BodyHandlers.discarding()).statusCode());
            request("PATCH", "/stagiaires/" + t[i] + "/compte", Map.of("utilisateurId", studentUsers[1-i]), 403);
            request("POST", "/stage-participants/" + p[i] + "/cloturer", Map.of(), 403);
            request("POST", "/rapports-activite/" + r[i] + "/observations", Map.of("typeObservation", "VALIDATION", "commentaire", "forged"), 403);
            request("PATCH", "/rapports-activite/" + r[i] + "/statut", Map.of("statutCode", "VALIDE"), 403);
            var body = Map.of("stageParticipantId", p[i], "moisStageId", month, "debutSemaine", "2026-10-05", "finSemaine", "2026-10-11", "activite", "own work");
            int ownReport = create("/rapports-activite", body);
            request("PATCH", "/rapports-activite/" + ownReport + "/statut", Map.of("statutCode", "SOUMIS"), 200);
            request("PATCH", "/rapports-activite/" + ownReport, body, 403);
            token = login(prefix + "-mentor" + i);
            request("PATCH", "/rapports-activite/" + ownReport + "/statut", Map.of("statutCode", "A_CORRIGER"), 200);
            token = login(prefix + "-trainee" + i);
            request("PATCH", "/rapports-activite/" + ownReport, body, 200);
            request("PATCH", "/rapports-activite/" + ownReport + "/statut", Map.of("statutCode", "SOUMIS"), 200);
            token = login(prefix + "-mentor" + i);
            request("PATCH", "/rapports-activite/" + ownReport + "/statut", Map.of("statutCode", "VALIDE"), 200);
            token = login(prefix + "-trainee" + i);
            assertEquals("VALIDE", get("/rapports-activite/" + ownReport).get("statut").asString());
        }
        for (String role : List.of("manager", "mentor")) for (int i = 0; i < 2; i++) {
            token = login(prefix + "-" + role + i);
            for (String list : List.of("/stagiaires", "/stage-participants", "/stages", "/encadreurs", "/rapports-activite", "/evaluations", "/soutenances", "/alertes")) assertEquals(list.equals("/rapports-activite") ? 2 : 1, get(list).size(), role + list);
            assertEquals(p[i], get("/stage-participants").get(0).get("id").asInt());
            assertEquals(1, get("/dashboard/resume").get("totalParticipations").asInt());
            int other = 1 - i;
            for (int index : new int[] {i, other}) {
                int expected = index == i ? 200 : 403;
                for (String path : List.of("/stagiaires/" + t[index], "/stagiaires/" + t[index] + "/academiques", "/encadreurs/" + e[index] + "/charge",
                        "/rapports-activite/" + r[index], "/evaluations/" + v[index], "/evaluations/" + v[index] + "/notes", "/soutenances/" + d[index])) request("GET", path, null, expected);
                for (String suffix : List.of("", "/evenements", "/historique-statuts", "/periodes-rapports", "/documents-obligatoires", "/fichiers")) request("GET", "/stage-participants/" + p[index] + suffix, null, expected);
                var download = http.send(builder("/fichiers/" + f[index] + "/telecharger").GET().build(), HttpResponse.BodyHandlers.discarding());
                assertEquals(expected, download.statusCode());
            }
            request("PATCH", "/alertes/" + a[other] + "/lire", Map.of(), 403);
            request("POST", "/stage-participants/" + p[other] + "/annuler", Map.of(), 403);
            request("PATCH", "/rapports-activite/" + r[other] + "/statut", Map.of("statutCode", "SOUMIS"), 403);
            request("POST", "/evaluations/" + v[other] + "/finaliser", Map.of(), 403);
            request("POST", "/rapports-activite", Map.of("stageParticipantId", p[other], "moisStageId", month, "debutSemaine", "2026-10-05", "finSemaine", "2026-10-11"), 403);
        }
    }
    @Test void passwordResetRevokesExistingSessions() throws Exception {
        String prefix = "reset-" + UUID.randomUUID().toString().substring(0,8);
        seedUser(prefix + "-admin", "ADMIN"); int target = seedUser(prefix + "-trainee", "STAGIAIRE");
        String oldToken = login(prefix + "-trainee");
        token = login(prefix + "-admin");
        request("PATCH", "/utilisateurs/" + target + "/mot-de-passe", Map.of("nouveauMotDePasse", "new-integration-password"), 204);
        token = oldToken; request("GET", "/auth/me", null, 401);
    }
    int seedUser(String name, String role) {
        jdbc.update("insert into utilisateurs(identifiant,mot_de_passe_hash,statut_id) select ?,?,s.id from statuts s join types_statut t on t.id=s.type_statut_id where t.code='UTILISATEUR' and s.code='ACTIF'", name, encoder.encode(PASSWORD));
        jdbc.update("insert into utilisateurs_role(utilisateur_id,role_id) select u.id,r.id from utilisateurs u cross join roles r where u.identifiant=? and r.code=?", name, role);
        return jdbc.queryForObject("select id from utilisateurs where identifiant=?", Integer.class, name);
    }
    String login(String name) throws Exception {
        String previous = token; token = null;
        try { return request("POST", "/auth/login", Map.of("identifiant", name, "motDePasse", PASSWORD), 200).get("token").asString(); }
        finally { token = previous; }
    }
    JsonNode get(String path) throws Exception { return request("GET", path, null, 200); }
    int create(String path, Object body) throws Exception { return request("POST", path, body, 201).get("id").asInt(); }
    HttpRequest.Builder builder(String path) {
        var builder = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api" + path)).timeout(Duration.ofSeconds(10));
        if (token != null) builder.header("Authorization", "Bearer " + token);
        return builder;
    }
    JsonNode request(String method, String path, Object body, int expected) throws Exception {
        var response = http.send(builder(path).header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body))).build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(expected, response.statusCode(), method + " " + path);
        return response.body().isBlank() ? json.nullNode() : json.readTree(response.body());
    }
    void upload(String path, String code, int expected) throws Exception {
        String boundary = "integration-boundary";
        String head = "--" + boundary + "\r\nContent-Disposition: form-data; name=\"typePieceJointeCode\"\r\n\r\n" + code
                + "\r\n--" + boundary + "\r\nContent-Disposition: form-data; name=\"fichier\"; filename=\"integration.pdf\"\r\nContent-Type: application/pdf\r\n\r\n";
        var body = new java.io.ByteArrayOutputStream(); body.write(head.getBytes(StandardCharsets.UTF_8));
        body.write(PdfFixtures.document()); body.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        var response = http.send(builder(path).header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray())).build(), HttpResponse.BodyHandlers.discarding());
        assertEquals(expected, response.statusCode(), "upload " + code);
    }
}

