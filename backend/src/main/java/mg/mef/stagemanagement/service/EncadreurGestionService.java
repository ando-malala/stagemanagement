package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.dto.encadreur.ChangerStatutEncadreurRequest;
import mg.mef.stagemanagement.dto.encadreur.EncadreurRequest;
import mg.mef.stagemanagement.dto.encadreur.EncadreurResponse;
import mg.mef.stagemanagement.model.Departement;
import mg.mef.stagemanagement.model.Encadreur;
import mg.mef.stagemanagement.model.EncadreurStatutHistorique;
import mg.mef.stagemanagement.model.Statut;
import mg.mef.stagemanagement.model.Utilisateur;
import mg.mef.stagemanagement.repository.DepartementRepository;
import mg.mef.stagemanagement.repository.EncadreurRepository;
import mg.mef.stagemanagement.repository.EncadreurStatutHistoriqueRepository;
import mg.mef.stagemanagement.repository.StatutRepository;
import mg.mef.stagemanagement.repository.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EncadreurGestionService {
    private final DossierAccessService access;

    private final EncadreurRepository encadreurRepository;
    private final EncadreurStatutHistoriqueRepository encadreurStatutHistoriqueRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final DepartementRepository departementRepository;
    private final StatutRepository statutRepository;
    private final mg.mef.stagemanagement.repository.UtilisateurRoleRepository roles;

    public EncadreurGestionService(
            EncadreurRepository encadreurRepository,
            EncadreurStatutHistoriqueRepository encadreurStatutHistoriqueRepository,
            UtilisateurRepository utilisateurRepository,
            DepartementRepository departementRepository,
            StatutRepository statutRepository
    , DossierAccessService access, mg.mef.stagemanagement.repository.UtilisateurRoleRepository roles) {
        this.roles = roles;
        this.access = access;
        this.encadreurRepository = encadreurRepository;
        this.encadreurStatutHistoriqueRepository = encadreurStatutHistoriqueRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.departementRepository = departementRepository;
        this.statutRepository = statutRepository;
    }

    public List<EncadreurResponse> listerEncadreurs() {
        return encadreurRepository.findAll()
                .stream().filter(access::readMentor)
                .sorted(Comparator.comparing(Encadreur::getNom)
                        .thenComparing(Encadreur::getPrenoms))
                .map(this::toResponse)
                .toList();
    }

    public List<PortailStagiaireService.Compte> comptesEligibles(Integer encadreurId) {
        access.require(access.role("ADMIN"));
        if (encadreurId != null) access.mentor(encadreurId, false);
        return utilisateurRepository.findEligibleMentorAccounts(encadreurId == null ? -1 : encadreurId).stream()
                .map(u -> new PortailStagiaireService.Compte(u.getId(), u.getIdentifiant())).toList();
    }

    public EncadreurResponse trouverEncadreur(Integer id) {
        access.mentor(id, false);
        Encadreur encadreur = encadreurRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Encadreur introuvable"
                        )
                );

        return toResponse(encadreur);
    }

    @Transactional(readOnly = false)
    public EncadreurResponse creerEncadreur(EncadreurRequest request) {
        if (request != null) access.require(access.department(request.getDepartementId()));
        Encadreur encadreur = new Encadreur();

        remplirEncadreur(encadreur, request, true);

        Statut statutActif = statutRepository
                .findStatutByTypeCodeAndStatutCode("ENCADREUR", "ACTIF")
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Statut ACTIF des encadreurs introuvable"
                        )
                );

        encadreur.setStatut(statutActif);

        Encadreur enregistre = encadreurRepository.save(encadreur);

        return toResponse(enregistre);
    }

    @Transactional(readOnly = false)
    public EncadreurResponse modifierEncadreur(
            Integer id,
            EncadreurRequest request
    ) {
        access.mentor(id, true); if (request != null) access.require(access.department(request.getDepartementId()));
        Encadreur encadreur = encadreurRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Encadreur introuvable"
                        )
                );

        remplirEncadreur(encadreur, request, false);

        Encadreur enregistre = encadreurRepository.save(encadreur);

        return toResponse(enregistre);
    }

    @Transactional(readOnly = false)
    public EncadreurResponse changerStatut(
            Integer encadreurId,
            ChangerStatutEncadreurRequest request,
            Integer responsableId
    ) {
        access.mentor(encadreurId, true);
        if (request == null
                || request.getStatutCode() == null
                || request.getStatutCode().trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le nouveau statut est obligatoire"
            );
        }

        Encadreur encadreur = encadreurRepository.findById(encadreurId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Encadreur introuvable"
                        )
                );

        Utilisateur responsable = utilisateurRepository.findById(responsableId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Responsable introuvable"
                        )
                );

        String statutCode = request.getStatutCode().trim().toUpperCase();

        Statut nouveauStatut = statutRepository
                .findStatutByTypeCodeAndStatutCode("ENCADREUR", statutCode)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Statut encadreur invalide"
                        )
                );

        Statut ancienStatut = encadreur.getStatut();
        AssociationRules.active(nouveauStatut.getActif(), "Statut");

        if (ancienStatut != null
                && ancienStatut.getId().equals(nouveauStatut.getId())) {

            return toResponse(encadreur);
        }

        encadreur.setStatut(nouveauStatut);

        Encadreur encadreurEnregistre =
                encadreurRepository.save(encadreur);

        EncadreurStatutHistorique historique =
                new EncadreurStatutHistorique();

        historique.setEncadreur(encadreurEnregistre);
        historique.setAncienStatut(ancienStatut);
        historique.setStatut(nouveauStatut);
        historique.setResponsable(responsable);
        historique.setMotif(request.getMotif());
        historique.setDateModification(LocalDateTime.now());

        encadreurStatutHistoriqueRepository.save(historique);

        return toResponse(encadreurEnregistre);
    }

    private void remplirEncadreur(
            Encadreur encadreur,
            EncadreurRequest request,
            boolean creation
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Les informations de l'encadreur sont obligatoires"
            );
        }

        encadreur.setNom(
                nettoyerTexteObligatoire(request.getNom(), "Le nom est obligatoire", 250)
        );

        encadreur.setPrenoms(
                nettoyerTexteObligatoire(request.getPrenoms(), "Les prénoms sont obligatoires", 250)
        );

        encadreur.setFonction(
                nettoyerTexteOptionnel(request.getFonction(), 150)
        );

        encadreur.setLocalite(
                nettoyerTexteOptionnel(request.getLocalite(), 150)
        );

        encadreur.setPorte(
                nettoyerTexteOptionnel(request.getPorte(), 30)
        );

        encadreur.setContact(
                nettoyerTexteOptionnel(request.getContact(), 150)
        );

        encadreur.setCourriel(
                AssociationRules.email(nettoyerTexteOptionnel(request.getCourriel(), 150))
        );

        if (request.getCapaciteMaxStagiaires() != null
                && request.getCapaciteMaxStagiaires() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La capacité maximale doit être positive"
            );
        }

        encadreur.setCapaciteMaxStagiaires(
                request.getCapaciteMaxStagiaires()
        );

        if (request.getDepartementId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le département est obligatoire"
            );
        }

        Departement departement = departementRepository
                .findById(request.getDepartementId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Département introuvable"
                        )
                );

        if (encadreur.getDepartement() == null || AssociationRules.changed(encadreur.getDepartement().getId(), departement.getId()))
            AssociationRules.activeStatus(departement.getStatut(), "Département");
        encadreur.setDepartement(departement);

        if (!access.role("ADMIN")) access.require(java.util.Objects.equals(request.getUtilisateurId(), encadreur.getUtilisateur() == null ? null : encadreur.getUtilisateur().getId()));
        if (request.getUtilisateurId() != null) {
            Utilisateur utilisateur = utilisateurRepository
                    .findForUpdate(request.getUtilisateurId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Utilisateur introuvable"
                            )
                    );

            encadreurRepository.findByUtilisateur_Id(utilisateur.getId()).filter(other -> !java.util.Objects.equals(other.getId(), encadreur.getId()))
                    .ifPresent(other -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce compte est déjà associé à un encadreur"); });
            if (encadreur.getUtilisateur() == null || AssociationRules.changed(encadreur.getUtilisateur().getId(), utilisateur.getId())) {
                AssociationRules.activeStatus(utilisateur.getStatut(), "Compte utilisateur");
                if (roles.findByUtilisateur_Id(utilisateur.getId()).stream().noneMatch(r -> "ENCADREUR".equals(r.getRole().getCode()) && Boolean.TRUE.equals(r.getRole().getActif())))
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Un compte possédant le rôle ENCADREUR actif est requis");
            }
            encadreur.setUtilisateur(utilisateur);
        } else {
            encadreur.setUtilisateur(null);
        }
    }

    private EncadreurResponse toResponse(Encadreur encadreur) {
        Utilisateur utilisateur = encadreur.getUtilisateur();
        Departement departement = encadreur.getDepartement();

        String statut = encadreur.getStatut() != null
                ? encadreur.getStatut().getCode()
                : null;

        return new EncadreurResponse(
                encadreur.getId(),
                utilisateur != null ? utilisateur.getId() : null,
                utilisateur != null ? utilisateur.getIdentifiant() : null,
                encadreur.getNom(),
                encadreur.getPrenoms(),
                encadreur.getFonction(),
                encadreur.getLocalite(),
                encadreur.getPorte(),
                encadreur.getContact(),
                encadreur.getCourriel(),
                statut,
                departement != null ? departement.getId() : null,
                departement != null ? departement.getLibelle() : null,
                encadreur.getCapaciteMaxStagiaires()
        );
    }

    private String nettoyerTexteObligatoire(
            String valeur,
            String message,
            int tailleMax
    ) {
        if (valeur == null || valeur.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    message
            );
        }

        String texte = valeur.trim();

        if (texte.length() > tailleMax) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le texte est trop long"
            );
        }

        return texte;
    }

    private String nettoyerTexteOptionnel(
            String valeur,
            int tailleMax
    ) {
        if (valeur == null || valeur.trim().isEmpty()) {
            return null;
        }

        String texte = valeur.trim();

        if (texte.length() > tailleMax) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le texte est trop long"
            );
        }

        return texte;
    }
}
