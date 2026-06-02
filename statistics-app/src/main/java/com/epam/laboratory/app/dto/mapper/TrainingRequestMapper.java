package com.epam.laboratory.app.dto.mapper;

import com.epam.laboratory.app.domain.TrainerStatus;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.dto.TrainingRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TrainingRequestMapper {
    @Mapping(source = "trainerUsername", target = "trainerUsername")
    @Mapping(source = "trainerFirstName", target = "trainerFirstName")
    @Mapping(source = "trainerLastName", target = "trainerLastName")
    @Mapping(source = "isActive", target = "trainerStatus", qualifiedByName = "mapTrainerStatus")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingDuration", target = "trainingDuration")
    Training toEntity(TrainingRequestDto dto);

    @Named("mapTrainerStatus")
    default TrainerStatus mapTrainerStatus(Boolean isActive) {
        return isActive ? TrainerStatus.ACTIVE : TrainerStatus.INACTIVE;
    }
}
