# OCR Tool

OCR application for text recognition from images using Tesseract.

## Features

- Text recognition from images (PNG, JPG, JPEG, TIFF, BMP)
- Text recognition from PDF files
- Web interface for file upload
- Language selection for recognition (English, Russian)
- Export result (copy, download)

## Requirements

- Java 17
- Maven
- Tesseract language data (eng.traineddata, rus.traineddata) in `src/main/resources/tessdata/`

## Run

```bash
mvn exec:java -D"exec.mainClass=com.github.dkrut.WebServer"
```

Open http://localhost:8080

## Web Interface Usage

1. Select file (drag & drop or "Select File" button)
2. Select language(s) - English, Russian
3. Click "Start OCR"
4. Copy or download result

## Architecture

### Classes

- `Ocr` - image processing via Tesseract (grayscale preprocessing)
- `PdfConverter` - PDF to PNG conversion
- `WebServer` - web interface (Javalin)

### Processing Flow

1. File uploaded via web interface
2. Temporary folder created: `TEMP/ocr-{uuid}/`
3. For PDF: converted to PNG (`pdf-images/`)
4. Image converted to grayscale (`grayscale/`)
5. Text recognized by Tesseract
6. Temporary files deleted