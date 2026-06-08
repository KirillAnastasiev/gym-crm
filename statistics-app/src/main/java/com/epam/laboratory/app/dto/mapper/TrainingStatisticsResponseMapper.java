package com.epam.laboratory.app.dto.mapper;

import com.epam.laboratory.app.domain.TrainingStatistics;
import com.epam.laboratory.app.dto.TrainingStatisticsResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingStatisticsResponseMapper {
    @Mapping(source = "trainerUsername", target = "trainerUsername")
    @Mapping(source = "trainingSummary", target = "trainingSummary")
    TrainingStatisticsResponseDto toDto(TrainingStatistics trainingStatistics);
}
