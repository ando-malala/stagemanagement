package mg.mef.stagemanagement.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ExportService {
    private final DossierSyntheseService dossiers;
    private final StageGestionService stages;
    private final PilotageService pilotage;
    private final AlerteGestionService alertes;
    public ExportService(DossierSyntheseService dossiers, StageGestionService stages, PilotageService pilotage, AlerteGestionService alertes) {
        this.dossiers = dossiers; this.stages = stages; this.pilotage = pilotage; this.alertes = alertes;
    }
    public byte[] participations() {
        var rows = new ArrayList<List<?>>();
        rows.add(List.of("Participation", "Stagiaire", "Stage", "Encadreur", "Debut", "Fin", "Statut"));
        stages.listerParticipations().forEach(p -> rows.add(Arrays.asList(p.getId(), p.getStagiaireNomComplet(), p.getStage(),
                p.getEncadreurNomComplet(), p.getDateDebutStage(), p.getDateFinStage(), p.getStatut())));
        return csv(rows);
    }
    public byte[] statistiques() {
        var rows = new ArrayList<List<?>>();
        rows.add(List.of("Participation", "Stagiaire", "Stage", "Statut", "Fin", "Jours restants", "Documents manquants", "Rapports attendus", "Soumis", "Valides", "Manquants"));
        pilotage.pilotage().dossiers().forEach(p -> rows.add(Arrays.asList(p.participationId(), p.stagiaire(), p.stage(), p.statut(), p.dateFin(),
                p.joursRestants(), p.documentsManquants(), p.rapportsAttendus(), p.rapportsSoumis(), p.rapportsValides(), p.rapportsManquants())));
        return csv(rows);
    }
    public byte[] alertes() {
        var rows = new ArrayList<List<?>>();
        rows.add(List.of("Alerte", "Type", "Participation", "Rapport", "Encadreur", "Soutenance", "Priorite", "Statut", "Echeance", "Message", "Action recommandee"));
        alertes.listerAlertes().forEach(a -> rows.add(Arrays.asList(a.getId(), a.getTypeAlerteCode(), a.getStageParticipantId(), a.getRapportId(),
                a.getEncadreurId(), a.getSoutenanceId(), a.getPriorite(), a.getStatut(), a.getDateEcheance(), a.getMessage(), a.getActionRecommandee())));
        return csv(rows);
    }
    public static byte[] csv(List<? extends List<?>> rows) {
        return ("\ufeff" + rows.stream().map(row -> row.stream().map(ExportService::cell).collect(Collectors.joining(";")))
                .collect(Collectors.joining("\r\n")) + "\r\n").getBytes(StandardCharsets.UTF_8);
    }
    private static String cell(Object value) {
        String s = value == null ? "" : value.toString();
        // Quote delimiters/newlines AND neutralize spreadsheet formulas, including whitespace-prefixed formulas.
        String trimmed = s.stripLeading();
        if (!(value instanceof Number) && (!trimmed.isEmpty() && "=+-@".indexOf(trimmed.charAt(0)) >= 0
                || s.startsWith("\t") || s.startsWith("\r") || s.startsWith("\n"))) s = "'" + s;
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
    public byte[] bilan(Integer id) throws IOException {
        var s = dossiers.participation(id);
        var p = s.participation();
        var lines = new ArrayList<String>();
        lines.add("MEF - SIIGFP | Bilan " + (p.getDateCloture() == null ? "provisoire" : "final"));
        lines.add("Participation " + id + " | Extrait du " + LocalDate.now(ZoneId.of("Indian/Antananarivo")));
        lines.add("Stagiaire : " + p.getStagiaireNomComplet());
        lines.add("Stage : " + p.getStage()); lines.add("Theme : " + text(p.getThemeStage()));
        lines.add("Encadreur : " + p.getEncadreurNomComplet());
        lines.add("Periode : " + p.getDateDebutStage() + " - " + p.getDateFinStage() + " | Statut : " + p.getStatut());
        lines.add("Cloture : " + text(p.getDateCloture()));
        lines.add(" "); lines.add("RAPPORTS ET VALIDATIONS");
        s.periodes().forEach(r -> lines.add(r.debut() + " - " + r.fin() + " : " + (r.statutRapport() == null ? "Non depose" : r.statutRapport()) + (r.manquante() ? " (manquant)" : "")));
        s.rapports().forEach(r -> {
            lines.add("Rapport " + r.rapport().getId() + " : " + text(r.rapport().getActivite()));
            r.observations().forEach(o -> lines.add("Observation : " + text(o.getCommentaire())));
        });
        lines.add(" "); lines.add("DOCUMENTS OBLIGATOIRES");
        s.documentsObligatoires().forEach(d -> lines.add(d.libelle() + " : " + (d.present() ? "present" : "manquant")));
        s.fichiers().forEach(f -> lines.add("Piece : " + f.getNomOriginal()));
        if (s.evaluation() != null) {
            lines.add(" "); lines.add("EVALUATION " + (Boolean.TRUE.equals(s.evaluation().getEstFinalisee()) ? "FINALISEE" : "EN COURS"));
            lines.add("Appreciation : " + text(s.evaluation().getAppreciationGlobale()));
            lines.add("Observations finales : " + text(s.evaluation().getObservationsFinales()));
            s.notes().forEach(n -> lines.add(n.getCritereLibelle() + " : " + n.getNote() + "/5 (coefficient " + n.getCoefficient() + ")"));
        }
        s.soutenances().forEach(d -> lines.add("Soutenance : " + d.getDateSoutenance() + " | " + text(d.getLieu())));
        lines.add(" "); lines.add("HISTORIQUE");
        s.historique().forEach(h -> lines.add(h.dateModification() + " : " + h.statut() + " | " + text(h.motif())));
        s.evenements().forEach(e -> lines.add(e.dateEvenement() + " : " + e.libelle() + " | " + text(e.motif())));
        lines.add(" "); lines.add("Extrait limite aux informations autorisees pour le compte connecte.");
        return pdf(lines);
    }
    private static String text(Object value) { return value == null ? "Non renseigne" : value.toString(); }
    public static byte[] pdf(List<String> lines) throws IOException {
        try (var document = new PDDocument(); var output = new ByteArrayOutputStream()) {
            var font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            List<String> wrapped = new ArrayList<>();
            for (String raw : lines) {
                StringBuilder line = new StringBuilder();
                for (int cp : raw.codePoints().toArray()) {
                    String character = Character.isISOControl(cp) ? " " : new String(Character.toChars(cp));
                    try { font.encode(character); } catch (IllegalArgumentException ex) { character = "?"; }
                    if (font.getStringWidth(line + character) / 1000 * 11 > 495) { wrapped.add(line.toString()); line.setLength(0); }
                    line.append(character);
                }
                wrapped.add(line.toString());
            }
            for (int start = 0; start < wrapped.size(); start += 45) {
                var page = new PDPage(PDRectangle.A4); document.addPage(page);
                try (var content = new PDPageContentStream(document, page)) {
                    content.beginText(); content.setFont(font, 11); content.setLeading(16); content.newLineAtOffset(50, 790);
                    for (int i = start; i < Math.min(start + 45, wrapped.size()); i++) { content.showText(wrapped.get(i)); content.newLine(); }
                    content.endText(); content.beginText(); content.setFont(font, 9); content.newLineAtOffset(50, 35);
                    content.showText("MEF - SIIGFP | Page " + (start / 45 + 1)); content.endText();
                }
            }
            document.save(output); return output.toByteArray();
        }
    }
}
