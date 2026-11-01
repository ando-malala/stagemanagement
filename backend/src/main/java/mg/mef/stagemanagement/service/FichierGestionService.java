package mg.mef.stagemanagement.service;

import mg.mef.stagemanagement.dto.fichier.FichierResponse;
import mg.mef.stagemanagement.dto.fichier.FichierTelechargementResponse;
import mg.mef.stagemanagement.model.Fichier;
import mg.mef.stagemanagement.model.StageParticipant;
import mg.mef.stagemanagement.model.StageParticipantFichier;
import mg.mef.stagemanagement.model.Stagiaire;
import mg.mef.stagemanagement.model.StagiaireFichier;
import mg.mef.stagemanagement.model.TypePieceJointe;
import mg.mef.stagemanagement.model.Utilisateur;
import mg.mef.stagemanagement.repository.FichierRepository;
import mg.mef.stagemanagement.repository.StageParticipantFichierRepository;
import mg.mef.stagemanagement.repository.StageParticipantRepository;
import mg.mef.stagemanagement.repository.StagiaireFichierRepository;
import mg.mef.stagemanagement.repository.StagiaireRepository;
import mg.mef.stagemanagement.repository.TypePieceJointeRepository;
import mg.mef.stagemanagement.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FichierGestionService {
    private final DossierAccessService access;
    private final DocumentContentValidator validator;
    private final ParticipationRulesService rules;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private final FichierRepository fichierRepository;
    private final StagiaireRepository stagiaireRepository;
    private final StageParticipantRepository stageParticipantRepository;
    private final StagiaireFichierRepository stagiaireFichierRepository;
    private final StageParticipantFichierRepository stageParticipantFichierRepository;
    private final TypePieceJointeRepository typePieceJointeRepository;
    private final UtilisateurRepository utilisateurRepository;

    public FichierGestionService(
            FichierRepository fichierRepository,
            StagiaireRepository stagiaireRepository,
            StageParticipantRepository stageParticipantRepository,
            StagiaireFichierRepository stagiaireFichierRepository,
            StageParticipantFichierRepository stageParticipantFichierRepository,
            TypePieceJointeRepository typePieceJointeRepository,
            UtilisateurRepository utilisateurRepository
    , DossierAccessService access, ParticipationRulesService rules, DocumentContentValidator validator) {
        this.access = access; this.rules = rules; this.validator = validator;
        this.fichierRepository = fichierRepository;
        this.stagiaireRepository = stagiaireRepository;
        this.stageParticipantRepository = stageParticipantRepository;
        this.stagiaireFichierRepository = stagiaireFichierRepository;
        this.stageParticipantFichierRepository = stageParticipantFichierRepository;
        this.typePieceJointeRepository = typePieceJointeRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // =========================
    // FICHIERS STAGIAIRE
    // =========================

    public List<FichierResponse> listerFichiersStagiaire(Integer stagiaireId) {
        access.trainee(stagiaireId, false);
        if (!stagiaireRepository.existsById(stagiaireId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Stagiaire introuvable"
            );
        }

        return stagiaireFichierRepository.findByStagiaire_Id(stagiaireId)
                .stream()
                .filter(link -> access.readFile(link.getFichier().getId()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = false)
    public FichierResponse ajouterFichierStagiaire(
            Integer stagiaireId,
            String typePieceJointeCode,
            MultipartFile multipartFile,
            Integer utilisateurId
    ) {
        access.trainee(stagiaireId, true);
        Stagiaire stagiaire = stagiaireRepository.findForUpdate(stagiaireId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Stagiaire introuvable"
                        )
                );

        TypePieceJointe typePieceJointe =
                trouverTypePieceJointe(typePieceJointeCode);

        if (!Boolean.TRUE.equals(typePieceJointe.getPourStagiaire())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ce type de pièce jointe n'est pas autorisé pour un stagiaire"
            );
        }

        verifierMultipleStagiaire(stagiaireId, typePieceJointe);

        Utilisateur utilisateur = trouverUtilisateur(utilisateurId);

        Fichier fichier = enregistrerFichierPhysiqueEtBase(
                multipartFile,
                utilisateur
        );

        StagiaireFichier stagiaireFichier = new StagiaireFichier();
        stagiaireFichier.setStagiaire(stagiaire);
        stagiaireFichier.setFichier(fichier);
        stagiaireFichier.setTypePieceJointe(typePieceJointe);
        stagiaireFichier.setDateAssociation(LocalDateTime.now());

        StagiaireFichier enregistre =
                stagiaireFichierRepository.save(stagiaireFichier);

        return toResponse(enregistre);
    }

    // =========================
    // FICHIERS PARTICIPATION
    // =========================

    public List<FichierResponse> listerFichiersParticipation(
            Integer stageParticipantId
    ) {
        access.participation(stageParticipantId, false);
        if (!stageParticipantRepository.existsById(stageParticipantId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Participation introuvable"
            );
        }

        return stageParticipantFichierRepository
                .findByStageParticipant_Id(stageParticipantId)
                .stream()
                .filter(link -> access.readFile(link.getFichier().getId()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = false)
    public FichierResponse ajouterFichierParticipation(
            Integer stageParticipantId,
            String typePieceJointeCode,
            MultipartFile multipartFile,
            Integer utilisateurId
    ) {
        access.participation(stageParticipantId, true);
        StageParticipant stageParticipant =
                stageParticipantRepository.findForUpdate(stageParticipantId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Participation introuvable"
                                )
                        );

        rules.lock(stageParticipant);
        TypePieceJointe typePieceJointe =
                trouverTypePieceJointe(typePieceJointeCode);

        if (!Boolean.TRUE.equals(typePieceJointe.getPourParticipation())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ce type de pièce jointe n'est pas autorisé pour une participation"
            );
        }

        verifierMultipleParticipation(stageParticipantId, typePieceJointe);

        Utilisateur utilisateur = trouverUtilisateur(utilisateurId);

        Fichier fichier = enregistrerFichierPhysiqueEtBase(
                multipartFile,
                utilisateur
        );

        StageParticipantFichier stageParticipantFichier =
                new StageParticipantFichier();

        stageParticipantFichier.setStageParticipant(stageParticipant);
        stageParticipantFichier.setFichier(fichier);
        stageParticipantFichier.setTypePieceJointe(typePieceJointe);
        stageParticipantFichier.setDateAssociation(LocalDateTime.now());

        StageParticipantFichier enregistre =
                stageParticipantFichierRepository.save(stageParticipantFichier);

        return toResponse(enregistre);
    }

    // =========================
    // TÉLÉCHARGEMENT
    // =========================

    public FichierTelechargementResponse preparerTelechargement(
            Integer fichierId
    ) {
        access.file(fichierId);
        Fichier fichier = fichierRepository.findById(fichierId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Fichier introuvable"
                        )
                );

        Path chemin = Paths.get(fichier.getChemin()).normalize();

        if (!Files.exists(chemin) || !Files.isReadable(chemin)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Fichier physique introuvable"
            );
        }

        try {
            if (!chemin.toRealPath().startsWith(Paths.get(uploadDir).toRealPath()))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fichier hors du stockage autorise");
        } catch (IOException failure) { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier indisponible"); }
        Resource resource = new FileSystemResource(chemin);

        return new FichierTelechargementResponse(
                resource,
                fichier.getNomOriginal(),
                fichier.getTypeMime()
        );
    }

    // =========================
    // MÉTHODES PRIVÉES
    // =========================

    private Fichier enregistrerFichierPhysiqueEtBase(
            MultipartFile multipartFile,
            Utilisateur utilisateur
    ) {
        var content = validator.validate(multipartFile);
        String nomOriginal = content.filename();
        String nomStockage = UUID.randomUUID() + content.extension();

        try {
            Path dossierUpload = Paths.get(uploadDir)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(dossierUpload);

            Path destination = dossierUpload
                    .resolve(nomStockage)
                    .normalize();

            if (!destination.startsWith(dossierUpload)) throw new IOException("Invalid storage path");
            Files.write(destination, content.bytes(), java.nio.file.StandardOpenOption.CREATE_NEW);
            org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                new org.springframework.transaction.support.TransactionSynchronization() {
                    @Override public void afterCompletion(int status) {
                        if (status != STATUS_COMMITTED) try { Files.deleteIfExists(destination); }
                        catch (IOException failure) { org.slf4j.LoggerFactory.getLogger(FichierGestionService.class).error("Document temporaire non nettoye apres echec transactionnel"); }
                    }
                });

            Fichier fichier = new Fichier();
            fichier.setNomOriginal(nomOriginal);
            fichier.setNomStockage(nomStockage);
            fichier.setChemin(destination.toString());
            fichier.setTypeMime(content.mime());
            fichier.setTailleOctets(multipartFile.getSize());
            fichier.setUtilisateur(utilisateur);
            fichier.setDateAjout(LocalDateTime.now());

            return fichierRepository.save(fichier);

        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de l'enregistrement du fichier"
            );
        }
    }

    private TypePieceJointe trouverTypePieceJointe(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le type de pièce jointe est obligatoire"
            );
        }

        return typePieceJointeRepository
                .findByCodeIgnoreCaseAndActifTrue(code.trim())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Type de pièce jointe introuvable"
                        )
                );
    }

    private Utilisateur trouverUtilisateur(Integer utilisateurId) {
        return utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Utilisateur connecté introuvable"
                        )
                );
    }

    private void verifierMultipleStagiaire(
            Integer stagiaireId,
            TypePieceJointe typePieceJointe
    ) {
        if (!Boolean.TRUE.equals(typePieceJointe.getMultipleAutorise())) {
            boolean existeDeja =
                    stagiaireFichierRepository
                            .existsByStagiaire_IdAndTypePieceJointe_Id(
                                    stagiaireId,
                                    typePieceJointe.getId()
                            );

            if (existeDeja) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Ce type de fichier existe déjà pour ce stagiaire"
                );
            }
        }
    }

    private void verifierMultipleParticipation(
            Integer stageParticipantId,
            TypePieceJointe typePieceJointe
    ) {
        if (!Boolean.TRUE.equals(typePieceJointe.getMultipleAutorise())) {
            boolean existeDeja =
                    stageParticipantFichierRepository
                            .existsByStageParticipant_IdAndTypePieceJointe_Id(
                                    stageParticipantId,
                                    typePieceJointe.getId()
                            );

            if (existeDeja) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Ce type de fichier existe déjà pour cette participation"
                );
            }
        }
    }

    private String extraireExtension(String nomOriginal) {
        String nom = Paths.get(nomOriginal).getFileName().toString();
        int index = nom.lastIndexOf(".");

        if (index < 0) {
            return "";
        }

        String extension = nom.substring(index).toLowerCase();

        if (!extension.matches("\\.[a-z0-9]{1,10}")) {
            return "";
        }

        return extension;
    }

    private FichierResponse toResponse(StagiaireFichier stagiaireFichier) {
        Fichier fichier = stagiaireFichier.getFichier();
        TypePieceJointe typePieceJointe =
                stagiaireFichier.getTypePieceJointe();

        return new FichierResponse(
                fichier.getId(),
                fichier.getNomOriginal(),
                fichier.getTypeMime(),
                fichier.getTailleOctets(),
                typePieceJointe.getCode(),
                typePieceJointe.getLibelle(),
                fichier.getDateAjout(),
                stagiaireFichier.getDateAssociation()
        );
    }

    private FichierResponse toResponse(
            StageParticipantFichier stageParticipantFichier
    ) {
        Fichier fichier = stageParticipantFichier.getFichier();
        TypePieceJointe typePieceJointe =
                stageParticipantFichier.getTypePieceJointe();

        return new FichierResponse(
                fichier.getId(),
                fichier.getNomOriginal(),
                fichier.getTypeMime(),
                fichier.getTailleOctets(),
                typePieceJointe.getCode(),
                typePieceJointe.getLibelle(),
                fichier.getDateAjout(),
                stageParticipantFichier.getDateAssociation()
        );
    }
}
