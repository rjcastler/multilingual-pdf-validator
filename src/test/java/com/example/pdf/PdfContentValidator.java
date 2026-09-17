package com.example.pdf;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class PdfContentValidator {

    private PdfContentValidator() {}

    public static ValidationResult containsAll(String pdfText, List<String> requiredTerms) {
        String normalized = PdfTextNormalizer.normalize(pdfText).toLowerCase();
        List<String> missing = new ArrayList<>();

        for (String term : requiredTerms) {
            if (!normalized.contains(PdfTextNormalizer.normalize(term).toLowerCase())) {
                missing.add(term);
            }
        }

        return new ValidationResult(missing.isEmpty(), missing);
    }

    public static ValidationResult matchesPattern(String pdfText, String regex, String description) {
        boolean found = Pattern.compile(regex).matcher(pdfText).find();
        return new ValidationResult(found, found ? List.of() : List.of(description));
    }

    public record ValidationResult(boolean passed, List<String> failures) {}
}
