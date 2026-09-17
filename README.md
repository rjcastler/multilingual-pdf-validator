# Multilingual PDF Validator

Java + Maven + PDFBox + JUnit framework for validating multilingual PDF content.

## Prerequisites

The project can be executed locally on **Windows** or **macOS**.

### Required software

| Software | Version | Purpose |
|---|---|---|
| Java JDK | 17 or later | Compile and execute the Java tests |
| Apache Maven | 3.8+ | Build the project and run tests |
| Git | Latest recommended | Clone and manage the repository |
| IntelliJ IDEA | 2024+ recommended | Optional IDE for development |
| Docker Desktop | Latest recommended | Optional; required only for Docker execution |
| Web browser | Any modern browser | View the generated HTML report |

### Verify Java

Open a terminal on macOS or Command Prompt/PowerShell on Windows:

```bash
java -version
```

Expected:

```text
java version "17.x.x"
```

### Verify Maven

```bash
mvn -version
```

Expected output should show Maven 3.8 or newer and Java 17 or newer.

### Verify Git

```bash
git --version
```

### macOS setup

Recommended options:

- Install JDK 17 using Temurin/OpenJDK or another trusted JDK distribution.
- Install Maven using Homebrew:

```bash
brew install maven
```

- Verify:

```bash
java -version
mvn -version
git --version
```

If Homebrew is not installed, Maven and Java can also be installed manually.

### Windows setup

Install:

1. JDK 17 or later.
2. Maven 3.8 or later.
3. Git.
4. Optionally IntelliJ IDEA and Docker Desktop.

After installation, open a **new** PowerShell or Command Prompt window and verify:

```powershell
java -version
mvn -version
git --version
```

If `java` or `mvn` is not recognized, make sure the Java `bin` directory and Maven `bin` directory are available through the system `PATH`.

### IntelliJ IDEA

If using IntelliJ IDEA:

1. Open the cloned `multilingual-pdf-validator` repository.
2. Make sure `pom.xml` is detected as a Maven project.
3. Set the project SDK to Java 17 or later.
4. Reload the Maven project.
5. Confirm that `src/test/java` is recognized as a Test Sources Root.
6. Run `PdfValidationTest` from IntelliJ or use Maven from the terminal.

### Docker

Docker is optional for local execution.

If Docker is used, install Docker Desktop and verify:

```bash
docker --version
```

The project can then be executed with:

```bash
docker build -t multilingual-pdf-validator .
docker run --rm multilingual-pdf-validator
```

### Recommended environment check

Before running the project, verify all required tools:

```bash
java -version
mvn -version
git --version
```

Then run:

```bash
mvn clean test
```

After a successful run, the HTML validation report is available at:

```text
target/pdf-validation-report/report.html
```

On macOS:

```bash
open target/pdf-validation-report/report.html
```

On Windows PowerShell:

```powershell
Start-Process target/pdf-validation-report/report.html
```

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
