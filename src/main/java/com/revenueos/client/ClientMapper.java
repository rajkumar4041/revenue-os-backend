package com.revenueos.client;

import com.revenueos.client.dto.ClientResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientResponse toResponse(Client client);
}
