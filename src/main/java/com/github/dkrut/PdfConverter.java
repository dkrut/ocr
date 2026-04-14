package com.github.dkrut;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfConverter {
    private static final String TEMP_DIR = "temp";
    private static final int DPI = 300;

    private final Logger log = LoggerFactory.getLogger(PdfConverter.class);

    public File pdfConvert(File pdfFile) {
        File tempFolder = new File(TEMP_DIR);
        if (pdfFile.exists()) {
            log.info("Start converting {} to PNG images", pdfFile.getName());
            try {
                if (tempFolder.exists()) {
                    log.warn("Previous temp folder is exist. Trying to delete");
                    FileUtils.forceDelete(tempFolder);
                    log.info("Previous temp folder deleted");
                }
                tempFolder.mkdir();
                log.debug("Temp folder created");

                try (PDDocument document = Loader.loadPDF(pdfFile)) {
                    PDFRenderer pdfRenderer = new PDFRenderer(document);
                    int pagesCount = document.getNumberOfPages();
                    log.info("Total pages to be converted: {}", pagesCount);

                    String fileName = pdfFile.getName().replace(".pdf", "");
                    for (int pageNumber = 0; pageNumber < pagesCount; pageNumber++) {
                        BufferedImage bim = pdfRenderer.renderImageWithDPI(pageNumber, DPI, ImageType.RGB);
                        ImageIOUtil.writeImage(bim, tempFolder.getPath() + File.separator + fileName + "_" + pageNumber + ".png", DPI);
                    }
                    log.info("Converting {} finished", pdfFile.getName());
                }
                return tempFolder;
            } catch (IOException e) {
                log.error("Error while converting PDF to PNG{}", e.getMessage());
                e.printStackTrace();
            }
        } else {
            log.warn(pdfFile.getName() + " doesn't exist");
        }
        return tempFolder;
    }
}