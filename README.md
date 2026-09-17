# Multilingual PDF Validator

Java + Maven + PDFBox + JUnit framework for validating multilingual PDF content.

## Validation Workflow

The framework follows this flow:

```text
                     JSON
                      │
             ┌────────┴────────┐
             ▼                 ▼
       Business Values    Language Values
             │                 │
             └────────┬────────┘
                      ▼
                   PDFBox
                      │
                      ▼
                Extract Text
                      │
                      ▼
                  Validate
                      │
             ┌────────┴────────┐
             ▼                 ▼
          JUnit             HTML Report
             │                 │
             └────────┬────────┘
                      ▼
                   Jenkins
```

### How the workflow works

1. **JSON**  
   Expected validation values are maintained in:
   `src/test/resources/testdata/pdf-validation.json`

2. **Business Values**  
   Common values such as invoice number, customer ID, amount and currency are
   validated across the PDFs.

3. **Language Values**  
   Language-specific text is configured separately for English, German and Dutch.
   This allows the framework to validate localized content without hard-coding
   values in Java.

4. **PDFBox**  
   Apache PDFBox loads each PDF and extracts its text.

5. **Extract Text**  
   The extracted PDF content is normalized before validation.

6. **Validate**  
   The framework validates configured business values, language-specific text,
   dates and other deterministic PDF content.

7. **JUnit**  
   JUnit executes the validation tests and marks the test suite as PASS or FAIL.

8. **HTML Report**  
   A detailed HTML report is generated under:
   `target/pdf-validation-report/report.html`

9. **Jenkins**  
   Jenkins runs the Maven tests, publishes JUnit results and publishes the HTML
   validation report as a build artifact.

## Project Structure

```text
multilingual-pdf-validator/
│
├── pom.xml
├── Jenkinsfile
├── Dockerfile
│
├── .github/
│   └── workflows/
│       └── maven-ci.yml
│
└── src/
    └── test/
        ├── java/
        │   └── com/example/pdf/
        │       ├── PdfTextExtractor.java
        │       ├── PdfTextNormalizer.java
        │       ├── PdfContentValidator.java
        │       ├── PdfTestData.java
        │       ├── PdfHtmlReport.java
        │       └── PdfValidationTest.java
        │
        └── resources/
            ├── pdfs/
            │   ├── en/
            │   ├── de/
            │   └── nl/
            │
            ├── testdata/
            │   └── pdf-validation.json
            │
            └── logback-test.xml
```

## Running Locally

Run:

```bash
mvn clean test
```

The tests validate the configured English, German and Dutch PDFs.

The HTML report is generated at:

```text
target/pdf-validation-report/report.html
```

Open it on macOS with:

```bash
open target/pdf-validation-report/report.html
```

## Docker

Build the image:

```bash
docker build -t multilingual-pdf-validator .
```

Run the tests:

```bash
docker run --rm multilingual-pdf-validator
```

## Jenkins

The Jenkins pipeline performs:

```text
GitHub
   ↓
Jenkins
   ↓
Checkout
   ↓
Maven clean test
   ↓
PDF Validation
   ↓
JUnit XML Results
   ↓
HTML Validation Report
```

The Jenkinsfile publishes:

- JUnit test results
- HTML PDF validation report
- HTML report as a build artifact

The Jenkins HTML Publisher Plugin is required for the HTML report.

## Configuration

Validation data is maintained in:

```text
src/test/resources/testdata/pdf-validation.json
```

Example:

```json
{
  "documents": {
    "invoice": {
      "fileName": "invoice.pdf",
      "businessValues": {
        "invoiceNumber": "INV-12345",
        "customerId": "CUST-1001",
        "amount": "500",
        "currency": "EUR"
      }
    }
  }
}
```

Add or modify expected values in this JSON instead of changing the Java
validation logic.

## Multilingual PDF Structure

Place the PDFs in:

```text
src/test/resources/pdfs/en/
src/test/resources/pdfs/de/
src/test/resources/pdfs/nl/
```

For example:

```text
pdfs/
├── en/
│   └── invoice.pdf
├── de/
│   └── invoice.pdf
└── nl/
    └── invoice.pdf
```

## Future Enhancements

Planned extensions include:

- Semantic English → German translation validation
- Semantic English → Dutch translation validation
- Page-level validation
- Table validation
- Placeholder validation
- Currency and number-format validation
- Date-format validation
- Screenshot/PDF visual comparison
- Allure reporting
- Parallel PDF validation
- Jenkins pipeline parameters
