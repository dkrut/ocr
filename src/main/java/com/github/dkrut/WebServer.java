package com.github.dkrut;

import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class WebServer {
    private static final String[] SUPPORTED_FORMATS = {"pdf", "png", "jpg", "jpeg", "tiff", "tif", "bmp"};

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(WebServer.class);

        Javalin.create(config -> {
            config.routes.get("/", ctx -> ctx.html(getHtml()));
            config.routes.post("/ocr", ctx -> {
                List<UploadedFile> files = ctx.uploadedFiles("file");
                if (files.isEmpty()) {
                    ctx.status(400).result("No file uploaded");
                    return;
                }

                UploadedFile uploadedFile = files.get(0);

                String languagesParam = ctx.formParam("languages");
                String languages = (languagesParam != null && !languagesParam.isEmpty())
                        ? languagesParam
                        : "eng";

                log.info("Starting OCR for file '{}' with languages: '{}'", uploadedFile.filename(), languages);

                try {
                    String result = processOcr(uploadedFile.content(), uploadedFile.filename(), languages);
                    ctx.result(result);
                    log.info("OCR completed for file: '{}'", uploadedFile.filename());
                } catch (Exception e) {
                    log.error("OCR failed for file '{}': {}", uploadedFile.filename(), e.getMessage());
                    ctx.status(500).result("Error: " + e.getMessage());
                }
            });
        }).start(8080);

        log.info("Server started at http://localhost:8080");
    }

    private static boolean isFormatSupported(String extension) {
        for (String fmt : SUPPORTED_FORMATS) {
            if (fmt.equals(extension)) return true;
        }
        return false;
    }

    private static String processOcr(InputStream inputStream, String fileName, String languages) throws TesseractException, IOException {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        if (!isFormatSupported(extension)) {
            throw new IllegalArgumentException("Unsupported file format: " + extension + ". Supported: PDF, PNG, JPG, JPEG, TIFF, BMP");
        }

        Path tempDir = Files.createTempDirectory("ocr-");
        File tempFile = tempDir.resolve(UUID.randomUUID() + "." + extension).toFile();

        try (inputStream) {
            Files.copy(inputStream, tempFile.toPath());
        }

        Ocr ocr = new Ocr();
        String result;

        if (extension.equals("pdf")) {
            PdfConverter pdfConverter = new PdfConverter();
            List<File> images = pdfConverter.convert(tempFile, fileName, tempDir);
            StringBuilder sb = new StringBuilder();
            for (File image : images) {
                sb.append(ocr.processFile(image, languages, tempDir)).append("\n");
            }
            result = sb.toString();
        } else {
            result = ocr.processFile(tempFile, languages, tempDir);
        }

        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach(p -> {
            try {
                Files.delete(p);
            } catch (IOException ignored) {
            }
        });

        return result;
    }

    private static String getHtml() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>OCR Tool</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f5f5f5; min-height: 100vh; display: flex; flex-direction: column; align-items: center; padding: 40px 20px; }
                        .container { width: 100%; max-width: 800px; }
                        h1 { font-size: 24px; font-weight: 600; color: #1a1a1a; margin-bottom: 32px; text-align: center; }
                        .drop-zone { background: #fff; border: 2px dashed #ddd; border-radius: 12px; padding: 48px 24px; text-align: center; transition: all 0.2s ease; cursor: pointer; }
                        .drop-zone:hover, .drop-zone.dragover { border-color: #333; background: #fafafa; }
                        .drop-zone input { display: none; }
                        .drop-zone-icon { width: 48px; height: 48px; margin: 0 auto 16px; background: #f0f0f0; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
                        .drop-zone-icon svg { width: 24px; height: 24px; stroke: #666; }
                        .drop-zone-text { font-size: 15px; color: #666; margin-bottom: 8px; }
                        .drop-zone-hint { font-size: 13px; color: #999; }
                        .button { display: inline-block; background: #1a1a1a; color: #fff; border: none; padding: 10px 20px; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s ease; }
                        .button:hover { background: #333; }
                        .button:disabled { background: #ccc; cursor: not-allowed; }
                        .button-secondary { background: #fff; color: #1a1a1a; border: 1px solid #ddd; }
                        .button-secondary:hover { background: #f5f5f5; }
                        .result-container { margin-top: 24px; display: none; }
                        .result-container.visible { display: block; }
                        .result-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
                        .result-title { font-size: 14px; font-weight: 500; color: #666; }
                        .result-actions { display: flex; gap: 8px; }
                        .result-text { background: #fff; border: 1px solid #eee; border-radius: 12px; padding: 20px; font-size: 14px; line-height: 1.6; color: #333; min-height: 200px; max-height: 500px; overflow-y: auto; white-space: pre-wrap; word-wrap: break-word; }
                        .loading { display: none; text-align: center; padding: 40px; }
                        .loading.visible { display: block; }
                        .spinner { width: 32px; height: 32px; border: 3px solid #eee; border-top-color: #333; border-radius: 50%; animation: spin 0.8s linear infinite; margin: 0 auto 16px; }
                        @keyframes spin { to { transform: rotate(360deg); } }
                        .file-name { margin-top: 12px; font-size: 13px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>OCR Tool</h1>
                        <div class="drop-zone" id="dropZone">
                            <input type="file" id="fileInput" accept=".pdf,.png,.jpg,.jpeg,.tiff,.bmp">
                            <div class="drop-zone-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                    <polyline points="17 8 12 3 7 8"/>
                                    <line x1="12" y1="3" x2="12" y2="15"/>
                                </svg>
                            </div>
                            <div class="drop-zone-text">Drag & drop or click to select file</div>
                            <div class="drop-zone-hint">PDF, PNG, JPG, TIFF, BMP</div>
                            <div class="file-name" id="fileName"></div>
                            <button class="button" id="uploadBtn" style="margin-top: 16px;">Select File</button>
                            <button class="button" id="startOcrBtn" style="margin-top: 16px; margin-left: 8px;" disabled>Start OCR</button>
                        </div>
                        <div style="margin-top: 16px; text-align: center;">
                            <span style="font-size: 14px; color: #666; margin-right: 12px;">Recognition languages:</span>
                            <label style="font-size: 14px; color: #333; margin-right: 16px; cursor: pointer;">
                                <input type="checkbox" name="language" value="eng" checked> English
                            </label>
                            <label style="font-size: 14px; color: #333; cursor: pointer;">
                                <input type="checkbox" name="language" value="rus"> Russian
                            </label>
                        </div>
                        <div class="loading" id="loading">
                            <div class="spinner"></div>
                            <div>Processing...</div>
                        </div>
                        <div class="result-container" id="resultContainer">
                            <div class="result-header">
                                <div class="result-title">Recognized Text</div>
                                <div class="result-actions">
                                    <button class="button button-secondary" id="copyBtn">Copy</button>
                                    <button class="button button-secondary" id="downloadBtn">Download</button>
                                </div>
                            </div>
                            <div class="result-text" id="resultText"></div>
                        </div>
                    </div>
                    <script>
                        const dropZone = document.getElementById('dropZone');
                        const fileInput = document.getElementById('fileInput');
                        const uploadBtn = document.getElementById('uploadBtn');
                        const startOcrBtn = document.getElementById('startOcrBtn');
                        const fileName = document.getElementById('fileName');
                        const loading = document.getElementById('loading');
                        const resultContainer = document.getElementById('resultContainer');
                        const resultText = document.getElementById('resultText');
                        const copyBtn = document.getElementById('copyBtn');
                        const downloadBtn = document.getElementById('downloadBtn');
                
                        let currentFile = null;
                        let currentFileName = '';
                        let recognizedText = '';
                        let fileSelected = false;
                
                        dropZone.addEventListener('click', () => fileInput.click());
                
                        dropZone.addEventListener('dragover', (e) => {
                            e.preventDefault();
                            dropZone.classList.add('dragover');
                        });
                
                        dropZone.addEventListener('dragleave', () => {
                            dropZone.classList.remove('dragover');
                        });
                
                        dropZone.addEventListener('drop', (e) => {
                            e.preventDefault();
                            dropZone.classList.remove('dragover');
                            if (e.dataTransfer.files.length) {
                                handleFile(e.dataTransfer.files[0]);
                            }
                        });
                
                        fileInput.addEventListener('change', () => {
                            if (fileInput.files.length) {
                                handleFile(fileInput.files[0]);
                            }
                        });
                
                        uploadBtn.addEventListener('click', (e) => {
                            e.stopPropagation();
                            fileInput.click();
                        });
                
                        startOcrBtn.addEventListener('click', (e) => {
                            e.stopPropagation();
                            if (currentFile && fileSelected) {
                                uploadFile();
                            }
                        });
                
                        function handleFile(file) {
                            currentFile = file;
                            let baseName = file.name.split('.')[0];
                            currentFileName = baseName.replace(/[^a-zA-Z0-9а-яА-ЯёЁ]/g, '_');
                            fileName.textContent = file.name;
                            fileSelected = true;
                            startOcrBtn.disabled = false;
                        }
                
                        async function uploadFile() {
                            if (!currentFile) return;
                
                            const formData = new FormData();
                            formData.append('file', currentFile);
                
                            const languages = Array.from(document.querySelectorAll('input[name="language"]:checked')).map(cb => cb.value);
                            formData.append('languages', languages.join('+'));
                
                            loading.classList.add('visible');
                            resultContainer.classList.remove('visible');
                
                            try {
                                const response = await fetch('/ocr', {
                                    method: 'POST',
                                    body: formData
                                });
                                recognizedText = await response.text();
                                resultText.textContent = recognizedText;
                                resultContainer.classList.add('visible');
                            } catch (error) {
                                resultText.textContent = 'Error: ' + error.message;
                                resultContainer.classList.add('visible');
                            } finally {
                                loading.classList.remove('visible');
                            }
                        }
                
                        copyBtn.addEventListener('click', () => {
                            navigator.clipboard.writeText(recognizedText).then(() => {
                                copyBtn.textContent = 'Copied!';
                                setTimeout(() => copyBtn.textContent = 'Copy', 2000);
                            });
                        });
                
                        downloadBtn.addEventListener('click', () => {
                            const blob = new Blob([recognizedText], { type: 'text/plain' });
                            const url = URL.createObjectURL(blob);
                            const a = document.createElement('a');
                            a.href = url;
                            a.download = 'ocr-result_' + currentFileName + '.txt';
                            a.click();
                            URL.revokeObjectURL(url);
                        });
                    </script>
                </body>
                </html>
                """;
    }
}