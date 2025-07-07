package com.pdfgen.pdfgen.controller;

import com.pdfgen.pdfgen.service.PdfGenerationService;
import com.pdfgen.pdfgen.util.dto.PdfRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pdf")
@RequiredArgsConstructor
@Tag(name = "PDF Generator", description = "API for generating and downloading PDF documents")
public class PdfGeneratorController {

    private final PdfGenerationService pdfGenerationService;

    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Generate or Fetch PDF", description = "Generate a PDF if it doesn't exist, or fetch the existing one")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF file returned successfully",
                    content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "PDF file not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Resource> generateAndDownloadPdf(
            @Valid @RequestBody PdfRequest request) {
        try {
            Resource pdfResource = pdfGenerationService.generateOrFetchPdfFile(request);

            // Check if the PDF exists or not
            if (pdfResource == null || !pdfResource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + pdfResource.getFilename() + "\"")
                    .body(pdfResource);

        } catch (Exception e) {
            // Optionally log the exception here
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
