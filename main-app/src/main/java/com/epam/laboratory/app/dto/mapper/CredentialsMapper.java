package com.epam.laboratory.app.dto.mapper;

import com.epam.laboratory.app.domain.UserCredentials;
import com.epam.laboratory.app.dto.CredentialsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    CredentialsDto toDto(UserCredentials credentials);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    UserCredentials toEntity(CredentialsDto credentialsDto);
}
