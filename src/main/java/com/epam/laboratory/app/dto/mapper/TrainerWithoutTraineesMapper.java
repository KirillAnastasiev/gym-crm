package com.epam.laboratory.app.dto.mapper;

import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.dto.TrainerDto;
import com.epam.laboratory.app.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TrainingTypeMapper.class})
public interface TrainerWithoutTraineesMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "specialization", target = "specialization")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "trainees", target = "trainees", ignore = true)
    TrainerDto toDto(Trainer trainer);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "specialization", target = "specialization")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "trainees", target = "trainees", ignore = true)
    Trainer toEntity(TrainerDto trainerDto);

    @Mapping(source = "username", target = "username")
    Trainer toEntity(UserDto userDto);
}
