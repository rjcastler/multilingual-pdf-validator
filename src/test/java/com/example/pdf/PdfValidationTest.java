package com.example.pdf;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfValidationTest {

    /*
     * Change this filename to your real document.
     * Put the same logical document in:
     *   src/test/resources/pdfs/en/
     *   src/test/resources/pdfs/de/
     *   src/test/resources/pdfs/nl/
     */
    private static final String PDF_FILE = "invoice.pdf";

    @Test
    void validateEnglishPdf() throws IOException {
        validateLanguage("en");
    }

    @Test
    void validateGermanPdf() throws IOException {
        validateLanguage("de");
    }

    @Test
    void validateDutchPdf() throws IOException {
        validateLanguage("nl");
    }

    private void validateLanguage(String language) throws IOException {
        Path pdf = PdfTestData.pdf(language, PDF_FILE);

        // This makes the starter project pass before real PDFs are added.
        // Once PDFs are present, the validation is executed.
        if (!Files.exists(pdf)) {
            System.out.println("SKIPPED: Add " + pdf + " to run this language test.");
            return;
        }

        String text = PdfTextExtractor.extract(pdf);

        PdfContentValidator.ValidationResult result =
                PdfContentValidator.containsAll(text, PdfTestData.requiredBusinessValues());

        assertTrue(
                result.passed(),
                () -> language + " PDF validation failed. Missing: " + result.failures()
        );
    }

    @Test
    void demonstrateNumberValidation() throws IOException {
        Path pdf = PdfTestData.pdf("en", PDF_FILE);

        if (!Files.exists(pdf)) {
            System.out.println("SKIPPED: Add " + pdf + " to run number validation.");
            return;
        }

        String text = PdfTextExtractor.extract(pdf);

        var result = PdfContentValidator.matchesPattern(
                text,
                "500",
                "Expected amount 500 was not found"
        );

        assertTrue(result.passed(), result.failures().toString());
    }
}
