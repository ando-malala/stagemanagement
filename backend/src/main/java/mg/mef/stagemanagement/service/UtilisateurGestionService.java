package mg.mef.stagemanagement.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import mg.mef.stagemanagement.dto.utilisateur.CreerUtilisateurRequest;
import mg.mef.stagemanagement.dto.utilisateur.UtilisateurListResponse;
import mg.mef.stagemanagement.model.Role;
import mg.mef.stagemanagement.model.Statut;
import mg.mef.stagemanagement.model.Utilisateur;
import mg.mef.stagemanagement.model.UtilisateurRole;
import mg.mef.stagemanagement.repository.RoleRepository;
import mg.mef.stagemanagement.repository.StatutRepository;
import mg.mef.stagemanagement.repository.UtilisateurRepository;
import mg.mef.stagemanagement.repository.UtilisateurRoleRepository;

@Service
public class UtilisateurGestionService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final StatutRepository statutRepository;
    private final UtilisateurRoleRepository utilisateurRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurGestionService(
            UtilisateurRepository utilisateurRepository,
            RoleRepository roleRepository,
            StatutRepository statutRepository,
            UtilisateurRoleRepository utilisateurRoleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.statutRepository = statutRepository;
        this.utilisateurRoleRepository = utilisateurRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UtilisateurListResponse creerUtilisateur( CreerUtilisateurRequest request ) {

        // 1. Vérifier les informations reçues

        if (request == null
                || request.getIdentifiant() == null
                || request.getMotDePasse() == null
                || request.getRoleCode() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informations obligatoires manquantes"
            );
        }

        String identifiant = request.getIdentifiant().trim();
        String motDePasse = request.getMotDePasse();
        String roleCode = request.getRoleCode().trim();

        if (identifiant.isEmpty()
                || identifiant.length() > 50
                || motDePasse.length() < 12
                || roleCode.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Identifiant, mot de passe ou rôle invalide"
            );
        }

        // 2. Vérifier l'unicité de l'identifiant

        if (utilisateurRepository.existsByIdentifiant(identifiant)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet identifiant existe déjà"
            );
        }

        // 3. Récupérer le rôle

        Role role = roleRepository.findByCode(roleCode)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Rôle introuvable"
                        )
                );

        if (!Boolean.TRUE.equals(role.getActif())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ce rôle est désactivé"
            );
        }

        // 4. Récupérer le statut ACTIF des utilisateurs

        Statut statut = statutRepository
                .findStatutActifUtilisateur()
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Statut ACTIF des utilisateurs introuvable"
                        )
                );

        // 5. Créer l'utilisateur

        Utilisateur utilisateur = new Utilisateur();

        utilisateur.setIdentifiant(identifiant);

        utilisateur.setMotDePasseHash(
                passwordEncoder.encode(motDePasse)
        );

        utilisateur.setStatut(statut);
        utilisateur.setDateCreation(LocalDateTime.now());

        Utilisateur utilisateurEnregistre =
                utilisateurRepository.save(utilisateur);

        // 6. Attribuer le rôle

        UtilisateurRole utilisateurRole = new UtilisateurRole();

        utilisateurRole.setUtilisateur(utilisateurEnregistre);
        utilisateurRole.setRole(role);

        utilisateurRoleRepository.save(utilisateurRole);

        // 7. Retourner une réponse sans mot de passe

        return new UtilisateurListResponse(
                utilisateurEnregistre.getId(),
                utilisateurEnregistre.getIdentifiant(),
                statut.getCode()
        );
    }

    @Transactional(readOnly = true)
    public UtilisateurListResponse trouverUtilisateur(Integer id) {

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Utilisateur introuvable"
                        )
                );

        return new UtilisateurListResponse(
                utilisateur.getId(),
                utilisateur.getIdentifiant(),
                utilisateur.getStatut().getCode()
        );
    }
}