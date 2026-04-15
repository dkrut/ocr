package com.github.dkrut;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

class Ocr {
    private static final String TESSDATA_PATH = "src/main/resources/tessdata";
    private static final int DPI = 300;

    private final Logger log = LoggerFactory.getLogger(Ocr.class);

    public String processFile(File file, String languages) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(TESSDATA_PATH);
        tesseract.setLanguage(languages);
        tesseract.setVariable("user_defined_dpi", String.valueOf(DPI));
        return tesseract.doOCR(file);
    }
}