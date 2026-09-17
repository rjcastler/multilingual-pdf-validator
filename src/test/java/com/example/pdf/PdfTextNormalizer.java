package com.example.pdf;

import java.text.Normalizer;

public final class PdfTextNormalizer {

    private PdfTextNormalizer() {}

    public static String normalize(String text) {
        if (text == null) {
            return "";
        }



        return Normalizer.normalize(text, Normalizer.Form.NFKC)
                .replace('\u00A0', ' ')
                .replaceAll("\\s+", " ")
                .trim();
    }
}
