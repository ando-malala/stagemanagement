package mg.mef.stagemanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.mef.stagemanagement.dto.auth.AuthMeResponse;
import mg.mef.stagemanagement.dto.utilisateur.AjouterDepartementUtilisateurRequest;
import mg.mef.stagemanagement.dto.utilisateur.AjouterRoleUtilisateurRequest;
import mg.mef.stagemanagement.dto.utilisateur.ChangerStatutUtilisateurRequest;
import mg.mef.stagemanagement.dto.utilisateur.CreerUtilisateurRequest;
import mg.mef.stagemanagement.dto.utilisateur.DepartementUtilisateurResponse;
import mg.mef.stagemanagement.dto.utilisateur.ReinitialiserMotDePasseRequest;
import mg.mef.stagemanagement.dto.utilisateur.UtilisateurListResponse;
import mg.mef.stagemanagement.service.UtilisateurGestionService;
import mg.mef.stagemanagement.service.UtilisateurService;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

        private final UtilisateurService utilisateurService;
        private final UtilisateurGestionService utilisateurGestionService;

        public UtilisateurController(
                UtilisateurService utilisateurService,
                UtilisateurGestionService utilisateurGestionService
        ) {
                this.utilisateurService = utilisateurService;
                this.utilisateurGestionService = utilisateurGestionService;
        }

        @GetMapping
        public ResponseEntity<List<UtilisateurListResponse>> findAll() {

                return ResponseEntity.ok(
                        utilisateurService.listerUtilisateurs()
                );
        }

        @GetMapping("/{id}")
        public ResponseEntity<UtilisateurListResponse> findById(
                @PathVariable Integer id
        ) {

                return ResponseEntity.ok(
                        utilisateurGestionService.trouverUtilisateur(id)
                );
        }

        @PostMapping
        public ResponseEntity<UtilisateurListResponse> create(
                @RequestBody CreerUtilisateurRequest request
        ) {

                UtilisateurListResponse utilisateur =
                        utilisateurGestionService.creerUtilisateur(request);

                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(utilisateur);
        }

        @PatchMapping("/{id}/statut")
        public ResponseEntity<UtilisateurListResponse> changerStatut(
                @PathVariable Integer id,
                @RequestBody ChangerStatutUtilisateurRequest request,
                @AuthenticationPrincipal AuthMeResponse utilisateurConnecte
        ) {
                UtilisateurListResponse utilisateur = 
                        utilisateurGestionService.changerStatut(
                                id,
                                request,
                                utilisateurConnecte.getUtilisateurId()
                        );
                
                return ResponseEntity.ok(utilisateur);
        }

        @PatchMapping("/{id}/mot-de-passe")
        public ResponseEntity<Void> reinitialiserMotDePasse(
                @PathVariable Integer id,
                @RequestBody ReinitialiserMotDePasseRequest request
        ) {

                utilisateurGestionService.reinitialiserMotDePasse(
                        id,
                        request
                );

                return ResponseEntity.noContent().build();
        }

        @GetMapping("/{id}/roles")
        public ResponseEntity<List<String>> listerRoles(
                @PathVariable Integer id
        ) {

                return ResponseEntity.ok(
                        utilisateurGestionService.listerRolesUtilisateur(id)
                );
        }

        @PostMapping("/{id}/roles")
        public ResponseEntity<List<String>> ajouterRole(
                @PathVariable Integer id,
                @RequestBody AjouterRoleUtilisateurRequest request
        ) {

                return ResponseEntity.ok(
                        utilisateurGestionService.ajouterRole(id, request)
                );
        }

        @DeleteMapping("/{id}/roles/{roleCode}")
        public ResponseEntity<List<String>> retirerRole(
                @PathVariable Integer id,
                @PathVariable String roleCode
        ) {

                return ResponseEntity.ok(
                        utilisateurGestionService.retirerRole(id, roleCode)
                );
        }

        @GetMapping("/{id}/departements")
        public ResponseEntity<List<DepartementUtilisateurResponse>> listerDepartements(
                @PathVariable Integer id
        ) {

                return ResponseEntity.ok(
                        utilisateurGestionService.listerDepartementsUtilisateur(id)
                );
        }

        @PostMapping("/{id}/departements")
        public ResponseEntity<List<DepartementUtilisateurResponse>> ajouterDepartement(
                @PathVariable Integer id,
                @RequestBody AjouterDepartementUtilisateurRequest request
        ) {

                return ResponseEntity.ok(
                        utilisateurGestionService.ajouterDepartementUtilisateur(id, request)
                );
        }

        @DeleteMapping("/{id}/departements/{departementId}")
        public ResponseEntity<List<DepartementUtilisateurResponse>> retirerDepartement(
                @PathVariable Integer id,
                @PathVariable Integer departementId
        ) {

                return ResponseEntity.ok(
                        utilisateurGestionService.retirerDepartementUtilisateur(
                                id,
                                departementId
                        )
                );
        }


}