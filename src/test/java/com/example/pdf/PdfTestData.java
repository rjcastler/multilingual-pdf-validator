package com.example.pdf;

import java.nio.file.Path;
import java.util.List;

public final class PdfTestData {

    private PdfTestData() {}

    public static Path pdf(String language, String fileName) {
        return Path.of("src", "test", "resources", "pdfs", language, fileName);
    }

    public static List<String> requiredBusinessValues() {
        return List.of(
                "INV-12345",
                "CUST-1001",
                "500"
        );
    }
}
