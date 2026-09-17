# Multilingual PDF Validator

Java 17 + Maven + Apache PDFBox + JUnit 5 starter framework for validating English, German and Dutch PDFs.

## Project structure

```text
pdf-multilingual-validator/
├── pom.xml
├── README.md
└── src/
    └── test/
        ├── java/com/example/pdf/
        │   ├── PdfTextExtractor.java
        │   ├── PdfTextNormalizer.java
        │   ├── PdfContentValidator.java
        │   ├── PdfTestData.java
        │   └── PdfValidationTest.java
        └── resources/pdfs/
            ├── en/
            ├── de/
            └── nl/
```

## Prerequisites

- JDK 17+
- Maven 3.8+
- Git (optional)
- GitHub Copilot (optional; useful for extending the framework)

Verify:

```bash
java -version
mvn -version
```

## First run

Put your PDFs here:

```text
src/test/resources/pdfs/en/
src/test/resources/pdfs/de/
src/test/resources/pdfs/nl/
```

The starter test currently demonstrates:
- PDF text extraction
- text normalization
- required-content checks
- amount/date/ID checks
- cross-language preservation of important data

Run:

```bash
mvn clean test
```

## Important

The initial implementation intentionally does NOT decide whether a translation is linguistically correct. It validates deterministic things such as required content and business data.

The next extension can add semantic translation validation (English -> German and English -> Dutch) using an approved translation/LLM service, with the AI result treated as a separate validation signal.

## GitHub Copilot prompt

Open this project in VS Code/GitHub Copilot and ask:

"Extend this Java Maven PDF validation framework. Add semantic translation validation for English to German and English to Dutch. Preserve deterministic validation for amounts, dates, IDs, placeholders and required sections. Return structured PASS/FAIL reasons and do not let the LLM invent missing source content."
