package com.epam.laboratory.app.dto.mapper;

import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.dto.CredentialsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerCredentialsMapper {
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    CredentialsDto toDto(Trainer trainer);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    Trainer toEntity(CredentialsDto credentialsDto);
}
