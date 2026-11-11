package mg.mef.stagemanagement.service;

import java.time.LocalDateTime;
import java.util.List;
import mg.mef.stagemanagement.security.ApplicationRoles;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import mg.mef.stagemanagement.dto.utilisateur.AjouterDepartementUtilisateurRequest;
import mg.mef.stagemanagement.dto.utilisateur.AjouterRoleUtilisateurRequest;
import mg.mef.stagemanagement.dto.utilisateur.ChangerStatutUtilisateurRequest;
import mg.mef.stagemanagement.dto.utilisateur.CreerUtilisateurRequest;
import mg.mef.stagemanagement.dto.utilisateur.DepartementUtilisateurResponse;
import mg.mef.stagemanagement.dto.utilisateur.ReinitialiserMotDePasseRequest;
import mg.mef.stagemanagement.dto.utilisateur.UtilisateurListResponse;
import mg.mef.stagemanagement.model.Departement;
import mg.mef.stagemanagement.model.ResponsableDepartement;
import mg.mef.stagemanagement.model.Role;
import mg.mef.stagemanagement.model.Statut;
import mg.mef.stagemanagement.model.Utilisateur;
import mg.mef.stagemanagement.model.UtilisateurRole;
import mg.mef.stagemanagement.model.UtilisateurStatutHistorique;
import mg.mef.stagemanagement.repository.DepartementRepository;
import mg.mef.stagemanagement.repository.ResponsableDepartementRepository;
import mg.mef.stagemanagement.repository.RoleRepository;
import mg.mef.stagemanagement.repository.StatutRepository;
import mg.mef.stagemanagement.repository.UtilisateurRepository;
import mg.mef.stagemanagement.repository.UtilisateurRoleRepository;
import mg.mef.stagemanagement.repository.UtilisateurStatutHistoriqueRepository;

@Service
@org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
public class UtilisateurGestionService {
        private final mg.mef.stagemanagement.repository.StagiaireRepository trainees;
        private final mg.mef.stagemanagement.repository.EncadreurRepository mentors;
        private final mg.mef.stagemanagement.repository.SessionUtilisateurRepository sessions;

        private final UtilisateurRepository utilisateurRepository;
        private final RoleRepository roleRepository;
        private final StatutRepository statutRepository;
        private final UtilisateurRoleRepository utilisateurRoleRepository;
        private final PasswordEncoder passwordEncoder;
        private final UtilisateurStatutHistoriqueRepository utilisateurStatutHistoriqueRepository;
        private final DepartementRepository departementRepository;
        private final ResponsableDepartementRepository responsableDepartementRepository;

        public UtilisateurGestionService(
                UtilisateurRepository utilisateurRepository,
                RoleRepository roleRepository,
                StatutRepository statutRepository,
                UtilisateurRoleRepository utilisateurRoleRepository,
                PasswordEncoder passwordEncoder,
                UtilisateurStatutHistoriqueRepository utilisateurStatutHistoriqueRepository,
                DepartementRepository departementRepository,
                ResponsableDepartementRepository responsableDepartementRepository, mg.mef.stagemanagement.repository.SessionUtilisateurRepository sessions,
                mg.mef.stagemanagement.repository.StagiaireRepository trainees, mg.mef.stagemanagement.repository.EncadreurRepository mentors
        ) {
                this.utilisateurRepository = utilisateurRepository; this.sessions = sessions; this.trainees = trainees; this.mentors = mentors;
                this.roleRepository = roleRepository;
                this.statutRepository = statutRepository;
                this.utilisateurRoleRepository = utilisateurRoleRepository;
                this.passwordEncoder = passwordEncoder;
                this.utilisateurStatutHistoriqueRepository = utilisateurStatutHistoriqueRepository;
                this.departementRepository = departementRepository;
                this.responsableDepartementRepository = responsableDepartementRepository;
        }

        @Transactional(readOnly = false)
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
                String roleCode = request.getRoleCode().trim().toUpperCase(java.util.Locale.ROOT);
                verifierRoleSupporte(roleCode);

                if (identifiant.isEmpty()
                        || identifiant.length() > 50
                        || motDePasse.length() < 6
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

        @Transactional(readOnly = false)
        public UtilisateurListResponse changerStatut(Integer utilisateurId, ChangerStatutUtilisateurRequest request, Integer responsableId ){
                if (request == null 
                        || request.getStatutCode() == null 
                        || request.getStatutCode().trim().isEmpty()){

                                throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                "Le nouveau statut est obligatoire"
                                );
                }

                Utilisateur utilisateur = utilisateurRepository.findForUpdate(utilisateurId)
                        .orElseThrow(() -> 
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                "Utilisateur introuvable"
                                )
                        );

