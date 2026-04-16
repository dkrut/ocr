package com.github.dkrut;

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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PdfConverter {
    private static final int DPI = 300;

    private final Logger log = LoggerFactory.getLogger(PdfConverter.class);

    public List<File> convert(File pdfFile, String originalFileName, Path tempDir) {
        List<File> images = new ArrayList<>();
        File tempFolder = new File(tempDir.toFile(), "pdf-images");

        if (!pdfFile.exists()) {
            log.warn("'{}' doesn't exist", pdfFile.getName());
            return images;
        }

        log.info("Start converting '{}' to PNG images", originalFileName);
        try {
            tempFolder.mkdirs();

            try (PDDocument document = Loader.loadPDF(pdfFile)) {
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                int pagesCount = document.getNumberOfPages();
                log.info("Total pages to be converted: {}", pagesCount);

                String baseName = originalFileName.replace(".pdf", "");

                for (int pageNumber = 0; pageNumber < pagesCount; pageNumber++) {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(pageNumber, DPI, ImageType.RGB);
                    File imageFile = new File(tempFolder, baseName + "_" + pageNumber + ".png");
                    ImageIOUtil.writeImage(bim, imageFile.getPath(), DPI);
                    images.add(imageFile);
                }

                log.info("Converting '{}' finished", originalFileName);
            }
        } catch (IOException e) {
            log.error("Error while converting PDF: {}", e.getMessage());
            e.printStackTrace();
        }

        return images;
    }
}