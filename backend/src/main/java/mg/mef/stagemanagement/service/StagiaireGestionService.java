package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.dto.stagiaire.StagiaireAcademiqueRequest;
import mg.mef.stagemanagement.dto.stagiaire.StagiaireAcademiqueResponse;
import mg.mef.stagemanagement.dto.stagiaire.StagiaireRequest;
import mg.mef.stagemanagement.dto.stagiaire.StagiaireResponse;
import mg.mef.stagemanagement.model.Etablissement;
import mg.mef.stagemanagement.model.Filiere;
import mg.mef.stagemanagement.model.NiveauEtude;
import mg.mef.stagemanagement.model.Stagiaire;
import mg.mef.stagemanagement.model.StagiaireAcademique;
import mg.mef.stagemanagement.model.Statut;
import mg.mef.stagemanagement.repository.EtablissementRepository;
import mg.mef.stagemanagement.repository.FiliereRepository;
import mg.mef.stagemanagement.repository.NiveauEtudeRepository;
import mg.mef.stagemanagement.repository.StagiaireAcademiqueRepository;
import mg.mef.stagemanagement.repository.StagiaireRepository;
import mg.mef.stagemanagement.repository.StatutRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StagiaireGestionService {
    private final DossierAccessService access;

    private final StagiaireRepository stagiaireRepository;
    private final StagiaireAcademiqueRepository stagiaireAcademiqueRepository;
    private final StatutRepository statutRepository;
    private final NiveauEtudeRepository niveauEtudeRepository;
    private final FiliereRepository filiereRepository;
    private final EtablissementRepository etablissementRepository;

    public StagiaireGestionService(
            StagiaireRepository stagiaireRepository,
            StagiaireAcademiqueRepository stagiaireAcademiqueRepository,
            StatutRepository statutRepository,
            NiveauEtudeRepository niveauEtudeRepository,
            FiliereRepository filiereRepository,
            EtablissementRepository etablissementRepository
    , DossierAccessService access) {
        this.access = access;
        this.stagiaireRepository = stagiaireRepository;
        this.stagiaireAcademiqueRepository = stagiaireAcademiqueRepository;
        this.statutRepository = statutRepository;
        this.niveauEtudeRepository = niveauEtudeRepository;
        this.filiereRepository = filiereRepository;
        this.etablissementRepository = etablissementRepository;
    }

    // =========================
    // STAGIAIRES
    // =========================

    public List<StagiaireResponse> listerStagiaires() {
        return stagiaireRepository.findAll()
                .stream().filter(access::readTrainee)
                .sorted(Comparator.comparing(Stagiaire::getNom)
                        .thenComparing(Stagiaire::getPrenoms))
                .map(this::toStagiaireResponse)
                .toList();
    }

    public StagiaireResponse trouverStagiaire(Integer id) {
        access.trainee(id, false);
        Stagiaire stagiaire = stagiaireRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Stagiaire introuvable"
                        )
                );

        return toStagiaireResponse(stagiaire);
    }

    @Transactional(readOnly = false)
    public StagiaireResponse creerStagiaire(StagiaireRequest request) {
        Stagiaire stagiaire = new Stagiaire();

        remplirStagiaire(stagiaire, request);
        access.require(access.role("ADMIN"));

        Statut statutActif = statutRepository
                .findStatutByTypeCodeAndStatutCode("STAGIAIRE", "ACTIF")
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Statut ACTIF des stagiaires introuvable"
                        )
                );

        stagiaire.setStatut(statutActif);
        stagiaire.setDateCreation(LocalDateTime.now());

        Stagiaire enregistre = stagiaireRepository.save(stagiaire);

        return toStagiaireResponse(enregistre);
    }

    @Transactional(readOnly = false)
    public StagiaireResponse modifierStagiaire(
            Integer id,
            StagiaireRequest request
    ) {
        access.trainee(id, true);
        Stagiaire stagiaire = stagiaireRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Stagiaire introuvable"
                        )
                );

        remplirStagiaire(stagiaire, request);
        access.require(access.role("ADMIN"));

        Stagiaire enregistre = stagiaireRepository.save(stagiaire);

        return toStagiaireResponse(enregistre);
    }

    // =========================
    // ACADÉMIQUE
    // =========================

    public List<StagiaireAcademiqueResponse> listerAcademiques(
            Integer stagiaireId
    ) {
        access.trainee(stagiaireId, false);
        verifierStagiaireExiste(stagiaireId);

        return stagiaireAcademiqueRepository
                .findByStagiaire_Id(stagiaireId)
                .stream()
                .map(this::toAcademiqueResponse)
                .toList();
    }

    @Transactional(readOnly = false)
    public StagiaireAcademiqueResponse ajouterAcademique(
            Integer stagiaireId,
            StagiaireAcademiqueRequest request
    ) {
        access.trainee(stagiaireId, true);
        Stagiaire stagiaire = stagiaireRepository.findById(stagiaireId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Stagiaire introuvable"
                        )
                );

        if (request == null
                || request.getNiveauEtudeId() == null
                || request.getFiliereId() == null
                || request.getEtablissementId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Les informations académiques sont incomplètes"
            );
        }

        String anneeEtudes = nettoyerAnneeEtudes(request.getAnneeEtudes());

        boolean existeDeja =
                stagiaireAcademiqueRepository
                        .existsByStagiaire_IdAndNiveauEtude_IdAndFiliere_IdAndEtablissement_IdAndAnneeEtudes(
                                stagiaireId,
                                request.getNiveauEtudeId(),
                                request.getFiliereId(),
                                request.getEtablissementId(),
                                anneeEtudes
                        );

        if (existeDeja) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette information académique existe déjà pour ce stagiaire"
            );
        }

        NiveauEtude niveauEtude = niveauEtudeRepository
                .findById(request.getNiveauEtudeId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Niveau d'étude introuvable"
                        )
                );

        Filiere filiere = filiereRepository
                .findById(request.getFiliereId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Filière introuvable"
                        )
                );

        Etablissement etablissement = etablissementRepository
                .findById(request.getEtablissementId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Établissement introuvable"
                        )
                );

        StagiaireAcademique academique = new StagiaireAcademique();

        academique.setStagiaire(stagiaire);
        academique.setNiveauEtude(niveauEtude);
        academique.setFiliere(filiere);
        academique.setEtablissement(etablissement);
        academique.setAnneeEtudes(anneeEtudes);

        StagiaireAcademique enregistre =
                stagiaireAcademiqueRepository.save(academique);

        return toAcademiqueResponse(enregistre);
    }

    // =========================
    // MAPPERS
    // =========================

    private StagiaireResponse toStagiaireResponse(Stagiaire stagiaire) {
        String statut = stagiaire.getStatut() != null
                ? stagiaire.getStatut().getCode()
                : null;

        var response = new StagiaireResponse(
                stagiaire.getId(),
                stagiaire.getNom(),
                stagiaire.getPrenoms(),
                stagiaire.getAdresse(),
                stagiaire.getContact(),
                stagiaire.getEmail(),
                statut,
                stagiaire.getDateCreation()
        );
        return response;
    }

    private StagiaireAcademiqueResponse toAcademiqueResponse(
            StagiaireAcademique academique
    ) {
        return new StagiaireAcademiqueResponse(
                academique.getId(),
                academique.getStagiaire().getId(),
                academique.getNiveauEtude().getId(),
                academique.getNiveauEtude().getLibelle(),
                academique.getFiliere().getId(),
                academique.getFiliere().getLibelle(),
                academique.getEtablissement().getId(),
                academique.getEtablissement().getLibelle(),
                academique.getAnneeEtudes()
        );
    }

    // =========================
    // UTILITAIRES
    // =========================

    private void remplirStagiaire(
            Stagiaire stagiaire,
            StagiaireRequest request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Les informations du stagiaire sont obligatoires"
            );
        }

        stagiaire.setNom(
                nettoyerTexteObligatoire(request.getNom(), "Le nom est obligatoire", 150)
        );

        stagiaire.setPrenoms(
                nettoyerTexteObligatoire(request.getPrenoms(), "Les prénoms sont obligatoires", 250)
        );

        stagiaire.setAdresse(
                nettoyerTexteObligatoire(request.getAdresse(), "L'adresse est obligatoire", 250)
        );

        stagiaire.setContact(
                nettoyerTexteOptionnel(request.getContact(), 100)
        );

        stagiaire.setEmail(
                nettoyerTexteOptionnel(request.getEmail(), 150)
        );
    }

    private void verifierStagiaireExiste(Integer stagiaireId) {
        if (!stagiaireRepository.existsById(stagiaireId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Stagiaire introuvable"
            );
        }
    }

    private String nettoyerAnneeEtudes(String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'année d'études est obligatoire"
            );
        }

        String annee = valeur.trim();

        if (!annee.matches("\\d{4}-\\d{4}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'année d'études doit être au format 2025-2026"
            );
        }

        return annee;
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
