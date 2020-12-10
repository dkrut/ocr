package com.github.dkrut;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Denis Krutikov on 26.02.2019.
 */

public class PdfConverter {
    private Logger log = LoggerFactory.getLogger(PdfConverter.class);

    public void pdfConvert(File pdfFile) {

        File tempFolder = new File("temp");

        if (pdfFile.exists()) {
            log.info("Start convert " + pdfFile.getName() + " to PNG image");
            try {
                if (tempFolder.exists()) {
                    log.warn("Previous temp folder is exist. Trying to delete");
                    FileUtils.forceDelete(tempFolder);
                    log.info("Previous temp folder deleted");
                }
                tempFolder.mkdir();
                log.info("Temp folder created");

                PDDocument document = PDDocument.load(pdfFile);
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                int pagesCount = document.getNumberOfPages();
                log.info("Total pages to be converted: " + pagesCount);

                String fileName = pdfFile.getName().replace(".pdf", "");//get file name

                //convert pdf to png, adding index to all pages
                for (int pageNumber = 0; pageNumber < pagesCount; pageNumber++) {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(pageNumber, 300, ImageType.RGB);
                    ImageIOUtil.writeImage(bim, tempFolder.getPath() + "/" +  fileName + "_" + pageNumber + ".png", 300); //suffix in filename will be used as the file format
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
                    PDFRenderer pdfRenderer = new PDFRenderer(document);

                    String fileName = tempDocument.getName().replace(".pdf", ""); //get file name without extension

                    int pagesCount = document.getNumberOfPages();
                    log.info("Total pages to be converted: " + pagesCount);

                    //convert pdf to png, adding index to all pages
                    for (int pageNumber = 0; pageNumber < pagesCount; pageNumber++) {
                        BufferedImage bim = pdfRenderer.renderImageWithDPI(pageNumber, 300, ImageType.RGB);
                        ImageIOUtil.writeImage(bim, tempFolder.getPath() + fileName + "_" + pageNumber + ".png", 300); //suffix in filename will be used as the file format
                    }
                    log.info("Converting " + tempDocument.getName() + " finished");
                    document.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}