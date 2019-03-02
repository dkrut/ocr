package org.bitbucket.dkrut;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Denis Krutikov on 26.02.2019.
 */

public class Ocr {

    public static void main(String[] args) {

        File outputDir = new File("src/main/resources/testFiles/");
        File tempFolder = new File("src/main/resources/temp");

        PdfConverter pdfConverter = new PdfConverter();
        pdfConverter.pdfConvert();

        Tesseract instance = new Tesseract();
        instance.setDatapath("src/main/resources/tessdata");
        instance.setLanguage("rus+eng");

        try {

            if (!tempFolder.exists()) {
                tempFolder.mkdir();
            }

            FileUtils.copyDirectory(outputDir, tempFolder);
            File[] outputTempFiles = tempFolder.listFiles();
//            System.out.println(tempFolder.listFiles().length);

            if (outputDir.length() != 0) {
                for (File ocrResult : outputTempFiles) {
                    System.out.println(instance.doOCR(ocrResult));
                }
            }

            FileUtils.forceDelete(tempFolder);

        } catch (TesseractException e) {
            System.err.println("Error while reading image: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error while coping output dir: " + e.getMessage());
        }
    }
}