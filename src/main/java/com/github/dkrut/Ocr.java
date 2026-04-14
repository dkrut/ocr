package com.github.dkrut;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class Ocr {
    private static final String TESSDATA_PATH = "src/main/resources/tessdata";

    private final Logger log = LoggerFactory.getLogger(Ocr.class);
    private final Tesseract instance = new Tesseract();
    private final Property properties = new Property();

    Ocr() {
        instance.setDatapath(TESSDATA_PATH);
        instance.setLanguage(properties.getProperty("language"));
    }

    public void ocrToConsole(File fileToOcr) {
        try {
            log.info("Start OCR {}", fileToOcr.getName());
            log.info("OCR result:\n{}", instance.doOCR(fileToOcr));
        } catch (TesseractException e) {
            log.error("Error while reading image: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void ocrToFile(File fileToOcr, File fileToWrite) {
        log.info("Start OCR {}", fileToOcr.getName());
        if (fileToWrite.exists()) {
            log.debug("{} is already exist. OCR {} will be performed to this file", fileToWrite.getName(), fileToOcr.getName());
        } else {
            try {
                fileToWrite.createNewFile();
                log.info("File {} created", fileToWrite.getName());
            } catch (IOException e) {
                log.error("Error while creating file {}: {}", fileToWrite.getName(), e.getMessage());
                e.printStackTrace();
            }
        }

        try (FileWriter fileWriter = new FileWriter(fileToWrite, true)) {
            fileWriter.write(instance.doOCR(fileToOcr));
            log.info("OCR {} to {} finished", fileToOcr.getName(), fileToWrite.getName());
        } catch (TesseractException e) {
            log.error("Error while reading image: {}", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Error while writing file {}: {}", fileToWrite.getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void ocrFile(File fileToOcr, File fileToWrite) {
        String outputResultToProperty = properties.getProperty("outputResultTo");
        if (outputResultToProperty == null || !outputResultToProperty.equalsIgnoreCase("file")) {
            ocrToConsole(fileToOcr);
        } else {
            ocrToFile(fileToOcr, fileToWrite);
        }
    }
}