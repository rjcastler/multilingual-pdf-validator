package com.example.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public final class PdfTextExtractor {

    private static final Logger logger =
            LoggerFactory.getLogger(PdfTextExtractor.class);

    private PdfTextExtractor() {}

    public static String extract(Path pdfPath) throws IOException {

        logger.info("Starting PDF text extraction: {}", pdfPath);

        try (var document = Loader.loadPDF(pdfPath.toFile())) {

            logger.info("PDF loaded successfully. Pages: {}",
                    document.getNumberOfPages());

            String text = new PDFTextStripper().getText(document);

            logger.info("Text extraction completed. Characters extracted: {}",
                    text.length());

            return text;
        }
    }
}