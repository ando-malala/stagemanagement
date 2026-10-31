package mg.mef.stagemanagement;

import mg.mef.stagemanagement.service.DocumentContentValidator;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class DocumentContentTests {
    final DocumentContentValidator validator = new DocumentContentValidator();
    MockMultipartFile pdf(String name, String mime, String body) {
        return new MockMultipartFile("fichier", name, mime, body.getBytes(StandardCharsets.US_ASCII));
    }
    @Test void pdfChecksTypeSignatureAndActiveContent() throws Exception {
        assertEquals("application/pdf", validator.validate(new MockMultipartFile("fichier", "document.pdf", "application/pdf", PdfFixtures.document())).mime());
        for (String body : new String[] {"not a pdf", "%PDF-1.4\n%%EOF", "%PDF-1.4\n/JavaScript (bad)\n%%EOF", "%PDF-1.4\n/EmbeddedFile\n%%EOF", "%PDF-1.4\ntruncated"})
            assertEquals(415, assertThrows(ResponseStatusException.class, () -> validator.validate(pdf("x.pdf", "application/pdf", body))).getStatusCode().value());
        assertThrows(ResponseStatusException.class, () -> validator.validate(pdf("x.pdf", "text/plain", "%PDF-1.4\n%%EOF")));
        assertThrows(ResponseStatusException.class, () -> validator.validate(pdf("x.html", "text/html", "<script>bad</script>")));
    }
    @Test void compressedPdfActionsAreRejectedAfterParsing() throws Exception {
        try (var doc = new org.apache.pdfbox.pdmodel.PDDocument(); var out = new java.io.ByteArrayOutputStream()) {
            doc.addPage(new org.apache.pdfbox.pdmodel.PDPage());
            doc.getDocumentCatalog().setOpenAction(new org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript("app.alert('test')"));
            doc.save(out);
            assertThrows(ResponseStatusException.class, () -> validator.validate(new MockMultipartFile("fichier", "active.pdf", "application/pdf", out.toByteArray())));
        }
    }
    @Test void imagesMustDecodeAndMatchTheirExtension() throws Exception {
        var out = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(new java.awt.image.BufferedImage(2, 2, java.awt.image.BufferedImage.TYPE_INT_RGB), "png", out);
        assertEquals("image/png", validator.validate(new MockMultipartFile("fichier", "image.png", "image/png", out.toByteArray())).mime());
        assertThrows(ResponseStatusException.class, () -> validator.validate(new MockMultipartFile("fichier", "image.jpg", "image/jpeg", out.toByteArray())));
        assertThrows(ResponseStatusException.class, () -> validator.validate(pdf("image.png", "image/png", "fake")));
    }
    @Test void emptyOversizeAndHeaderInjectionAreRejected() {
        assertThrows(ResponseStatusException.class, () -> validator.validate(pdf("x.pdf", "application/pdf", "")));
        assertThrows(ResponseStatusException.class, () -> validator.validate(pdf("x\r\n.pdf", "application/pdf", "%PDF-1.4\n%%EOF")));
        assertEquals(413, assertThrows(ResponseStatusException.class, () -> validator.validate(new MockMultipartFile("fichier", "x.pdf", "application/pdf", new byte[10*1024*1024+1]))).getStatusCode().value());
    }
}
