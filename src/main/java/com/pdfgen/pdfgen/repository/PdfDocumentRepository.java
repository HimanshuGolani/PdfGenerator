package com.pdfgen.pdfgen.repository;

import com.pdfgen.pdfgen.entity.PdfDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PdfDocumentRepository extends JpaRepository<PdfDocument, UUID> {
    Optional<PdfDocument> findByDocumentHash(String documentHash);
}