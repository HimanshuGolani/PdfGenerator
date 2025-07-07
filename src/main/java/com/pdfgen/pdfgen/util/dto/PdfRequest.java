package com.pdfgen.pdfgen.util.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PdfRequest(
        @NotBlank(message = "Seller name is required")
        String seller,

        @NotBlank(message = "Seller GSTIN is required")
        @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
                message = "Invalid GSTIN format")
        String sellerGstin,

        @NotBlank(message = "Seller address is required")
        String sellerAddress,

        @NotBlank(message = "Buyer name is required")
        String buyer,

        @NotBlank(message = "Buyer GSTIN is required")
        @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
                message = "Invalid GSTIN format")
        String buyerGstin,

        @NotBlank(message = "Buyer address is required")
        String buyerAddress,

        @NotEmpty(message = "Items list cannot be empty")
        @Size(min = 1, message = "At least one item is required")
        @Valid
        List<Item> items
) {
    public record Item(
            @NotBlank(message = "Item name is required")
            String name,

            @NotBlank(message = "Item quantity is required")
            String quantity,

            @NotNull(message = "Item rate is required")
            Double rate,

            @NotNull(message = "Item amount is required")
            Double amount
    ) {}
}