package org.bitbucket.dkrut;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;

/**
 * Created by Denis Krutikov on 26.02.2019.
 */

public class Ocr {

    public static void main(String[] args) {

        File outputDir = new File("src/main/resources/testFiles/");
        File[] outputFiles = outputDir.listFiles();
//        System.out.println(outputDir.listFiles().length);

        Tesseract instance = new Tesseract();
        instance.setDatapath("src/main/resources/tessdata");
        instance.setLanguage("rus+eng");

        try {
            if (outputDir.length() != 0) {
                for (File ocrResult : outputFiles) {
                    System.out.println(instance.doOCR(ocrResult));
                }
            }
        } catch (TesseractException e) {
            System.err.println("Error while reading image " + e.getMessage());
        }
    }
}