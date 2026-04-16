package com.github.dkrut;

import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

class Ocr {
    private static final String TESSDATA_PATH = "src/main/resources/tessdata";
    private static final int DPI = 300;

    private final Logger log = LoggerFactory.getLogger(Ocr.class);
    private final Tesseract tesseract;

    public Ocr() {
        tesseract = new Tesseract();
        tesseract.setDatapath(TESSDATA_PATH);
        tesseract.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_DEFAULT);
        tesseract.setVariable("user_defined_dpi", String.valueOf(DPI));
    }

    public String processFile(File file, String languages, Path tempDir) throws TesseractException {
        try {
            BufferedImage original = ImageIO.read(file);
            BufferedImage grayscale = ImageHelper.convertImageToGrayscale(original);
            
            File grayDir = new File(tempDir.toFile(), "grayscale");
            grayDir.mkdirs();
            
            String baseName = FilenameUtils.removeExtension(file.getName());
            File grayFile = new File(grayDir, baseName + "_gray.png");
            ImageIO.write(grayscale, "PNG", grayFile);
            
            tesseract.setLanguage(languages);
            return tesseract.doOCR(grayFile);
        } catch (IOException e) {
            throw new TesseractException(e.getMessage());
        }
    }
}