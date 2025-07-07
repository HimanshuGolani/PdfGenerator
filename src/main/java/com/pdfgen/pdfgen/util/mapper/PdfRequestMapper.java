package com.pdfgen.pdfgen.util.mapper;

import com.pdfgen.pdfgen.entity.PdfDocument;
import com.pdfgen.pdfgen.entity.PdfItem;
import com.pdfgen.pdfgen.util.dto.PdfRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PdfRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "documentHash", ignore = true)
    @Mapping(target = "filePath", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "items", source = "items")
    PdfDocument toEntity(PdfRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pdfDocument", ignore = true)
    PdfItem toItemEntity(PdfRequest.Item item);

    List<PdfItem> toItemEntityList(List<PdfRequest.Item> items);

    @AfterMapping
    default void linkItemsToDocument(@MappingTarget PdfDocument document) {
        if (document.getItems() != null) {
            for (PdfItem item : document.getItems()) {
                item.setPdfDocument(document);
            }
        }
    }
}
