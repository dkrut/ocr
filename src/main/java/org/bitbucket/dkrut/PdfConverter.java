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
    public void pdfConvert(File pdfFile) {

        File tempFolder = new File("src/main/resources/temp");

        if (pdfFile.exists()) {
            try {
                if (!tempFolder.exists()) {
                    tempFolder.mkdir();
                }

                PDDocument document = PDDocument.load(pdfFile);
                List<PDPage> list = document.getDocumentCatalog().getAllPages();

                String fileName = pdfFile.getName().replace(".pdf", ""); //get file name without extension
                int pageNumber = 1;
                //convert pdf to png, adding index to all pages
                for (PDPage page : list) {
                    BufferedImage image = page.convertToImage();
                    File outputfile = new File(tempFolder.getPath() + "/" + fileName + "_" + pageNumber + ".png");
                    ImageIO.write(image, "png", outputfile);
                    pageNumber++;
                }
                document.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pdfConvertAll() {

        File sourcePdfDir = new File("src/main/resources/pdf");
        File tempFolder = new File("src/main/resources/temp");

        if (sourcePdfDir.listFiles() != null) {
            try {
                File[] sourceTempFiles = sourcePdfDir.listFiles();

                if (!tempFolder.exists()) {
                    tempFolder.mkdir();
                }

                assert sourceTempFiles != null;
                for (File tempDocument : sourceTempFiles) {
                    PDDocument document = PDDocument.load(tempDocument);
                    List<PDPage> list = document.getDocumentCatalog().getAllPages();

                    String fileName = tempDocument.getName().replace(".pdf", ""); //get file name without extension
                    int pageNumber = 1;
                    //convert pdf to png, adding index to all pages
                    for (PDPage page : list) {
                        BufferedImage image = page.convertToImage();
                        File outputfile = new File(tempFolder.getPath() + "/" + fileName + "_" + pageNumber + ".png");
                        ImageIO.write(image, "png", outputfile);
                        pageNumber++;
                    }
                    document.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}