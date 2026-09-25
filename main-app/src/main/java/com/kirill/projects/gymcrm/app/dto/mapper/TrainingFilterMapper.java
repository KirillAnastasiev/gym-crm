package com.kirill.projects.gymcrm.app.dto.mapper;

import com.kirill.projects.gymcrm.app.domain.TrainingFilter;
import com.kirill.projects.gymcrm.app.dto.TrainingFilterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingFilterMapper {
    @Mapping(source = "traineeUsername", target = "traineeUsername")
    @Mapping(source = "trainerUsername", target = "trainerUsername")
    @Mapping(source = "trainingTypeName", target = "trainingTypeName")
    @Mapping(source = "periodFrom", target = "periodFrom")
    @Mapping(source = "periodTo", target = "periodTo")
    TrainingFilterDto toDto(TrainingFilter trainingFilter);

    @Mapping(source = "traineeUsername", target = "traineeUsername")
    @Mapping(source = "trainerUsername", target = "trainerUsername")
    @Mapping(source = "trainingTypeName", target = "trainingTypeName")
    @Mapping(source = "periodFrom", target = "periodFrom")
    @Mapping(source = "periodTo", target = "periodTo")
    TrainingFilter toEntity(TrainingFilterDto trainingFilterDto);
}
