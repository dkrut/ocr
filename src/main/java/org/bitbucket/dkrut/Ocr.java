package org.bitbucket.dkrut;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * Created by Denis Krutikov on 04.03.2019.
 */

public class Ocr {

    private Logger log = LoggerFactory.getLogger(Ocr.class);

    private Properties tesseractProperties = new Properties();
    private Tesseract instance = new Tesseract();

    private void getTesseractProperties(){
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/tesseract.properties");
            tesseractProperties.load(fis);
        } catch (IOException e) {
            log.error("Error while reading Tesseract properties: " + e.getMessage());
            e.printStackTrace();
        }

        instance.setDatapath("src/main/resources/tessdata");
        instance.setLanguage(tesseractProperties.getProperty("language"));
    }

    public void ocrToConsole(File fileToOcr){
        getTesseractProperties();
        try {
            log.info("Start OCR " + fileToOcr.getName());
            System.out.println(instance.doOCR(fileToOcr));
        } catch (TesseractException e) {
            log.error("Error while reading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void ocrToFile(File fileToOcr, File fileToWrite){
        getTesseractProperties();
        FileWriter fileWriter = null;
        log.info("Start OCR " + fileToOcr.getName());

        if (fileToWrite.exists()){
            log.warn(fileToWrite.getName() + " is already exist. OCR " + fileToOcr.getName() + " will be performed to this file");
        } else {
            try {
                fileToWrite.createNewFile();
                log.info("File " + fileToWrite.getName() + " created");
            } catch (IOException e){
                log.error("Error while creating file " + fileToWrite.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        try {
            fileWriter = new FileWriter(fileToWrite,true);
            fileWriter.write(instance.doOCR(fileToOcr));
            log.info("OCR " + fileToOcr.getName() + " to " + fileToWrite.getName() + " finished");

        } catch (TesseractException e) {
            log.error("Error while reading image: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Error while writing file " + fileToWrite.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }finally{
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    log.error("Error while closing fileWriter: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
