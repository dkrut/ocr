package org.bitbucket.dkrut;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Denis Krutikov on 26.02.2019.
 */

public class PdfConverter {
    private Logger log = LoggerFactory.getLogger(PdfConverter.class);

    public void pdfConvert(File pdfFile) {

        File tempFolder = new File("src/main/resources/temp");

        if (pdfFile.exists()) {
            try {
                if (tempFolder.exists()) {
                    log.warn("Previous temp folder is exist. Trying to delete");
                    FileUtils.forceDelete(tempFolder);
                    log.info("Previous temp folder deleted");
                }
                tempFolder.mkdir();
                log.info("Temp folder created");

                log.info("Start convert " + pdfFile.getName() + " to PNG image");
                PDDocument document = PDDocument.load(pdfFile);
                List<PDPage> list = document.getDocumentCatalog().getAllPages();
                log.info("Total pages to be converted: " + list.size());

                String fileName = pdfFile.getName().replace(".pdf", ""); //get file name without extension
                int pageNumber = 1;
                //convert pdf to png, adding index to all pages
                for (PDPage page : list) {
                    BufferedImage image = page.convertToImage();
                    File outputfile = new File(tempFolder.getPath() + "/" + fileName + "_" + pageNumber + ".png");
                    log.info("Image created: " + outputfile.getName());
                    ImageIO.write(image, "png", outputfile);
                    pageNumber++;
                }
                log.info("Converting " + pdfFile.getName() + " finished");
                document.close();

            } catch (IOException e) {
                log.error("Error while converting PDF to PNG" + e.getMessage());
                e.printStackTrace();
            }
        } else log.warn(pdfFile.getName() + " doesn't exist");
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