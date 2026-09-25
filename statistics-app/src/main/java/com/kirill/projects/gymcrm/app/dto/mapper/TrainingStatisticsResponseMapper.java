package com.kirill.projects.gymcrm.app.dto.mapper;

import com.kirill.projects.gymcrm.app.domain.TrainingStatistics;
import com.kirill.projects.gymcrm.app.dto.TrainingStatisticsResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Duration;
import java.time.Month;
import java.time.Year;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Mapper(componentModel = "spring")
public interface TrainingStatisticsResponseMapper {
    @Mapping(source = "trainerUsername", target = "trainerUsername")
    @Mapping(source = "yearStatisticsSet", target = "trainingSummary", qualifiedByName = "toTrainingSummary")
    TrainingStatisticsResponseDto toDto(TrainingStatistics trainingStatistics);

    @Named("toTrainingSummary")
    default Map<Year, Map<Month, Duration>> toTrainingSummary(Set<TrainingStatistics.YearStatistics> yearsStatistics) {
        return yearsStatistics.stream()
                .collect(toMap(
                        TrainingStatistics.YearStatistics::getYear,
                        ys -> ys.getMonthStatistics().stream()
                                .collect(toMap(
                                        TrainingStatistics.MonthStatistics::getMonth,
                                        TrainingStatistics.MonthStatistics::getTotalDuration
                                ))
                ));

    }

}
