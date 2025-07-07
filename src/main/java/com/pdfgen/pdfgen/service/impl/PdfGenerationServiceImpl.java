package com.pdfgen.pdfgen.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.pdfgen.pdfgen.entity.PdfDocument;
import com.pdfgen.pdfgen.entity.PdfItem;
import com.pdfgen.pdfgen.exception.custom.PdfGenerationException;
import com.pdfgen.pdfgen.repository.PdfDocumentRepository;
import com.pdfgen.pdfgen.service.PdfGenerationService;
import com.pdfgen.pdfgen.util.dto.PdfRequest;
import com.pdfgen.pdfgen.util.hashing.HashUtil;
import com.pdfgen.pdfgen.util.mapper.PdfRequestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerationServiceImpl implements PdfGenerationService {

    private final PdfDocumentRepository pdfDocumentRepository;
    private final PdfRequestMapper pdfRequestMapper;

    @Value("${pdf.storage.path:./pdf-storage}")
    private String pdfStoragePath;

    @Override
    @Transactional
    public Resource generateOrFetchPdfFile(PdfRequest request) {
        String documentHash = HashUtil.calculateHash(request);

        return pdfDocumentRepository.findByDocumentHash(documentHash)
                .map(existingDoc -> {
                    log.info("Reusing existing PDF: {}", existingDoc.getFileName());
                    return getPdfResource(existingDoc.getFilePath(), existingDoc.getFileName());
                })
                .orElseGet(() -> {
                    log.info("Generating new PDF");
                    String fileName = generateFileName();
                    Path filePath = Paths.get(pdfStoragePath).resolve(fileName);

                    try {
                        createStorageDirectoryIfNotExists();
                        generatePdfToFile(request, filePath);

                        PdfDocument pdfDoc = pdfRequestMapper.toEntity(request);
                        pdfDoc.setDocumentHash(documentHash);
                        pdfDoc.setFileName(fileName);
                        pdfDoc.setFilePath(filePath.toString());
                        pdfDoc.setCreatedAt(LocalDateTime.now());

                        // Fix: Set parent PdfDocument reference for each PdfItem
                        for (PdfItem item : pdfDoc.getItems()) {
                            item.setPdfDocument(pdfDoc);
                        }

                        pdfDocumentRepository.save(pdfDoc);
                        return getPdfResource(filePath.toString(), fileName);
                    } catch (Exception e) {
                        log.error("Error creating PDF", e);
                        throw new PdfGenerationException("Failed to generate PDF", e);
                    }
                });
    }

    private void generatePdfToFile(PdfRequest request, Path path) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(path.toFile()));
        document.open();
        addContent(document, request);
        document.close();
    }

    private Resource getPdfResource(String path, String fileName) {
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

    private void createStorageDirectoryIfNotExists() throws Exception {
        Path directory = Paths.get(pdfStoragePath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
    }

    private String generateFileName() {
        return "invoice_" + UUID.randomUUID() + ".pdf";
    }

    private void addContent(Document document, PdfRequest request) throws DocumentException {
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("INVOICE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);

        PdfPCell sellerCell = new PdfPCell();
        sellerCell.addElement(new Paragraph("Seller:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        sellerCell.addElement(new Paragraph(request.seller()));
        sellerCell.addElement(new Paragraph(request.sellerAddress()));
        sellerCell.addElement(new Paragraph("GSTIN: " + request.sellerGstin()));
        sellerCell.setBorder(Rectangle.BOX);
        headerTable.addCell(sellerCell);

        PdfPCell buyerCell = new PdfPCell();
        buyerCell.addElement(new Paragraph("Buyer:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        buyerCell.addElement(new Paragraph(request.buyer()));
        buyerCell.addElement(new Paragraph(request.buyerAddress()));
        buyerCell.addElement(new Paragraph("GSTIN: " + request.buyerGstin()));
        buyerCell.setBorder(Rectangle.BOX);
        headerTable.addCell(buyerCell);

        document.add(headerTable);
        document.add(Chunk.NEWLINE);

        PdfPTable itemsTable = new PdfPTable(4);
        itemsTable.setWidthPercentage(100);

        Stream.of("Item", "Quantity", "Rate", "Amount")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
                    itemsTable.addCell(header);
                });

        for (PdfRequest.Item item : request.items()) {
            itemsTable.addCell(item.name());
            itemsTable.addCell(item.quantity());
            itemsTable.addCell(String.format("%.2f", item.rate()));
            itemsTable.addCell(String.format("%.2f", item.amount()));
        }

        document.add(itemsTable);
    }
}
