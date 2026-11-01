package mg.mef.stagemanagement;

import mg.mef.stagemanagement.service.ExportService;
import org.junit.jupiter.api.Test;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ExportTests {
    @Test void csvEscapesCellsAndNeutralizesSpreadsheetFormulas() {
        String csv = new String(ExportService.csv(List.of(Arrays.asList("Nom;Prenom", "a\"b", "ligne\n2", null, " =HYPERLINK(1)", "@cmd", "\tcmd", -3))), StandardCharsets.UTF_8);
        assertEquals("\ufeff\"Nom;Prenom\";\"a\"\"b\";\"ligne\n2\";\"\";\"' =HYPERLINK(1)\";\"'@cmd\";\"'\tcmd\";\"-3\"\r\n", csv);
    }
    @Test void pdfPaginatesAndKeepsFinalLineAndAccents() throws Exception {
        var lines = new ArrayList<String>();
        lines.add("Evaluation finalis\u00e9e");
        for (int i = 0; i < 110; i++) lines.add("Activite " + i + " : " + "long contenu ".repeat(15));
        lines.add("FIN DU DOSSIER");
        try (var pdf = Loader.loadPDF(ExportService.pdf(lines))) {
            assertTrue(pdf.getNumberOfPages() > 3);
            String text = new PDFTextStripper().getText(pdf);
            assertTrue(text.contains("Evaluation finalis\u00e9e")); assertTrue(text.contains("FIN DU DOSSIER"));
        }
    }
}
