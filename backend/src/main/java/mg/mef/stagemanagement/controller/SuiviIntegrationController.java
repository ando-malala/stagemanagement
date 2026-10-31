package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.service.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SuiviIntegrationController {
    private final DocumentCatalogueService documents;
    private final ReferentielWorkflowService references;
    private final SuiviParticipationService suivi;
    public SuiviIntegrationController(DocumentCatalogueService documents, ReferentielWorkflowService references, SuiviParticipationService suivi) {
        this.documents = documents; this.references = references; this.suivi = suivi;
    }
    @GetMapping("/referentiels/suivi/types-documents")
    public List<DocumentCatalogueService.TypeDocument> typesDocuments() { return documents.types(); }
    @GetMapping("/referentiels/suivi/types-evenements")
    public List<ReferentielWorkflowService.Reference> typesEvenements() { return references.evenements(); }
    @GetMapping("/referentiels/suivi/types-alertes")
    public List<ReferentielWorkflowService.Reference> typesAlertes() { return references.alertes(); }
    @GetMapping("/referentiels/suivi/statuts/{type}")
    public List<ReferentielWorkflowService.Reference> statuts(@PathVariable String type) { return references.statuts(type); }
    @GetMapping("/referentiels/suivi/transitions/{type}/{source}")
    public List<ReferentielWorkflowService.Transition> transitions(@PathVariable String type, @PathVariable String source) { return references.transitions(type, source); }
    @GetMapping("/stage-participants/{id}/documents-obligatoires")
    public List<DocumentCatalogueService.DocumentObligatoire> documentsObligatoires(@PathVariable Integer id) { return documents.obligatoires(id); }
    @GetMapping("/stage-participants/{id}/historique-statuts")
    public List<SuiviParticipationService.Historique> historique(@PathVariable Integer id) { return suivi.historiques(id); }
    @GetMapping("/stage-participants/{id}/evenements")
    public List<SuiviParticipationService.Evenement> evenements(@PathVariable Integer id) { return suivi.evenements(id); }
    @GetMapping("/stage-participants/{id}/periodes-rapports")
    public List<SuiviParticipationService.Periode> periodes(@PathVariable Integer id) { return suivi.periodes(id); }
    @GetMapping("/stage-participants/{id}/actions")
    public List<SuiviParticipationService.Action> actions(@PathVariable Integer id) { return suivi.actions(id); }
    @GetMapping("/encadreurs/{id}/charge")
    public SuiviParticipationService.Charge charge(@PathVariable Integer id) { return suivi.charge(id); }
}
