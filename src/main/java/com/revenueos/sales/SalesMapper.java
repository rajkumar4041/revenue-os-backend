package com.revenueos.sales;

import com.revenueos.sales.dto.LeadResponse;
import com.revenueos.sales.dto.OpportunityResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SalesMapper {

    LeadResponse toResponse(Lead lead);

    OpportunityResponse toResponse(Opportunity opportunity);
}
