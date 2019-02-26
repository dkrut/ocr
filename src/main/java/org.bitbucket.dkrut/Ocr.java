package org.bitbucket.dkrut;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;

/**
 * Created by Denis Krutikov on 26.02.2019.
 */

public class Ocr {

    public static void main(String[] args) {

        File output = new File("src/main/resources/testFiles/");
//        System.out.println(output.listFiles().length);

        Tesseract instance = new Tesseract();
        instance.setDatapath("src/main/resources/tessdata");
        instance.setLanguage("rus+eng");

        File outputfile = new File(output + "\\test.png");

        try {
            System.out.println(instance.doOCR(outputfile));
        } catch (TesseractException e) {
            System.err.println("Error while reading image " + e.getMessage());
        }
    }
}