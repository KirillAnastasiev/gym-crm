package com.kirill.projects.gymcrm.app.dto.mapper;

import com.kirill.projects.gymcrm.app.domain.TrainingStatistics;
import com.kirill.projects.gymcrm.app.dto.TrainingStatisticsDto;
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
