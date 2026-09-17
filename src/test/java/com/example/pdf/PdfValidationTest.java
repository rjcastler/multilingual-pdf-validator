package com.example.pdf;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfValidationTest {

    private static final Logger logger =
            LoggerFactory.getLogger(PdfValidationTest.class);

    private static final String DOCUMENT = "invoice";

    private static final List<PdfHtmlReport.LanguageResult> reportResults =
            new ArrayList<>();

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

    @AfterAll
    static void generateHtmlReport() throws IOException {

        Path report = Path.of(
                "target",
                "pdf-validation-report",
                "report.html"
        );

        PdfHtmlReport.write(
                PdfTestData.fileName(DOCUMENT),
                reportResults,
                report
        );

        logger.info("========================================");
        logger.info("HTML validation report generated:");
        logger.info("{}", report.toAbsolutePath());
        logger.info("========================================");
    }

    private void validateLanguage(String language)
            throws IOException {

        String fileName =
                PdfTestData.fileName(DOCUMENT);

        Path pdf =
                PdfTestData.pdf(language, fileName);

        logger.info("========================================");
        logger.info("Starting {} PDF validation",
                language.toUpperCase());
        logger.info("Document : {}", DOCUMENT);
        logger.info("PDF      : {}", pdf);

        List<PdfHtmlReport.CheckResult> checks =
                new ArrayList<>();

        if (!Files.exists(pdf)) {

            logger.error("FAIL - PDF does not exist: {}", pdf);

            checks.add(new PdfHtmlReport.CheckResult(
                    "PDF exists",
                    "FAIL",
                    "PDF not found: " + pdf
            ));

            reportResults.add(
                    new PdfHtmlReport.LanguageResult(
                            language.toUpperCase(),
                            false,
                            checks
                    )
            );

            assertTrue(false,
                    "PDF not found: " + pdf);

            return;
        }

        checks.add(new PdfHtmlReport.CheckResult(
                "PDF exists",
                "PASS",
                pdf.toString()
        ));

        String text =
                PdfTextExtractor.extract(pdf);

        checks.add(new PdfHtmlReport.CheckResult(
                "Text extraction",
                "PASS",
                "Characters extracted: " + text.length()
        ));

        // 1. Validate common business values.
        var businessResult =
                PdfContentValidator.containsAll(
                        text,
                        PdfTestData.businessValues(DOCUMENT)
                );

        checks.add(new PdfHtmlReport.CheckResult(
                "Business values",
                businessResult.passed()
                        ? "PASS"
                        : "FAIL",
                businessResult.passed()
                        ? "All configured business values found"
                        : "Missing: " + businessResult.failures()
        ));

        // 2. Validate language-specific text.
        var languageResult =
                PdfContentValidator.containsAll(
                        text,
                        PdfTestData.requiredText(
                                DOCUMENT,
                                language
                        )
                );

        checks.add(new PdfHtmlReport.CheckResult(
                "Language content",
                languageResult.passed()
                        ? "PASS"
                        : "FAIL",
                languageResult.passed()
                        ? "All configured " +
                          language.toUpperCase() +
                          " text found"
                        : "Missing: " +
                          languageResult.failures()
        ));

        // 3. Validate localized date text.
        String expectedDate =
                PdfTestData.dateText(
                        DOCUMENT,
                        language
                );

        boolean dateFound =
                PdfTextNormalizer.normalize(text)
                        .toLowerCase()
                        .contains(
                                PdfTextNormalizer
                                        .normalize(expectedDate)
                                        .toLowerCase()
                        );

        checks.add(new PdfHtmlReport.CheckResult(
                "Due date",
                dateFound ? "PASS" : "FAIL",
                dateFound
                        ? "Found: " + expectedDate
                        : "Expected: " + expectedDate
        ));

        // 4. Validate amount.
        var amountResult =
                PdfContentValidator.matchesPattern(
                        text,
                        "500",
                        "Expected amount 500"
                );

        checks.add(new PdfHtmlReport.CheckResult(
                "Amount",
                amountResult.passed()
                        ? "PASS"
                        : "FAIL",
                amountResult.passed()
                        ? "Expected amount 500 found"
                        : String.join(
                                ", ",
                                amountResult.failures()
                        )
        ));

        boolean passed =
                checks.stream()
                        .allMatch(c ->
                                !"FAIL".equals(c.status()));

        reportResults.add(
                new PdfHtmlReport.LanguageResult(
                        language.toUpperCase(),
                        passed,
                        checks
                )
        );

        logger.info(
                "{} PDF validation: {}",
                language.toUpperCase(),
                passed ? "PASS" : "FAIL"
        );

        assertTrue(
                passed,
                () -> language.toUpperCase() +
                        " PDF validation failed"
        );
    }
}
