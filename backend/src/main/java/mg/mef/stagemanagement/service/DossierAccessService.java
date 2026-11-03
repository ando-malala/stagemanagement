package mg.mef.stagemanagement.service;

import java.util.Objects;
import mg.mef.stagemanagement.dto.auth.AuthMeResponse;
import mg.mef.stagemanagement.model.*;
import mg.mef.stagemanagement.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Resource authorization shared by every business entry point. Never trust a client owner ID. */
@Service
@Transactional(readOnly = true)
public class DossierAccessService {
    private final StageParticipantRepository participations;
    private final StagiaireRepository trainees;
    private final StageRepository stages;
    private final EncadreurRepository mentors;
    private final RapportActiviteRepository reports;
    private final EvaluationFinaleRepository evaluations;
    private final SoutenanceRepository defenses;
    private final StageParticipantFichierRepository participationFiles;
    private final StagiaireFichierRepository traineeFiles;
    private final StructureAccueilRepository structures;
    private final StagiaireAcademiqueRepository academics;

    public DossierAccessService(StageParticipantRepository participations,
            StagiaireRepository trainees, StageRepository stages, EncadreurRepository mentors,
            RapportActiviteRepository reports, EvaluationFinaleRepository evaluations, SoutenanceRepository defenses,
            StageParticipantFichierRepository participationFiles, StagiaireFichierRepository traineeFiles,
            StructureAccueilRepository structures, StagiaireAcademiqueRepository academics) {
        this.participations = participations; this.trainees = trainees;
        this.stages = stages; this.mentors = mentors; this.reports = reports; this.evaluations = evaluations;
        this.defenses = defenses; this.participationFiles = participationFiles; this.traineeFiles = traineeFiles;
        this.structures = structures; this.academics = academics;
    }
    public AuthMeResponse user() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof AuthMeResponse))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session requise");
        return (AuthMeResponse) auth.getPrincipal();
    }
    public boolean role(String role) { return user().getRoles().contains(role); }
    public boolean traineeOnly() { return role("STAGIAIRE") && !role("ADMIN") && !role("ENCADREUR"); }
    public void require(boolean allowed) {
        if (!allowed) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces hors de votre perimetre");
    }
    private ResponseStatusException missing() { return new ResponseStatusException(HttpStatus.NOT_FOUND, "Ressource introuvable"); }
    private boolean owns(Utilisateur u) { return u != null && Objects.equals(u.getId(), user().getUtilisateurId()); }
    // Administrative authority is global after merging ADMIN and RESPONSABLE_STAGE.
    public boolean department(Integer id) {
        return role("ADMIN");
    }
    public boolean readParticipation(StageParticipant p) {
        return manageParticipation(p) || (role("ENCADREUR") && owns(p.getEncadreur().getUtilisateur()))
                || (role("STAGIAIRE") && owns(p.getStagiaireAcademique().getStagiaire().getUtilisateur()));
    }
    public boolean manageParticipation(StageParticipant p) {
        return department(p.getStage().getStructureAccueil().getDepartement().getId());
    }
    public StageParticipant participation(Integer id, boolean write) {
        var p = participations.findById(id).orElseThrow(this::missing);
        require(write ? manageParticipation(p) : readParticipation(p)); return p;
    }
    public boolean readTrainee(Stagiaire t) {
        return role("ADMIN")
                || (role("STAGIAIRE") && owns(t.getUtilisateur()))
                || participations.findByStagiaireAcademique_Stagiaire_Id(t.getId()).stream().anyMatch(this::readParticipation);
    }
    public Stagiaire trainee(Integer id, boolean write) {
        var t = trainees.findById(id).orElseThrow(this::missing);
        require(write ? role("ADMIN") : readTrainee(t)); return t;
    }
    public boolean readStage(Stage s) {
        return department(s.getStructureAccueil().getDepartement().getId())
                || participations.findByStage_Id(s.getId()).stream().anyMatch(this::readParticipation);
    }
    public void stage(Integer id, boolean write) {
        var s = stages.findById(id).orElseThrow(this::missing);
        require(write ? department(s.getStructureAccueil().getDepartement().getId()) : readStage(s));
    }
    public boolean readMentor(Encadreur e) {
        return department(e.getDepartement().getId()) || (role("ENCADREUR") && owns(e.getUtilisateur()))
                || (role("STAGIAIRE") && participations.findByEncadreur_Id(e.getId()).stream().anyMatch(this::readParticipation));
    }
    public void mentor(Integer id, boolean write) {
        var e = mentors.findById(id).orElseThrow(this::missing);
        require(write ? department(e.getDepartement().getId()) : readMentor(e));
    }
    public void structure(Integer id) {
        if (id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Structure obligatoire");
        require(department(structures.findById(id).orElseThrow(this::missing).getDepartement().getId()));
    }
    public void assignment(Integer stageId, Integer academicId, Integer mentorId) {
        if (stageId == null || academicId == null || mentorId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Affectation incomplete");
        stage(stageId, true); mentor(mentorId, true);
        trainee(academics.findById(academicId).orElseThrow(this::missing).getStagiaire().getId(), false);
    }
    public boolean readReport(RapportActivite r) { return readParticipation(r.getStageParticipant()); }
    public RapportActivite report(Integer id) {
        var r = reports.findById(id).orElseThrow(this::missing); require(readReport(r)); return r;
    }
    public void review(Integer participationId) {
        var p = participation(participationId, false);
        require(manageParticipation(p) || (role("ENCADREUR") && owns(p.getEncadreur().getUtilisateur())));
    }
    public void reportContent(Integer id, Integer targetParticipation) {
        var r = report(id);
        require(canAuthorReport(r.getStageParticipant()));
        require(Objects.equals(r.getStageParticipant().getId(), targetParticipation));
        require(java.util.Set.of("BROUILLON", "A_CORRIGER").contains(r.getStatut().getCode()));
    }
    public boolean canAuthorReport(StageParticipant p) {
        return p != null && role("STAGIAIRE") && owns(p.getStagiaireAcademique().getStagiaire().getUtilisateur());
    }
    public boolean canReviewReport(StageParticipant p) {
        return p != null && role("ENCADREUR") && owns(p.getEncadreur().getUtilisateur())
                && !owns(p.getStagiaireAcademique().getStagiaire().getUtilisateur());
    }
    public void reportAuthor(Integer participationId) {
        require(canAuthorReport(participation(participationId, false)));
    }
    public void reportReviewer(Integer participationId) {
        require(canReviewReport(participation(participationId, false)));
    }
    public void reportTransition(Integer id, String target) {
        var r = report(id);
        String code = target == null ? "" : target.trim().toUpperCase(java.util.Locale.ROOT);
        if ("SOUMIS".equals(code)) {
            require(canAuthorReport(r.getStageParticipant()));
            require(java.util.Set.of("BROUILLON", "A_CORRIGER").contains(r.getStatut().getCode()));
        } else {
            require(canReviewReport(r.getStageParticipant()));
            require(java.util.Set.of("VALIDE", "A_CORRIGER").contains(code) && "SOUMIS".equals(r.getStatut().getCode()));
        }
    }
    public boolean readEvaluation(EvaluationFinale e) { return readParticipation(e.getStageParticipant()); }
    public void evaluation(Integer id, boolean write) {
        var e = evaluations.findById(id).orElseThrow(this::missing);
        if (write) review(e.getStageParticipant().getId()); else require(readEvaluation(e));
    }
    public void evaluator(Integer participationId, Integer mentorId) {
        review(participationId);
        var p = participation(participationId, false);
        require(Objects.equals(p.getEncadreur().getId(), mentorId));
    }
    public boolean readDefense(Soutenance s) { return readParticipation(s.getStageParticipant()); }
    public void defense(Integer id, boolean write) {
        var s = defenses.findById(id).orElseThrow(this::missing); participation(s.getStageParticipant().getId(), write);
    }
    public boolean readAlert(Alerte a) {
        if (role("ADMIN")) return true;
        if (traineeOnly()) return false;
        boolean linked = false;
        if (a.getStageParticipant() != null) { linked = true; if (!readParticipation(a.getStageParticipant())) return false; }
        if (a.getRapport() != null) { linked = true; if (!readReport(a.getRapport())) return false; }
        if (a.getEncadreur() != null) {
            linked = true;
            if (!readMentor(a.getEncadreur())) return false;
            // A global workload message contains counts across all this mentor's departments.
            if (a.getTypeAlerte() != null && "ENCADREUR_SURCHARGE".equals(a.getTypeAlerte().getCode())
                    && participations.findByEncadreur_Id(a.getEncadreur().getId()).stream()
                    .filter(p -> java.util.Set.of("EN_COURS", "A_CLOTURER").contains(p.getStatut().getCode()))
                    .anyMatch(p -> !readParticipation(p))) return false;
        }
        if (a.getSoutenance() != null) { linked = true; if (!readDefense(a.getSoutenance())) return false; }
        return linked;
    }
    public void file(Integer id) {
        require(readFile(id));
    }
    public boolean readFile(Integer id) {
        if (role("ADMIN")) return true;
        var ps = participationFiles.findByFichier_Id(id);
        var ts = traineeFiles.findByFichier_Id(id);
        // All associations must be readable; a second link must never expose another dossier.
        return (!ps.isEmpty() || !ts.isEmpty()) && ps.stream().allMatch(f -> readParticipation(f.getStageParticipant()))
                && ts.stream().allMatch(f -> readTrainee(f.getStagiaire()));
    }
}
