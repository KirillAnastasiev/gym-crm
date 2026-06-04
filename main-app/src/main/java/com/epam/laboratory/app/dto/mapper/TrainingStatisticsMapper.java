package com.epam.laboratory.app.dto.mapper;

import com.epam.laboratory.app.domain.TrainingStatistics;
import com.epam.laboratory.app.dto.TrainingStatisticsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingStatisticsMapper {
    @Mapping(source = "trainerUsername", target = "trainerUsername")
    @Mapping(source = "trainingSummary", target = "trainingSummary")
    TrainingStatistics toEntity(TrainingStatisticsDto trainingStatisticsDto);

    @Mapping(source = "trainerUsername", target = "trainerUsername")
    @Mapping(source = "trainingSummary", target = "trainingSummary")
    TrainingStatisticsDto toDto(TrainingStatistics trainingStatistics);
}
