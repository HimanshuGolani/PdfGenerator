package com.pdfgen.pdfgen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pdf_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String documentHash;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String fileName;

    // Additional metadata can be added here
    private String seller;
    private String buyer;
}