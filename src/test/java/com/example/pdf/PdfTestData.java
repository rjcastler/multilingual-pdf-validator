package com.example.pdf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class PdfTestData {

    private static final Logger logger =
            LoggerFactory.getLogger(PdfTestData.class);

    private static final String CONFIG =
            "testdata/pdf-validation.json";

    private static final JsonNode ROOT;

    static {
        try (InputStream input =
                     PdfTestData.class.getClassLoader().getResourceAsStream(CONFIG)) {

            if (input == null) {
                throw new IllegalStateException(
                        "Configuration file not found: " + CONFIG);
            }

            ROOT = new ObjectMapper().readTree(input);

        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Unable to load " + CONFIG + ": " + e.getMessage());
        }
    }

    private PdfTestData() {}

    public static Path pdf(String language, String fileName) {
        return Path.of(
                "src",
                "test",
                "resources",
                "pdfs",
                language,
                fileName
        );
    }

    public static String fileName(String document) {
        return documentNode(document)
                .path("fileName")
                .asText();
    }

    public static List<String> businessValues(String document) {
        List<String> values = new ArrayList<>();

        JsonNode node = documentNode(document).path("businessValues");

        node.fields().forEachRemaining(entry ->
                values.add(entry.getValue().asText())
        );

        return values;
    }

    public static List<String> requiredText(
            String document,
            String language) {

        List<String> values = new ArrayList<>();

        JsonNode node = languageNode(document, language)
                .path("requiredText");

        node.forEach(value -> values.add(value.asText()));

        return values;
    }

    public static String dateText(
            String document,
            String language) {

        return languageNode(document, language)
                .path("dateText")
                .asText();
    }

    private static JsonNode documentNode(String document) {
        JsonNode node = ROOT.path("documents").path(document);

        if (node.isMissingNode()) {
            throw new IllegalArgumentException(
                    "Document not configured: " + document);
        }

        return node;
    }

    private static JsonNode languageNode(
            String document,
            String language) {

        JsonNode node = documentNode(document)
                .path("languages")
                .path(language);

        if (node.isMissingNode()) {
            throw new IllegalArgumentException(
                    "Language '" + language +
                            "' not configured for document '" +
                            document + "'");
        }

        return node;
    }
}
