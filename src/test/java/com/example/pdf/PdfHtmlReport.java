package com.example.pdf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class PdfHtmlReport {

    private PdfHtmlReport() {}

    public record CheckResult(String check, String status, String details) {}

    public static void write(
            String document,
            List<LanguageResult> results,
            Path output) throws IOException {

        Files.createDirectories(output.getParent());

        boolean overallPass = results.stream().allMatch(LanguageResult::passed);

        StringBuilder html = new StringBuilder();

        html.append("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Multilingual PDF Validation Report</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 30px; background: #f6f7f9; color: #222; }
                    .container { max-width: 1200px; margin: auto; }
                    h1 { margin-bottom: 5px; }
                    .meta { color: #666; margin-bottom: 25px; }
                    .summary { padding: 18px; border-radius: 8px; margin-bottom: 25px; font-size: 20px; font-weight: bold; }
                    .pass { background: #e7f6ec; color: #176b36; }
                    .fail { background: #fdeaea; color: #a12626; }
                    table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 1px 4px #ddd; }
                    th, td { padding: 12px; border-bottom: 1px solid #ddd; text-align: left; }
                    th { background: #eeeeee; }
                    .status-pass { color: #176b36; font-weight: bold; }
                    .status-fail { color: #a12626; font-weight: bold; }
                    .section { margin-top: 30px; }
                    .card { background: white; padding: 18px; margin-top: 15px; border-radius: 8px; box-shadow: 0 1px 4px #ddd; }
                    code { background: #f1f1f1; padding: 2px 5px; border-radius: 3px; }
                </style>
                </head>
                <body>
                <div class="container">
                """);

        html.append("<h1>Multilingual PDF Validation Report</h1>");
        html.append("<div class=\"meta\">Document: <code>")
                .append(escape(document))
                .append("</code><br>Generated: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append("</div>");

        html.append("<div class=\"summary ")
                .append(overallPass ? "pass" : "fail")
                .append("\">Overall: ")
                .append(overallPass ? "PASS" : "FAIL")
                .append("</div>");

        html.append("""
                <table>
                <thead>
                <tr>
                    <th>Validation</th>
                    <th>English</th>
                    <th>German</th>
                    <th>Dutch</th>
                </tr>
                </thead>
                <tbody>
                """);

        List<String> checkNames = results.stream()
                .flatMap(r -> r.checks().stream())
                .map(CheckResult::check)
                .distinct()
                .toList();

        for (String checkName : checkNames) {
            html.append("<tr><td>").append(escape(checkName)).append("</td>");
            for (String language : List.of("EN", "DE", "NL")) {
                LanguageResult languageResult = results.stream()
                        .filter(r -> r.language().equalsIgnoreCase(language))
                        .findFirst().orElse(null);

                CheckResult check = languageResult == null ? null :
                        languageResult.checks().stream()
                                .filter(c -> c.check().equals(checkName))
                                .findFirst().orElse(null);

                String status = check == null ? "NOT RUN" : check.status();

                html.append("<td class=\"")
                        .append("PASS".equals(status) ? "status-pass" : "FAIL".equals(status) ? "status-fail" : "")
                        .append("\">")
                        .append(escape(status))
                        .append("</td>");
            }
            html.append("</tr>");
        }

        html.append("</tbody></table>");

        html.append("<div class=\"section\"><h2>Detailed Results</h2>");

        for (LanguageResult languageResult : results) {
            html.append("<div class=\"card\"><h3>")
                    .append(escape(languageResult.language()))
                    .append(" — ")
                    .append(languageResult.passed() ? "<span class=\"status-pass\">PASS</span>" : "<span class=\"status-fail\">FAIL</span>")
                    .append("</h3>");

            html.append("<table><thead><tr><th>Check</th><th>Status</th><th>Details</th></tr></thead><tbody>");

            for (CheckResult check : languageResult.checks()) {
                html.append("<tr><td>").append(escape(check.check()))
                        .append("</td><td class=\"")
                        .append("PASS".equals(check.status()) ? "status-pass" : "status-fail")
                        .append("\">")
                        .append(escape(check.status()))
                        .append("</td><td>")
                        .append(escape(check.details()))
                        .append("</td></tr>");
            }

            html.append("</tbody></table></div>");
        }

        html.append("</div></div></body></html>");

        Files.writeString(output, html.toString(), StandardCharsets.UTF_8);
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public record LanguageResult(
            String language,
            boolean passed,
            List<CheckResult> checks) {
    }
}
