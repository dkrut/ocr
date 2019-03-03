package org.bitbucket.dkrut;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Denis Krutikov on 26.02.2019.
 */

public class Ocr {

    public static void main(String[] args) {

        Logger log = LoggerFactory.getLogger(org.bitbucket.dkrut.Ocr.class);

        Properties tesseractProperties = new Properties();
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/tesseract.properties");
            tesseractProperties.load(fis);
        } catch (IOException e){
            System.err.println("Error while reading Tesseract properties: " + e.getMessage());
        }

        File sourceDir = new File("src/main/resources/testFiles");
        File tempFolder = new File("src/main/resources/temp");

        PdfConverter pdfConverter = new PdfConverter();

        Tesseract instance = new Tesseract();
        instance.setDatapath("src/main/resources/tessdata");
        instance.setLanguage(tesseractProperties.getProperty("language"));

        if (sourceDir.exists() && sourceDir.list() != null) {
            log.info(sourceDir + "have files to OCR");
            try {
                if (!tempFolder.exists()) {
                    tempFolder.mkdir();
                    log.info("Temp folder created");
                }

                File[] sourceDirFiles = sourceDir.listFiles();
                assert sourceDirFiles != null;
                for (File checkingFile : sourceDirFiles) {
                    String fileNameWithExtension = checkingFile.toString();
                    if (FilenameUtils.getExtension(fileNameWithExtension).equals("pdf")){
                        pdfConverter.pdfConvert(checkingFile);
                    } else FileUtils.copyFileToDirectory(checkingFile, tempFolder);
                }

                File[] outputTempFiles = tempFolder.listFiles();

                assert outputTempFiles != null;
                for (File ocrResult : outputTempFiles) {
                    log.info("Start OCR " + ocrResult.getName());
                    System.out.println(instance.doOCR(ocrResult));
                }

                FileUtils.forceDelete(tempFolder);
                log.info("Temp folder deleted");


            } catch (TesseractException e) {
                log.error("Error while reading image: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                log.error("Error while coping/deleting dir: " + e.getMessage());
                e.printStackTrace();
            }
        } else log.warn(sourceDir.getName() + " is empty or doesn't exist");
        log.info("OCR finished");
    }

}