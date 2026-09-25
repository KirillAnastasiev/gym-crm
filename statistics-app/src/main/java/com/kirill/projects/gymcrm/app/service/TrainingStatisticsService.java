package com.kirill.projects.gymcrm.app.service;

import com.kirill.projects.gymcrm.app.domain.Training;
import com.kirill.projects.gymcrm.app.domain.TrainingStatistics;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Optional;

public interface TrainingStatisticsService {
    void saveTrainingStatisticsForTraining(@NotBlank Training training);
    void deleteTrainingStatisticsForTraining(@NotBlank Training training);
    Optional<TrainingStatistics> getStatisticsByTrainerUsername(@NotBlank String trainerUsername);
    Optional<TrainingStatistics> getStatisticsByTrainerUsernameInPeriod(@NotBlank String trainingUsername, @NotNull LocalDate startDate, @NotNull LocalDate endDate);
}
