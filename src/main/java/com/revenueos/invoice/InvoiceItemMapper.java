package com.revenueos.invoice;

import com.revenueos.invoice.dto.InvoiceItemResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceItemMapper {

    InvoiceItemResponse toResponse(InvoiceItem item);

    List<InvoiceItemResponse> toResponseList(List<InvoiceItem> items);
}
