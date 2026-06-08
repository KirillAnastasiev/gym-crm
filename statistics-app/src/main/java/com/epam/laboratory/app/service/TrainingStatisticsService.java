package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.TrainingStatistics;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public interface TrainingStatisticsService {
    TrainingStatistics getStatisticsForTrainer(@NotBlank String trainerUsername);
    TrainingStatistics getStatisticsForTrainerInPeriod(@NotBlank String trainingUsername,
                                                       @NotNull LocalDate startDate,
                                                       @NotNull LocalDate endDate);
}
