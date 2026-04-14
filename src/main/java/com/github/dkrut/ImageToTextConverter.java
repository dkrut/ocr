package com.github.dkrut;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ImageToTextConverter {
    private static final String SOURCE_DIR = "FilesToOCR";
    private static final String OUTPUT_DIR = "OutputResult";

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(ImageToTextConverter.class);

        File sourceDir = new File(SOURCE_DIR);
        if (!sourceDir.exists()) {
            log.error("{} doesn't exist", sourceDir.getAbsolutePath());
            throw new IllegalArgumentException();
        }

        File[] sourceDirFiles = sourceDir.listFiles();
        if (sourceDirFiles == null || sourceDirFiles.length == 0) {
            log.warn("{} is empty. Nothing to OCR", sourceDir.getAbsolutePath());
            return;
        }

        log.info("{} have {} file(s) to OCR", sourceDir.getAbsolutePath(), sourceDirFiles.length);
        File outputFolder = new File(OUTPUT_DIR);
        if (!outputFolder.exists()) {
            log.warn("{} doesn't exist. Trying to create", outputFolder);
            outputFolder.mkdir();
            log.info("{} folder created", outputFolder.getName());
        }

        Ocr ocr = new Ocr();
        PdfConverter pdfConverter = new PdfConverter();
        try {
            for (File checkingFile : sourceDirFiles) {
                File ocrResultFile = new File(outputFolder, FilenameUtils.removeExtension(checkingFile.getName()) + ".txt");
                if (FilenameUtils.getExtension(checkingFile.getName()).equals("pdf")) {
                    File tempFolder = pdfConverter.pdfConvert(checkingFile);
                    if (tempFolder.isDirectory() && tempFolder.exists()) {
                        File[] tempFolderFiles = tempFolder.listFiles();
                        if (tempFolderFiles != null && tempFolderFiles.length > 0) {
                            for (File ocrResult : tempFolderFiles) {
                                ocr.ocrFile(ocrResult, ocrResultFile);
                            }
                            FileUtils.forceDelete(tempFolder);
                            log.debug("Temp folder deleted");
                        }
                    }
                } else {
                    ocr.ocrFile(checkingFile, ocrResultFile);
                }
            }
            log.info("All OCR finished");
        } catch (IOException e) {
            log.error("Error while coping/deleting dir: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}