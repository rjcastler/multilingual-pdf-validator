# JSON-driven PDF validation

Add this folder/file to the existing project:

src/test/resources/testdata/pdf-validation.json

The Java test reads validation values from this JSON. You no longer need to
change Java code when business values or language-specific expected text changes.

## Add a new document

Example:

"documents": {
  "invoice": { ... },
  "policy": {
    "fileName": "policy.pdf",
    "businessValues": {
      "policyNumber": "POL-10001",
      "customerId": "CUST-1001"
    },
    "languages": {
      "en": {
        "requiredText": ["POLICY", "Policy Number"],
        "dateText": "15 October 2026"
      },
      "de": {
        "requiredText": ["VERSICHERUNGSPOLICE", "Policennummer"],
        "dateText": "15. Oktober 2026"
      },
      "nl": {
        "requiredText": ["VERZEKERINGSPOLIS", "Polisnummer"],
        "dateText": "15 oktober 2026"
      }
    }
  }
}

You would then add:

src/test/resources/pdfs/en/policy.pdf
src/test/resources/pdfs/de/policy.pdf
src/test/resources/pdfs/nl/policy.pdf

and create a parameterized test for the policy document.

## Important

This update requires Jackson in pom.xml.

Add this dependency:

<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.20.0</version>
</dependency>

Then reload Maven and run:

mvn clean test

The existing PdfHtmlReport.java is reused; you do not need to replace it.
