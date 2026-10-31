package mg.mef.stagemanagement.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.imageio.ImageIO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Component
public class DocumentContentValidator {
    public record Validated(String filename, String extension, String mime, byte[] bytes) {}
    public Validated validate(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fichier obligatoire");
        if (file.getSize() > 10L * 1024 * 1024) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Maximum 10 Mio");
        String name = Optional.ofNullable(file.getOriginalFilename()).orElse("").replace('\\', '/');
        name = name.substring(name.lastIndexOf('/') + 1);
        if (name.isBlank() || name.length() > 255 || name.chars().anyMatch(c -> c < 32 || c == 127)) invalid();
        String lower = name.toLowerCase(Locale.ROOT);
        try {
            byte[] bytes = file.getBytes();
            String mime; String extension;
            if (lower.endsWith(".pdf")) {
                String pdf = new String(bytes, StandardCharsets.ISO_8859_1);
                if (!pdf.startsWith("%PDF-") || !pdf.stripTrailing().endsWith("%%EOF")
                        || java.util.regex.Pattern.compile("/(JavaScript|JS|Launch|EmbeddedFile|OpenAction|AA)\\b").matcher(pdf).find()) invalid();
                try (var document = org.apache.pdfbox.Loader.loadPDF(bytes)) {
                    if (document.isEncrypted() || document.getNumberOfPages() < 1 || document.getNumberOfPages() > 300) invalid();
                    inspectPdf(document.getDocumentCatalog().getCOSObject(), Collections.newSetFromMap(new IdentityHashMap<>()), 0);
                }
                mime = "application/pdf"; extension = ".pdf";
            } else if (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                try (var input = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
                    var readers = ImageIO.getImageReaders(input);
                    if (!readers.hasNext()) { invalid(); }
                    var reader = readers.next();
                    try {
                        reader.setInput(input, true, true);
                        String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                        boolean png = lower.endsWith(".png");
                        if (!(png ? format.equals("png") : format.equals("jpeg"))) invalid();
                        long pixels = (long) reader.getWidth(0) * reader.getHeight(0);
                        if (pixels <= 0 || pixels > 20_000_000 || reader.read(0) == null) invalid();
                        mime = png ? "image/png" : "image/jpeg"; extension = png ? ".png" : ".jpg";
                    } finally { reader.dispose(); }
                }
            } else { invalid(); return null; }
            if (!mime.equalsIgnoreCase(file.getContentType())) invalid();
            return new Validated(name, extension, mime, bytes);
        } catch (IOException | IllegalArgumentException failure) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Contenu documentaire invalide");
        }
    }
    private void inspectPdf(org.apache.pdfbox.cos.COSBase node, Set<org.apache.pdfbox.cos.COSBase> visited, int depth) {
        if (node == null || !visited.add(node)) return;
        if (depth > 64 || visited.size() > 100000) invalid();
        Set<String> denied = Set.of("JavaScript", "JS", "Launch", "EmbeddedFile", "EmbeddedFiles", "OpenAction", "AA", "RichMedia", "XFA", "SubmitForm", "ImportData", "GoToR", "Sound", "Movie");
        if (node instanceof org.apache.pdfbox.cos.COSObject obj) inspectPdf(obj.getObject(), visited, depth + 1);
        else if (node instanceof org.apache.pdfbox.cos.COSDictionary dict) {
            for (var entry : dict.entrySet()) {
                if (denied.contains(entry.getKey().getName())) invalid();
                inspectPdf(entry.getValue(), visited, depth + 1);
            }
        } else if (node instanceof org.apache.pdfbox.cos.COSArray array) {
            for (var value : array) inspectPdf(value, visited, depth + 1);
        } else if (node instanceof org.apache.pdfbox.cos.COSName name && denied.contains(name.getName())) invalid();
    }
    private void invalid() { throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "Formats autorises : PDF sans contenu actif detecte, JPEG et PNG ; extension, contenu et type MIME doivent correspondre"); }
}
