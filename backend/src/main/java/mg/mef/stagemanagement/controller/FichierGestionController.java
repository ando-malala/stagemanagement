package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.dto.auth.AuthMeResponse;
import mg.mef.stagemanagement.dto.fichier.FichierResponse;
import mg.mef.stagemanagement.dto.fichier.FichierTelechargementResponse;
import mg.mef.stagemanagement.service.FichierGestionService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FichierGestionController {

    private final FichierGestionService fichierGestionService;

    public FichierGestionController(
            FichierGestionService fichierGestionService
    ) {
        this.fichierGestionService = fichierGestionService;
    }

    // =========================
    // FICHIERS STAGIAIRE
    // =========================

    @GetMapping("/stagiaires/{id}/fichiers")
    public ResponseEntity<List<FichierResponse>> listerFichiersStagiaire(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(
                fichierGestionService.listerFichiersStagiaire(id)
        );
    }

    @PostMapping(
            value = "/stagiaires/{id}/fichiers",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<FichierResponse> ajouterFichierStagiaire(
            @PathVariable Integer id,
            @RequestParam("typePieceJointeCode") String typePieceJointeCode,
            @RequestParam("fichier") MultipartFile fichier,
            @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
    ) {
        return ResponseEntity.ok(
                fichierGestionService.ajouterFichierStagiaire(
                        id,
                        typePieceJointeCode,
                        fichier,
                        utilisateurConnecte.getUtilisateurId()
                )
        );
    }

    // =========================
    // FICHIERS PARTICIPATION
    // =========================

    @GetMapping("/stage-participants/{id}/fichiers")
    public ResponseEntity<List<FichierResponse>> listerFichiersParticipation(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(
                fichierGestionService.listerFichiersParticipation(id)
        );
    }

    @PostMapping(
            value = "/stage-participants/{id}/fichiers",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<FichierResponse> ajouterFichierParticipation(
            @PathVariable Integer id,
            @RequestParam("typePieceJointeCode") String typePieceJointeCode,
            @RequestParam("fichier") MultipartFile fichier,
            @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
    ) {
        return ResponseEntity.ok(
                fichierGestionService.ajouterFichierParticipation(
                        id,
                        typePieceJointeCode,
                        fichier,
                        utilisateurConnecte.getUtilisateurId()
                )
        );
    }

    // =========================
    // TÉLÉCHARGEMENT
    // =========================

    @GetMapping("/fichiers/{id}/telecharger")
    public ResponseEntity<Resource> telechargerFichier(
            @PathVariable Integer id
    ) {
        FichierTelechargementResponse fichier =
                fichierGestionService.preparerTelechargement(id);

        String nomOriginal = fichier.getNomOriginal()
                .replace("\"", "")
                .replace("\n", "")
                .replace("\r", "");

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

        if (fichier.getTypeMime() != null
                && !fichier.getTypeMime().trim().isEmpty()) {
            mediaType = MediaType.parseMediaType(fichier.getTypeMime());
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + nomOriginal + "\""
                )
                .body(fichier.getResource());
    }
}