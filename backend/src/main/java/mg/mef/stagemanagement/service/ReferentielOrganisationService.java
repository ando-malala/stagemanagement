package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.dto.referentiel.CadreStageRequest;
import mg.mef.stagemanagement.dto.referentiel.CadreStageResponse;
import mg.mef.stagemanagement.dto.referentiel.DepartementRequest;
import mg.mef.stagemanagement.dto.referentiel.DepartementResponse;
import mg.mef.stagemanagement.dto.referentiel.StructureAccueilRequest;
import mg.mef.stagemanagement.dto.referentiel.StructureAccueilResponse;
import mg.mef.stagemanagement.model.CadreStage;
import mg.mef.stagemanagement.model.Departement;
import mg.mef.stagemanagement.model.Statut;
import mg.mef.stagemanagement.model.StructureAccueil;
import mg.mef.stagemanagement.repository.CadreStageRepository;
import mg.mef.stagemanagement.repository.DepartementRepository;
import mg.mef.stagemanagement.repository.StatutRepository;
import mg.mef.stagemanagement.repository.StructureAccueilRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import mg.mef.stagemanagement.model.Statut;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReferentielOrganisationService {
    private final DossierAccessService access;

    private final DepartementRepository departementRepository;
    private final StatutRepository statutRepository;
    private final StructureAccueilRepository structureAccueilRepository;
    private final CadreStageRepository cadreStageRepository;

    public ReferentielOrganisationService(
            DepartementRepository departementRepository,
            StatutRepository statutRepository,
            StructureAccueilRepository structureAccueilRepository,
            CadreStageRepository cadreStageRepository
    , DossierAccessService access) {
        this.access = access;
        this.departementRepository = departementRepository;
        this.statutRepository = statutRepository;
        this.structureAccueilRepository = structureAccueilRepository;
        this.cadreStageRepository = cadreStageRepository;
    }

    // =========================
    // DÉPARTEMENTS
    // =========================

    public List<DepartementResponse> listerDepartements() {
        return departementRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Departement::getLibelle))
                .map(this::toDepartementResponse)
                .toList();
    }

    @Transactional(readOnly = false)
    public DepartementResponse creerDepartement(DepartementRequest request) {
        String libelle = nettoyerTexte(request.getLibelle(), "Le libellé est obligatoire", 250);

        if (departementRepository.existsByLibelleIgnoreCase(libelle)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce département existe déjà"
            );
        }

        Statut statut = trouverStatutDepartement(
                request.getStatutCode() == null
                        ? "ACTIF"
                        : request.getStatutCode()
        );

        Departement departement = new Departement();
        departement.setLibelle(libelle);
        departement.setStatut(statut);

        Departement enregistre = departementRepository.save(departement);

        return toDepartementResponse(enregistre);
    }

    @Transactional(readOnly = false)
    public DepartementResponse modifierDepartement(
            Integer id,
            DepartementRequest request
    ) {
        Departement departement = departementRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Département introuvable"
                        )
                );

        String libelle = nettoyerTexte(request.getLibelle(), "Le libellé est obligatoire", 250);

        departementRepository.findByLibelleIgnoreCase(libelle)
                .ifPresent(existant -> {
                    if (!existant.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Ce département existe déjà"
                        );
                    }
                });

        departement.setLibelle(libelle);

        if (request.getStatutCode() != null
                && !request.getStatutCode().trim().isEmpty()) {

            departement.setStatut(
                    trouverStatutDepartement(request.getStatutCode())
            );
        }

        Departement enregistre = departementRepository.save(departement);

        return toDepartementResponse(enregistre);
    }

    // =========================
    // STRUCTURES D'ACCUEIL
    // =========================

    public List<StructureAccueilResponse> listerStructuresAccueil() {
        return structureAccueilRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(StructureAccueil::getLibelle))
                .map(this::toStructureAccueilResponse)
                .toList();
    }

    @Transactional(readOnly = false)
    public StructureAccueilResponse creerStructureAccueil(
            StructureAccueilRequest request
    ) {
        if (request == null || request.getDepartementId() == null) {
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

        String libelle = nettoyerTexte(request.getLibelle(), "Le libellé est obligatoire", 150);

        if (structureAccueilRepository
                .existsByDepartement_IdAndLibelleIgnoreCase(departement.getId(), libelle)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette structure existe déjà dans ce département"
            );
        }

        StructureAccueil structureAccueil = new StructureAccueil();
        structureAccueil.setDepartement(departement);
        structureAccueil.setLibelle(libelle);
        structureAccueil.setLocalite(nettoyerTexteOptionnel(request.getLocalite(), 150));
        structureAccueil.setPorte(nettoyerTexteOptionnel(request.getPorte(), 30));
        structureAccueil.setContact(nettoyerTexteOptionnel(request.getContact(), 150));
        structureAccueil.setCourriel(nettoyerTexteOptionnel(request.getCourriel(), 150));
        structureAccueil.setActif(request.getActif() == null || request.getActif());

        StructureAccueil enregistree =
                structureAccueilRepository.save(structureAccueil);

        return toStructureAccueilResponse(enregistree);
    }

    @Transactional(readOnly = false)
    public StructureAccueilResponse modifierStructureAccueil(
            Integer id,
            StructureAccueilRequest request
    ) {
        StructureAccueil structureAccueil = structureAccueilRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Structure d'accueil introuvable"
                        )
                );

        if (request == null || request.getDepartementId() == null) {
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

        String libelle = nettoyerTexte(request.getLibelle(), "Le libellé est obligatoire", 150);

        structureAccueilRepository
                .findByDepartement_IdAndLibelleIgnoreCase(departement.getId(), libelle)
                .ifPresent(existante -> {
                    if (!existante.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Cette structure existe déjà dans ce département"
                        );
                    }
                });

        structureAccueil.setDepartement(departement);
        structureAccueil.setLibelle(libelle);
        structureAccueil.setLocalite(nettoyerTexteOptionnel(request.getLocalite(), 150));
        structureAccueil.setPorte(nettoyerTexteOptionnel(request.getPorte(), 30));
        structureAccueil.setContact(nettoyerTexteOptionnel(request.getContact(), 150));
        structureAccueil.setCourriel(nettoyerTexteOptionnel(request.getCourriel(), 150));

        if (request.getActif() != null) {
            structureAccueil.setActif(request.getActif());
        }

        StructureAccueil enregistree =
                structureAccueilRepository.save(structureAccueil);

        return toStructureAccueilResponse(enregistree);
    }

    // =========================
    // CADRES DE STAGE
    // =========================

    public List<CadreStageResponse> listerCadresStage() {
        return cadreStageRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(CadreStage::getThemeStage))
                .map(this::toCadreStageResponse)
                .toList();
    }

    @Transactional(readOnly = false)
    public CadreStageResponse creerCadreStage(
            CadreStageRequest request
    ) {
        String theme = nettoyerTexte(request.getThemeStage(), "Le thème est obligatoire", 250);

        CadreStage cadreStage = new CadreStage();
        cadreStage.setThemeStage(theme);
        cadreStage.setActiviteConfieStagiaire(
                nettoyerTexteLongOptionnel(request.getActiviteConfieStagiaire())
        );
        cadreStage.setObjectifStage(
                nettoyerTexteLongOptionnel(request.getObjectifStage())
        );
        cadreStage.setActif(request.getActif() == null || request.getActif());

        CadreStage enregistre =
                cadreStageRepository.save(cadreStage);

        return toCadreStageResponse(enregistre);
    }

    @Transactional(readOnly = false)
    public CadreStageResponse modifierCadreStage(
            Integer id,
            CadreStageRequest request
    ) {
        CadreStage cadreStage = cadreStageRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Cadre de stage introuvable"
                        )
                );

        String theme = nettoyerTexte(request.getThemeStage(), "Le thème est obligatoire", 250);

        cadreStage.setThemeStage(theme);
        cadreStage.setActiviteConfieStagiaire(
                nettoyerTexteLongOptionnel(request.getActiviteConfieStagiaire())
        );
        cadreStage.setObjectifStage(
                nettoyerTexteLongOptionnel(request.getObjectifStage())
        );

        if (request.getActif() != null) {
            cadreStage.setActif(request.getActif());
        }

        CadreStage enregistre =
                cadreStageRepository.save(cadreStage);

        return toCadreStageResponse(enregistre);
    }

    // =========================
    // MAPPERS
    // =========================

    private DepartementResponse toDepartementResponse(Departement departement) {
        String statut = departement.getStatut() != null
                ? departement.getStatut().getCode()
                : null;

        return new DepartementResponse(
                departement.getId(),
                departement.getLibelle(),
                statut
        );
    }

    private StructureAccueilResponse toStructureAccueilResponse(
            StructureAccueil structureAccueil
    ) {
        Departement departement = structureAccueil.getDepartement();

        return new StructureAccueilResponse(
                structureAccueil.getId(),
                departement != null ? departement.getId() : null,
                departement != null ? departement.getLibelle() : null,
                structureAccueil.getLibelle(),
                structureAccueil.getLocalite(),
                structureAccueil.getPorte(),
                structureAccueil.getContact(),
                structureAccueil.getCourriel(),
                structureAccueil.getActif()
        );
    }

    private CadreStageResponse toCadreStageResponse(CadreStage cadreStage) {
        return new CadreStageResponse(
                cadreStage.getId(),
                cadreStage.getThemeStage(),
                cadreStage.getActiviteConfieStagiaire(),
                cadreStage.getObjectifStage(),
                cadreStage.getActif()
        );
    }

    // =========================
    // UTILITAIRES
    // =========================

    private Statut trouverStatutDepartement(String code) {
        String codeNettoye = code.trim().toUpperCase();

        return statutRepository
                .findStatutByTypeCodeAndStatutCode(
                        "DEPARTEMENT",
                        codeNettoye
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Statut de département invalide"
                        )
                );
    }

    private String nettoyerTexte(
            String valeur,
            String messageErreur,
            int tailleMax
    ) {
        if (valeur == null || valeur.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    messageErreur
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

    private String nettoyerTexteOptionnel(String valeur, int tailleMax) {
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

    private String nettoyerTexteLongOptionnel(String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) {
            return null;
        }

        return valeur.trim();
    }
}
