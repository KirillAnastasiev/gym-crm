package com.epam.laboratory.app.dto.mapper;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.dto.CredentialsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TraineeCredentialsMapper {
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    CredentialsDto toDto(Trainee trainee);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    Trainee toEntity(CredentialsDto credentialsDto);
}