                Utilisateur responsable = utilisateurRepository.findById(responsableId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Responsable introuvable"
                                )
                        );

                String nouveauStatutCode = request.getStatutCode().trim().toUpperCase();

                Statut nouveauStatut = statutRepository
                        .findStatutUtilisateurByCode(nouveauStatutCode)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Statut utilisateur invalide"
                                )
                        );

                Statut ancienStatut = utilisateur.getStatut();

                 if (ancienStatut != null
                        && ancienStatut.getId().equals(nouveauStatut.getId())) {

                        return new UtilisateurListResponse(
                                utilisateur.getId(),
                                utilisateur.getIdentifiant(),
                                nouveauStatut.getCode()
                        );
                }

                AssociationRules.active(nouveauStatut.getActif(), "Statut");
                utilisateur.setStatut(nouveauStatut);

                Utilisateur utilisateurEnregistre =
                        utilisateurRepository.save(utilisateur);

                UtilisateurStatutHistorique historique =
                        new UtilisateurStatutHistorique();

                historique.setUtilisateur(utilisateurEnregistre);
                historique.setAncienStatut(ancienStatut);
                historique.setStatut(nouveauStatut);
                historique.setResponsable(responsable);
                historique.setMotif(request.getMotif());
                historique.setDateModification(LocalDateTime.now());

                utilisateurStatutHistoriqueRepository.save(historique);

                return new UtilisateurListResponse(
                        utilisateurEnregistre.getId(),
                        utilisateurEnregistre.getIdentifiant(),
                        nouveauStatut.getCode()
                );
        }

        @Transactional(readOnly = false)
        public void reinitialiserMotDePasse( Integer utilisateurId, ReinitialiserMotDePasseRequest request ) {

                if (request == null
                        || request.getNouveauMotDePasse() == null
                        || request.getNouveauMotDePasse().length() < 12) {

                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Le nouveau mot de passe doit contenir au moins 12 caractères"
                        );
                }

                Utilisateur utilisateur = utilisateurRepository.findForUpdate(utilisateurId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Utilisateur introuvable"
                                )
                        );

                utilisateur.setMotDePasseHash(
                        passwordEncoder.encode(request.getNouveauMotDePasse())
                );

                utilisateurRepository.save(utilisateur);
                sessions.revokeAll(utilisateurId, LocalDateTime.now());
        }

        @Transactional(readOnly = true)
        public List<String> listerRolesUtilisateur(Integer utilisateurId) {

                Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Utilisateur introuvable"
                                )
                        );

                return utilisateurRoleRepository
                        .findByUtilisateur_Id(utilisateur.getId())
                        .stream()
                        .map(utilisateurRole ->
                                utilisateurRole.getRole().getCode()
                        )
                        .map(ApplicationRoles::effective)
                        .filter(ApplicationRoles.SUPPORTED::contains)
                        .distinct()
                        .toList();
        }

        @Transactional(readOnly = false)
        public List<String> ajouterRole( Integer utilisateurId, AjouterRoleUtilisateurRequest request) {

                if (request == null
                        || request.getRoleCode() == null
                        || request.getRoleCode().trim().isEmpty()) {

                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Le rôle est obligatoire"
                        );
                }

                Utilisateur utilisateur = utilisateurRepository.findForUpdate(utilisateurId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Utilisateur introuvable"
                                )
                        );

                String roleCode = request.getRoleCode().trim().toUpperCase(java.util.Locale.ROOT);
                verifierRoleSupporte(roleCode);

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

                boolean existeDeja =
                        utilisateurRoleRepository.existsByUtilisateur_IdAndRole_Id(
                                utilisateur.getId(),
                                role.getId()
                        );

                if (existeDeja) {
                        return listerRolesUtilisateur(utilisateur.getId());
                }

                UtilisateurRole utilisateurRole = new UtilisateurRole();

                utilisateurRole.setUtilisateur(utilisateur);
                utilisateurRole.setRole(role);

                utilisateurRoleRepository.save(utilisateurRole);

                return listerRolesUtilisateur(utilisateur.getId());
        }

        @Transactional(readOnly = false)
        public List<String> retirerRole( Integer utilisateurId,String roleCode) {

                if (roleCode == null || roleCode.trim().isEmpty()) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Le rôle est obligatoire"
                        );
                }

                Utilisateur utilisateur = utilisateurRepository.findForUpdate(utilisateurId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Utilisateur introuvable"
                                )
                        );

                String roleCodeNettoye = roleCode.trim().toUpperCase(java.util.Locale.ROOT);
                verifierRoleSupporte(roleCodeNettoye);
                if (("STAGIAIRE".equals(roleCodeNettoye) && trainees.findByUtilisateur_Id(utilisateurId).isPresent())
                        || ("ENCADREUR".equals(roleCodeNettoye) && mentors.findByUtilisateur_Id(utilisateurId).isPresent()))
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Dissociez d'abord le compte de sa fiche métier avant de retirer ce rôle");
                var attributions = utilisateurRoleRepository.findByUtilisateur_Id(utilisateur.getId());
                var aRetirer = attributions.stream()
                        .filter(attribution -> roleCodeNettoye.equals(ApplicationRoles.effective(attribution.getRole().getCode())))
                        .toList();
                if (aRetirer.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ce rôle n'est pas attribué à cet utilisateur");
                }
                boolean autreRoleActif = attributions.stream().anyMatch(attribution ->
                        !aRetirer.contains(attribution) && Boolean.TRUE.equals(attribution.getRole().getActif())
                        && ApplicationRoles.SUPPORTED.contains(ApplicationRoles.effective(attribution.getRole().getCode())));
                if (!autreRoleActif) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un utilisateur doit conserver au moins un rôle actif");
                }
                // Remove both ADMIN and its legacy alias, otherwise administrative access would survive.
                utilisateurRoleRepository.deleteAll(aRetirer);

                return listerRolesUtilisateur(utilisateur.getId());
        }

        @Transactional(readOnly = true)
        public List<DepartementUtilisateurResponse> listerDepartementsUtilisateur(
                Integer utilisateurId
        ) {

                Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Utilisateur introuvable"
                                )
                        );

                return responsableDepartementRepository
                        .findByUtilisateur_Id(utilisateur.getId())
                        .stream()
                        .map(responsableDepartement -> {
                                Departement departement =
                                        responsableDepartement.getDepartement();

                                String statut = departement.getStatut() != null
                                        ? departement.getStatut().getCode()
                                        : null;

                                return new DepartementUtilisateurResponse(
                                        departement.getId(),
                                        departement.getLibelle(),
                                        statut
                                );
                        })
                        .toList();
        }

        @Transactional(readOnly = false)
        public List<DepartementUtilisateurResponse> ajouterDepartementUtilisateur(
                Integer utilisateurId,
                AjouterDepartementUtilisateurRequest request
        ) {

                if (request == null || request.getDepartementId() == null) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Le département est obligatoire"
                        );
                }

                Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Utilisateur introuvable"
                                )
                        );

                Departement departement = departementRepository.findById(request.getDepartementId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Département introuvable"
                                )
                        );

                boolean existeDeja =
                        responsableDepartementRepository.existsByUtilisateur_IdAndDepartement_Id(
                                utilisateur.getId(),
                                departement.getId()
                        );

                if (existeDeja) {
                        return listerDepartementsUtilisateur(utilisateur.getId());
                }

                ResponsableDepartement responsableDepartement =
                        new ResponsableDepartement();

                AssociationRules.activeStatus(departement.getStatut(), "Département");

                responsableDepartement.setUtilisateur(utilisateur);
                responsableDepartement.setDepartement(departement);

                responsableDepartementRepository.save(responsableDepartement);

                return listerDepartementsUtilisateur(utilisateur.getId());
        }

        @Transactional(readOnly = false)
        public List<DepartementUtilisateurResponse> retirerDepartementUtilisateur(
                Integer utilisateurId,
                Integer departementId
        ) {

                Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Utilisateur introuvable"
                                )
                        );

                ResponsableDepartement responsableDepartement =
                        responsableDepartementRepository
                                .findByUtilisateur_IdAndDepartement_Id(
                                        utilisateur.getId(),
                                        departementId
                                )
                                .orElseThrow(() ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Ce département n'est pas attribué à cet utilisateur"
                                        )
                                );

                responsableDepartementRepository.delete(responsableDepartement);

                return listerDepartementsUtilisateur(utilisateur.getId());
        }
        private void verifierRoleSupporte(String code) {
                if (!ApplicationRoles.SUPPORTED.contains(code)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rôle attendu : ADMIN, ENCADREUR ou STAGIAIRE");
                }
        }
}
