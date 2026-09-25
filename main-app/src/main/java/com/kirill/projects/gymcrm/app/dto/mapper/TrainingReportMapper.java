package com.kirill.projects.gymcrm.app.dto.mapper;

import com.kirill.projects.gymcrm.app.domain.TrainingReport;
import com.kirill.projects.gymcrm.app.dto.TrainingReportRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingReportMapper {
    @Mapping(source = "trainerUsername", target = "trainerUsername")
    @Mapping(source = "trainerFirsName", target = "trainerFirsName")
    @Mapping(source = "trainerLastName", target = "trainerLastName")
    @Mapping(source = "isActive", target = "isActive")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingDuration", target = "trainingDuration")
    TrainingReportRequestDto toDto(TrainingReport entity);
}
