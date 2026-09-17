package com.example.pdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class PdfContentValidator {

    private static final Logger logger =
            LoggerFactory.getLogger(PdfContentValidator.class);

    private PdfContentValidator() {}

    public static ValidationResult containsAll(
            String pdfText,
            List<String> requiredTerms) {

        String normalized =
                PdfTextNormalizer.normalize(pdfText).toLowerCase();

        List<String> missing = new ArrayList<>();

        logger.info("Starting required-content validation. Values to validate: {}",
                requiredTerms.size());

        for (String term : requiredTerms) {

            boolean found = normalized.contains(
                    PdfTextNormalizer.normalize(term).toLowerCase()
            );

            if (found) {
                logger.info("PASS - Required value found: [{}]", term);
            } else {
                logger.error("FAIL - Required value missing: [{}]", term);
                missing.add(term);
            }
        }

        if (missing.isEmpty()) {
            logger.info("Required-content validation PASSED");
        } else {
            logger.error(
                    "Required-content validation FAILED. Missing values: {}",
                    missing
            );
        }

        return new ValidationResult(missing.isEmpty(), missing);
    }

    public static ValidationResult matchesPattern(
            String pdfText,
            String regex,
            String description) {

        boolean found =
                Pattern.compile(regex).matcher(pdfText).find();

        if (found) {
            logger.info("PASS - Pattern validation: {}", description);
        } else {
            logger.error("FAIL - Pattern validation: {}", description);
        }

        return new ValidationResult(
                found,
                found ? List.of() : List.of(description)
        );
    }

    public record ValidationResult(
            boolean passed,
            List<String> failures) {
    }
}