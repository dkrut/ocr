package org.bitbucket.dkrut;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Created by Denis Krutikov on 26.02.2019.
 */

public class PdfConverter {
    public void pdfConvert() {

        String sourceDir = "src/main/resources/pdf/test.pdf";
        String tempDir = "src/main/resources/temp";
        File sourceFile = new File(sourceDir);
        File tempFolder = new File(tempDir);

        try {
            if (!tempFolder.exists()) {
                tempFolder.mkdir();
            }
            if (sourceFile.exists()) {
                PDDocument document = PDDocument.load(sourceDir);
                List<PDPage> list = document.getDocumentCatalog().getAllPages();

                String fileName = sourceFile.getName().replace(".pdf", ""); //get file name without extension
                int pageNumber = 1;
                //convert pdf to png, adding index to all pages
                for (PDPage page : list) {
                    BufferedImage image = page.convertToImage();
                    File outputfile = new File(tempDir + "/" + fileName + "_" + pageNumber + ".png");
                    ImageIO.write(image, "png", outputfile);
                    pageNumber++;
                }
                document.close();
            } else {
                System.err.println(sourceFile.getName() +" - file not exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}