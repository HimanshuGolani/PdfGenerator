package com.pdfgen.pdfgen.util.hashing;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.pdfgen.pdfgen.util.dto.PdfRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HashUtil {

    public static String calculateHash(PdfRequest request) {
        return String.valueOf(request.hashCode());
    }

    public static void generatePdfToFile(PdfRequest request, Path path) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(path.toFile()));
        document.open();
        addContent(document, request);
        document.close();
    }

    public static Resource getPdfResource(String path, String fileName) {
        try {
            Path filePath = Paths.get(path).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("PDF file not found: " + fileName);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error reading PDF file: " + fileName, e);
        }
    }

    // Dummy implementation — you should replace this with actual PDF content logic
    private static void addContent(Document document, PdfRequest request) throws DocumentException {
        document.add(new Paragraph("Invoice for: " + request.seller() + " to " + request.buyer()));
    }
}
