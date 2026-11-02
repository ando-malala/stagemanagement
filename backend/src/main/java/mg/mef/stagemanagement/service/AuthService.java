package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.dto.auth.LoginRequest;
import mg.mef.stagemanagement.dto.auth.LoginResponse;
import mg.mef.stagemanagement.model.HistoriqueConnexionUtilisateur;
import mg.mef.stagemanagement.model.SessionUtilisateur;
import mg.mef.stagemanagement.model.Utilisateur;
import mg.mef.stagemanagement.repository.HistoriqueConnexionUtilisateurRepository;
import mg.mef.stagemanagement.repository.SessionUtilisateurRepository;
import mg.mef.stagemanagement.repository.UtilisateurRepository;
import mg.mef.stagemanagement.repository.UtilisateurRoleRepository;
import mg.mef.stagemanagement.dto.auth.AuthMeResponse; 

import org.springframework.boot.web.server.servlet.Session;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class AuthService {

        private final UtilisateurRepository utilisateurRepository;
        private final UtilisateurRoleRepository utilisateurRoleRepository;
        private final SessionUtilisateurRepository sessionUtilisateurRepository;
        private final HistoriqueConnexionUtilisateurRepository historiqueConnexionRepository;
        private final PasswordEncoder passwordEncoder;

        private final SecureRandom secureRandom = new SecureRandom();

        public AuthService(
                UtilisateurRepository utilisateurRepository,
                UtilisateurRoleRepository utilisateurRoleRepository,
                SessionUtilisateurRepository sessionUtilisateurRepository,
                HistoriqueConnexionUtilisateurRepository historiqueConnexionRepository,
                PasswordEncoder passwordEncoder
        ){
                this.utilisateurRepository = utilisateurRepository;
                this.utilisateurRoleRepository = utilisateurRoleRepository;
                this.sessionUtilisateurRepository = sessionUtilisateurRepository;
                this.historiqueConnexionRepository = historiqueConnexionRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Transactional 
        public LoginResponse login(LoginRequest request){
                
                //1 - Chercher l'utilisateur
                Utilisateur utilisateur = utilisateurRepository
                        .findByIdentifiant(request.getIdentifiant())
                        .orElseThrow(() -> 
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Identifiant ou mot de passe incorrect"
                                )
                );

                // 2- verfifier le mot de passe 
                boolean motDePasseCorrect = passwordEncoder.matches(
                request.getMotDePasse(), 
                utilisateur.getMotDePasseHash()
                );

                if (!motDePasseCorrect) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Identifiant ou mot de passe incorrect"
                );
                }

                // 3- verifier que le compte est ACTIF
                if(!"ACTIF".equals(utilisateur.getStatut().getCode())){
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Ce compte n'est pas actif"
                );
                }

                // 4 - recuperer les roles
                List<String> roles = utilisateurRoleRepository
                        .findByUtilisateur_Id(utilisateur.getId())
                        .stream()
                        .filter(utilisateurRole ->
                                Boolean.TRUE.equals(utilisateurRole.getRole().getActif())
                        )
                        .map(utilisateurRole ->
                                utilisateurRole.getRole().getCode()
                        )
                        .map(mg.mef.stagemanagement.security.ApplicationRoles::effective)
                        .filter(mg.mef.stagemanagement.security.ApplicationRoles.SUPPORTED::contains)
                        .distinct()
                        .toList();
                
                // 5 - Generer le token envoye au client
                String token = genererToken();

                // 6 - ne stocker le hash en base
                String tokenHash = hasherToken(token);

                // 7 - creer la session
                SessionUtilisateur session = new SessionUtilisateur();
                session.setUtilisateur(utilisateur);
                session.setJetonHash(tokenHash);
                session.setDateCreation(LocalDateTime.now());

                // session valable on va faire hoe 8 heure
                session.setDateExpiration(LocalDateTime.now().plusHours(8));
                
                sessionUtilisateurRepository.save(session);

                // 8 - historiser la connexion
                HistoriqueConnexionUtilisateur historique = 
                        new HistoriqueConnexionUtilisateur();
                
                historique.setUtilisateur(utilisateur);
                historique.setDateConnexion(LocalDateTime.now());

                historiqueConnexionRepository.save(historique);

                // 9 - Reponse au frontend
                return new LoginResponse(
                token,
                utilisateur.getId(),
                utilisateur.getIdentifiant(),
                roles
                );

        }

        private String genererToken(){
                byte[] bytes = new byte[32];
                secureRandom.nextBytes(bytes);
                return Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(bytes);

        }

        private String hasherToken(String token) {

                try {

                MessageDigest digest =
                        MessageDigest.getInstance("SHA-256");

                byte[] hash = digest.digest(
                        token.getBytes(StandardCharsets.UTF_8)
                );

                return Base64.getEncoder()
                        .encodeToString(hash);

                } catch (NoSuchAlgorithmException e) {

                throw new IllegalStateException(
                        "Impossible de générer le hash du token",
                        e
                );
                }
        }

        @Transactional(readOnly=true)
        public AuthMeResponse me(String token){
                String tokenHash = hasherToken(token);

                SessionUtilisateur session = sessionUtilisateurRepository
                                .findByJetonHashAndDateRevocationIsNullAndDateExpirationAfter(
                                        tokenHash,
                                        LocalDateTime.now()
                                )
                                .orElseThrow(() ->
                                        new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "Session invalide ou expirée"
                                        )
                                );

                Utilisateur utilisateur = session.getUtilisateur();

                if (!"ACTIF".equals(utilisateur.getStatut().getCode())) {
                        throw new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "Ce compte n'est pas actif"
                        );
                }

                List<String> roles = utilisateurRoleRepository
                        .findByUtilisateur_Id(utilisateur.getId())
                        .stream()
                        .filter(utilisateurRole ->
                                Boolean.TRUE.equals(
                                        utilisateurRole.getRole().getActif()
                                )
                        )
                        .map(utilisateurRole ->
                                utilisateurRole.getRole().getCode()
                        )
                        .map(mg.mef.stagemanagement.security.ApplicationRoles::effective)
                        .filter(mg.mef.stagemanagement.security.ApplicationRoles.SUPPORTED::contains)
                        .distinct()
                        .toList();

                return new AuthMeResponse(
                        utilisateur.getId(),
                        utilisateur.getIdentifiant(),
                        roles
                );
        }

        @Transactional
        public void logout(String token) {

                String tokenHash = hasherToken(token);

                SessionUtilisateur session =
                        sessionUtilisateurRepository
                                .findByJetonHashAndDateRevocationIsNullAndDateExpirationAfter(
                                        tokenHash,
                                        LocalDateTime.now()
                                )
                                .orElseThrow(() ->
                                        new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "Session invalide ou expirée"
                                        )
                                );

                session.setDateRevocation(LocalDateTime.now());

                sessionUtilisateurRepository.save(session);
        }

}
