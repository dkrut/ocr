package org.bitbucket.dkrut;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by Denis Krutikov on 26.02.2019.
 */

public class ImageText {

    public static void main(String[] args) {

        Logger log = LoggerFactory.getLogger(ImageText.class);

        Ocr ocr = new Ocr();
        PdfConverter pdfConverter = new PdfConverter();

        File sourceDir = new File("src/main/resources/testFiles");
        File tempFolder = new File("src/main/resources/temp");
        File outPutFolder = new File("src/main/resources/outPutResult");

        if (sourceDir.exists() && sourceDir.list() != null) {
            log.info(sourceDir + " have files to OCR");

            if (!outPutFolder.exists()){
                log.warn(outPutFolder + " doesn't exist. Trying to create");
                outPutFolder.mkdir();
                log.info(outPutFolder.getName() + " folder created");
            }

            try {
                File[] sourceDirFiles = sourceDir.listFiles();
                assert sourceDirFiles != null;
                for (File checkingFile : sourceDirFiles) {
                    String fileNameWithExtension = checkingFile.toString();
                    File ocrResultFile = new File("src/main/resources/outPutResult/" + FilenameUtils.removeExtension(checkingFile.getName()) + ".txt");
                    if (FilenameUtils.getExtension(fileNameWithExtension).equals("pdf")){
                        pdfConverter.pdfConvert(checkingFile);

                        File[] tempFolderFiles = tempFolder.listFiles();
                        assert tempFolderFiles != null;
                        for (File ocrResult : tempFolderFiles) {
                            ocr.ocrToFile(ocrResult, ocrResultFile);
                        }
                        FileUtils.forceDelete(tempFolder);
                        log.info("Temp folder deleted");

                    } else ocr.ocrToFile(checkingFile, ocrResultFile);
                }
            } catch (IOException e) {
                log.error("Error while coping/deleting dir: " + e.getMessage());
                e.printStackTrace();
            }
        } else log.warn(sourceDir.getName() + " is empty or doesn't exist");
        log.info("All OCR finished");
    }
}