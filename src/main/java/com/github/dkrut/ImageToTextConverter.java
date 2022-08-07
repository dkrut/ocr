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

        Ocr ocr = new Ocr();
        PdfConverter pdfConverter = new PdfConverter();
        File sourceDir = new File("FilesToOCR");
        File tempFolder = new File("temp");
        File outputFolder = new File("OutputResult");
        if (!sourceDir.exists()) {
            log.error(sourceDir.getAbsolutePath() + " doesn't exist");
            throw new IllegalArgumentException();
        }
        File[] sourceDirFiles = sourceDir.listFiles();
        if (sourceDirFiles.length > 0) {
            log.info(sourceDir.getAbsolutePath() + " have " + sourceDirFiles.length + " file(s) to OCR");
            if (!outputFolder.exists()) {
                log.warn(outputFolder + " doesn't exist. Trying to create");
                outputFolder.mkdir();
                log.info(outputFolder.getName() + " folder created");
            }
            try {
                for (File checkingFile : sourceDirFiles) {
                    String fileNameWithExtension = checkingFile.toString();
                    File ocrResultFile = new File(outputFolder + "/" + FilenameUtils.removeExtension(checkingFile.getName()) + ".txt");
                    if (FilenameUtils.getExtension(fileNameWithExtension).equals("pdf")) {
                        pdfConverter.pdfConvert(checkingFile);

                        File[] tempFolderFiles = tempFolder.listFiles();
                        assert tempFolderFiles != null;
                        for (File ocrResult : tempFolderFiles) {
                            ocr.ocrFile(ocrResult, ocrResultFile);
                        }
                        FileUtils.forceDelete(tempFolder);
                        log.debug("Temp folder deleted");
                    } else {
                        ocr.ocrFile(checkingFile, ocrResultFile);
                    }
                }
                log.info("All OCR finished");
            } catch (IOException e) {
                log.error("Error while coping/deleting dir: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            log.warn(sourceDir.getAbsolutePath() + " is empty. Nothing to OCR");
        }
    }
}