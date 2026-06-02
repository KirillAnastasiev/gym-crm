package com.epam.laboratory.app.dto.mapper;

import com.epam.laboratory.app.domain.TrainingReport;
import com.epam.laboratory.app.dto.TrainingReportRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TrainingReportMapper {
    @Mapping(source = "trainerUsername", target = "trainerUsername")
    @Mapping(source = "trainerFirsName", target = "trainerFirsName")
    @Mapping(source = "trainerLastName", target = "trainerLastName")
    @Mapping(source = "isActive", target = "isActive")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingDuration", target = "trainingDuration")
    @Mapping(source = "actionType", target = "actionType", qualifiedByName = "mapActionType")
    TrainingReportRequestDto toDto(TrainingReport entity);

    @Named("mapActionType")
    static String mapActionType(TrainingReport.ActionType actionType) {
        return actionType.name();
    }
}
