package com.pdfgen.pdfgen.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "pdf_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String quantity;

    @Column(nullable = false)
    private Double rate;

    @Column(nullable = false)
    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdf_document_id", nullable = false)
    private PdfDocument pdfDocument;
}
