package mg.mef.stagemanagement.controller;

import mg.mef.stagemanagement.service.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SyntheseController {
    private final DossierSyntheseService dossiers;
    private final PilotageService pilotage;
    private final ExportService exports;
    public SyntheseController(DossierSyntheseService dossiers, PilotageService pilotage, ExportService exports) {
        this.dossiers = dossiers; this.pilotage = pilotage; this.exports = exports;
    }
    @GetMapping("/stagiaires/{id}/dossier")
    public DossierSyntheseService.Dossier dossier(@PathVariable Integer id) { return dossiers.dossier(id); }
    @GetMapping("/dashboard/pilotage")
    public PilotageService.Pilotage pilotage() { return pilotage.pilotage(); }
    @GetMapping("/dashboard/priorites")
    public List<PilotageService.Priorite> priorites() { return pilotage.priorites(); }
    @GetMapping("/stage-participants/{id}/bilan.pdf")
    public ResponseEntity<byte[]> bilan(@PathVariable Integer id) throws IOException {
        return download(exports.bilan(id), "application/pdf", "bilan-participation-" + id + ".pdf");
    }
    @GetMapping("/exports/participations.csv")
    public ResponseEntity<byte[]> participations() { return download(exports.participations(), "text/csv;charset=UTF-8", "participations.csv"); }
    @GetMapping("/exports/statistiques.csv")
    public ResponseEntity<byte[]> statistiques() { return download(exports.statistiques(), "text/csv;charset=UTF-8", "statistiques.csv"); }
    @GetMapping("/exports/alertes.csv")
    public ResponseEntity<byte[]> alertes() { return download(exports.alertes(), "text/csv;charset=UTF-8", "alertes.csv"); }
    private ResponseEntity<byte[]> download(byte[] bytes, String type, String filename) {
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(type)).cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).build().toString()).body(bytes);
    }
}
