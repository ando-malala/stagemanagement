package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.dto.referentiel.ReferentielRequest;
import mg.mef.stagemanagement.dto.referentiel.ReferentielResponse;
import mg.mef.stagemanagement.model.Etablissement;
import mg.mef.stagemanagement.model.Filiere;
import mg.mef.stagemanagement.model.MoisStage;
import mg.mef.stagemanagement.model.NiveauEtude;
import mg.mef.stagemanagement.repository.EtablissementRepository;
import mg.mef.stagemanagement.repository.FiliereRepository;
import mg.mef.stagemanagement.repository.MoisStageRepository;
import mg.mef.stagemanagement.repository.NiveauEtudeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReferentielAcademiqueService {

    private final EtablissementRepository etablissementRepository;
    private final FiliereRepository filiereRepository;
    private final NiveauEtudeRepository niveauEtudeRepository;
    private final MoisStageRepository moisStageRepository;

    public ReferentielAcademiqueService(
            EtablissementRepository etablissementRepository,
            FiliereRepository filiereRepository,
            NiveauEtudeRepository niveauEtudeRepository,
            MoisStageRepository moisStageRepository
    ) {
        this.etablissementRepository = etablissementRepository;
        this.filiereRepository = filiereRepository;
        this.niveauEtudeRepository = niveauEtudeRepository;
        this.moisStageRepository = moisStageRepository;
    }

    // =========================
    // ÉTABLISSEMENTS
    // =========================

    public List<ReferentielResponse> listerEtablissements() {
        return etablissementRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Etablissement::getLibelle))
                .map(e -> new ReferentielResponse(
                        e.getId(),
                        e.getLibelle(),
                        e.getActif()
                ))
                .toList();
    }

    @Transactional(readOnly = false)
    public ReferentielResponse creerEtablissement(ReferentielRequest request) {
        String libelle = nettoyerLibelle(request, 150);

        if (etablissementRepository.existsByLibelleIgnoreCase(libelle)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet établissement existe déjà"
            );
        }

        Etablissement etablissement = new Etablissement();
        etablissement.setLibelle(libelle);
        etablissement.setActif(
                request.getActif() == null || request.getActif()
        );

        Etablissement enregistre =
                etablissementRepository.save(etablissement);

        return new ReferentielResponse(
                enregistre.getId(),
                enregistre.getLibelle(),
                enregistre.getActif()
        );
    }

    @Transactional(readOnly = false)
    public ReferentielResponse modifierEtablissement(
            Integer id,
            ReferentielRequest request
    ) {
        Etablissement etablissement = etablissementRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Établissement introuvable"
                        )
                );

        String libelle = nettoyerLibelle(request, 150);

        etablissementRepository.findByLibelleIgnoreCase(libelle)
                .ifPresent(existant -> {
                    if (!existant.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Cet établissement existe déjà"
                        );
                    }
                });

        etablissement.setLibelle(libelle);

        if (request.getActif() != null) {
            etablissement.setActif(request.getActif());
        }

        Etablissement enregistre =
                etablissementRepository.save(etablissement);

        return new ReferentielResponse(
                enregistre.getId(),
                enregistre.getLibelle(),
                enregistre.getActif()
        );
    }

    // =========================
    // FILIÈRES
    // =========================

    public List<ReferentielResponse> listerFilieres() {
        return filiereRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Filiere::getLibelle))
                .map(f -> new ReferentielResponse(
                        f.getId(),
                        f.getLibelle(),
                        f.getActif()
                ))
                .toList();
    }

    @Transactional(readOnly = false)
    public ReferentielResponse creerFiliere(ReferentielRequest request) {
        String libelle = nettoyerLibelle(request, 150);

        if (filiereRepository.existsByLibelleIgnoreCase(libelle)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette filière existe déjà"
            );
        }

        Filiere filiere = new Filiere();
        filiere.setLibelle(libelle);
        filiere.setActif(
                request.getActif() == null || request.getActif()
        );

        Filiere enregistree = filiereRepository.save(filiere);

        return new ReferentielResponse(
                enregistree.getId(),
                enregistree.getLibelle(),
                enregistree.getActif()
        );
    }

    @Transactional(readOnly = false)
    public ReferentielResponse modifierFiliere(
            Integer id,
            ReferentielRequest request
    ) {
        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Filière introuvable"
                        )
                );

        String libelle = nettoyerLibelle(request, 150);

        filiereRepository.findByLibelleIgnoreCase(libelle)
                .ifPresent(existante -> {
                    if (!existante.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Cette filière existe déjà"
                        );
                    }
                });

        filiere.setLibelle(libelle);

        if (request.getActif() != null) {
            filiere.setActif(request.getActif());
        }

        Filiere enregistree = filiereRepository.save(filiere);

        return new ReferentielResponse(
                enregistree.getId(),
                enregistree.getLibelle(),
                enregistree.getActif()
        );
    }

    // =========================
    // NIVEAUX D'ÉTUDE
    // =========================

    public List<ReferentielResponse> listerNiveauxEtude() {
        return niveauEtudeRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(NiveauEtude::getLibelle))
                .map(n -> new ReferentielResponse(
                        n.getId(),
                        n.getLibelle(),
                        n.getActif()
                ))
                .toList();
    }

    @Transactional(readOnly = false)
    public ReferentielResponse creerNiveauEtude(ReferentielRequest request) {
        String libelle = nettoyerLibelle(request, 100);

        if (niveauEtudeRepository.existsByLibelleIgnoreCase(libelle)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce niveau d'étude existe déjà"
            );
        }

        NiveauEtude niveauEtude = new NiveauEtude();
        niveauEtude.setLibelle(libelle);
        niveauEtude.setActif(
                request.getActif() == null || request.getActif()
        );

        NiveauEtude enregistre =
                niveauEtudeRepository.save(niveauEtude);

        return new ReferentielResponse(
                enregistre.getId(),
                enregistre.getLibelle(),
                enregistre.getActif()
        );
    }

    @Transactional(readOnly = false)
    public ReferentielResponse modifierNiveauEtude(
            Integer id,
            ReferentielRequest request
    ) {
        NiveauEtude niveauEtude = niveauEtudeRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Niveau d'étude introuvable"
                        )
                );

        String libelle = nettoyerLibelle(request, 100);

        niveauEtudeRepository.findByLibelleIgnoreCase(libelle)
                .ifPresent(existant -> {
                    if (!existant.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Ce niveau d'étude existe déjà"
                        );
                    }
                });

        niveauEtude.setLibelle(libelle);

        if (request.getActif() != null) {
            niveauEtude.setActif(request.getActif());
        }

        NiveauEtude enregistre =
                niveauEtudeRepository.save(niveauEtude);

        return new ReferentielResponse(
                enregistre.getId(),
                enregistre.getLibelle(),
                enregistre.getActif()
        );
    }

    // =========================
    // MOIS DE STAGE
    // =========================

    public List<ReferentielResponse> listerMoisStage() {
        return moisStageRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(MoisStage::getId))
                .map(m -> new ReferentielResponse(
                        m.getId(),
                        m.getLibelle(),
                        m.getActif()
                ))
                .toList();
    }

    @Transactional(readOnly = false)
    public ReferentielResponse creerMoisStage(ReferentielRequest request) {
        String libelle = nettoyerLibelle(request, 50);

        if (moisStageRepository.existsByLibelleIgnoreCase(libelle)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce mois de stage existe déjà"
            );
        }

        MoisStage moisStage = new MoisStage();
        moisStage.setLibelle(libelle);
        moisStage.setActif(
                request.getActif() == null || request.getActif()
        );

        MoisStage enregistre =
                moisStageRepository.save(moisStage);

        return new ReferentielResponse(
                enregistre.getId(),
                enregistre.getLibelle(),
                enregistre.getActif()
        );
    }

    @Transactional(readOnly = false)
    public ReferentielResponse modifierMoisStage(
            Integer id,
            ReferentielRequest request
    ) {
        MoisStage moisStage = moisStageRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Mois de stage introuvable"
                        )
                );

        String libelle = nettoyerLibelle(request, 50);

        moisStageRepository.findByLibelleIgnoreCase(libelle)
                .ifPresent(existant -> {
                    if (!existant.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Ce mois de stage existe déjà"
                        );
                    }
                });

        moisStage.setLibelle(libelle);

        if (request.getActif() != null) {
            moisStage.setActif(request.getActif());
        }

        MoisStage enregistre =
                moisStageRepository.save(moisStage);

        return new ReferentielResponse(
                enregistre.getId(),
                enregistre.getLibelle(),
                enregistre.getActif()
        );
    }

    // =========================
    // UTILITAIRE
    // =========================

    private String nettoyerLibelle(ReferentielRequest request, int maxLength) {

        if (request == null
                || request.getLibelle() == null
                || request.getLibelle().trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le libellé est obligatoire"
            );
        }

        String libelle = request.getLibelle().trim();

        if (libelle.length() > maxLength) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le libellé est trop long"
            );
        }

        return libelle;
    }
}