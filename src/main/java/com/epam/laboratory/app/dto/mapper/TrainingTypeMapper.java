package com.epam.laboratory.app.dto.mapper;

import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.dto.TrainingTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "trainingTypeName", target = "trainingTypeName")
    TrainingTypeDto toDto(TrainingType trainingType);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "trainingTypeName", target = "trainingTypeName")
    TrainingType toEntity(TrainingTypeDto trainingTypeDto);
}
