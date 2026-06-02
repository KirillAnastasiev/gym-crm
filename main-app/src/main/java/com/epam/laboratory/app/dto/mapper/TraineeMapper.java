package com.epam.laboratory.app.dto.mapper;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.dto.TraineeDto;
import com.epam.laboratory.app.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TrainerWithoutTraineesMapper.class})
public interface TraineeMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password", ignore = true)
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "trainers", target = "trainers", defaultExpression = "java(new java.util.ArrayList<>())")
    TraineeDto toDto(Trainee trainee);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "trainers", target = "trainers", defaultExpression = "java(new java.util.ArrayList<>())")
    Trainee toEntity(TraineeDto traineeDto);

    @Mapping(source = "username", target = "username")
    Trainee toEntity(UserDto userDto);
}
