package com.pdfgen.pdfgen.service;

import com.pdfgen.pdfgen.util.dto.PdfRequest;
import org.springframework.core.io.Resource;

public interface PdfGenerationService {
    Resource generateOrFetchPdfFile(PdfRequest request);
}
