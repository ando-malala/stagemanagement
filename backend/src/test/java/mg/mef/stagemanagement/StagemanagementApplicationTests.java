package mg.mef.stagemanagement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import mg.mef.stagemanagement.service.AlerteOrchestrationService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
		"server.address=127.0.0.1",
		"spring.jpa.hibernate.ddl-auto=validate"
})
class StagemanagementApplicationTests {

	@Value("${local.server.port}")
	private int port;

	// Prevent the daily scheduler from writing alerts if tests run at 06:00.
	@MockitoBean
	private AlerteOrchestrationService alerteOrchestrationService;

	@Test
	void anonymousRequestsAreRejectedOverHttp() throws Exception {
		for (String path : new String[] { "/api/auth/me", "/api/dashboard/resume",
				"/api/stagiaires", "/api/utilisateurs", "/api/journal-audit",
				"/api/referentiels/academiques/etablissements" }) {
			assertEquals(401, get(path, null), path);
		}
	}

	@Test
	void unknownSessionIsRejectedOverHttp() throws Exception {
		// A lookup only: no login, account creation or session revocation.
		assertEquals(401, get("/api/auth/me", "integration-test-session-that-does-not-exist"));
	}

	private int get(String path, String token) throws Exception {
		HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
				.timeout(Duration.ofSeconds(5)).header("Accept", "application/json").GET();
		if (token != null) request.header("Authorization", "Bearer " + token);
		return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build()
				.send(request.build(), HttpResponse.BodyHandlers.discarding()).statusCode();
	}
}
