package com.example.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Path;

public final class PdfTextExtractor {

    private PdfTextExtractor() {}

    public static String extract(Path pdfPath) throws IOException {
        try (var document = Loader.loadPDF(pdfPath.toFile())) {
            return new PDFTextStripper().getText(document);
        }
    }
}
