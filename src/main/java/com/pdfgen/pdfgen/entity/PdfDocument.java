package com.pdfgen.pdfgen.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pdf_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String documentHash;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String seller;

    @Column(nullable = false)
    private String sellerGstin;

    @Column(nullable = false)
    private String sellerAddress;

    @Column(nullable = false)
    private String buyer;

    @Column(nullable = false)
    private String buyerGstin;

    @Column(nullable = false)
    private String buyerAddress;

    @OneToMany(mappedBy = "pdfDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PdfItem> items;
}
