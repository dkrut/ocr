package com.github.dkrut;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by Denis Krutikov on 26.02.2019.
 */
public class ImageToTextConverter {
    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(ImageToTextConverter.class);

        File sourceDir = new File("FilesToOCR");
        if (!sourceDir.exists()) {
            log.error("{} doesn't exist", sourceDir.getAbsolutePath());
            throw new IllegalArgumentException();
        }

        File[] sourceDirFiles = sourceDir.listFiles();
        if (sourceDirFiles.length > 0) {
            log.info("{} have {} file(s) to OCR", sourceDir.getAbsolutePath(), sourceDirFiles.length);
            File outputFolder = new File("OutputResult");
            if (!outputFolder.exists()) {
                log.warn("{} doesn't exist. Trying to create", outputFolder);
                outputFolder.mkdir();
                log.info("{} folder created", outputFolder.getName());
            }
            try {
                for (File checkingFile : sourceDirFiles) {
                    Ocr ocr = new Ocr();
                    String fileNameWithExtension = checkingFile.toString();
                    File ocrResultFile = new File(outputFolder + "/" + FilenameUtils.removeExtension(checkingFile.getName()) + ".txt");
                    if (FilenameUtils.getExtension(fileNameWithExtension).equals("pdf")) {
                        PdfConverter pdfConverter = new PdfConverter();
                        File tempFolder = pdfConverter.pdfConvert(checkingFile);
                        if (tempFolder.isDirectory() && tempFolder.exists()) {
                            File[] tempFolderFiles = tempFolder.listFiles();
                            if (tempFolderFiles.length > 0) {
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
        } else {
            log.warn("{} is empty. Nothing to OCR", sourceDir.getAbsolutePath());
        }
    }
}