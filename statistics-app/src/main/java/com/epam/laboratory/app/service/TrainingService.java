package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Training;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Collection;

public interface TrainingService {
    void addTraining(@NotNull Training training);
    void deleteTraining(@NotNull Training training);
    Collection<Training> getTrainingsByTrainerUsername(@NotBlank String trainerUsername);
    Collection<Training> getTrainingsByTrainerUsernameBetweenDates(@NotBlank String trainerUsername, @NotNull LocalDate fromDate, @NotNull LocalDate toDate);
}
