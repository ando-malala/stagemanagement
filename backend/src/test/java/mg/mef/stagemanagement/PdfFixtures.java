package mg.mef.stagemanagement;

final class PdfFixtures {
    static byte[] document() throws java.io.IOException {
        try (var pdf = new org.apache.pdfbox.pdmodel.PDDocument(); var out = new java.io.ByteArrayOutputStream()) {
            pdf.addPage(new org.apache.pdfbox.pdmodel.PDPage()); pdf.save(out); return out.toByteArray();
        }
    }
}
