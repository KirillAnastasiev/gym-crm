package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.TrainingStatistics;

import java.time.LocalDate;

public interface TrainingStatisticsService {
    TrainingStatistics getTrainingStatisticsForTrainerInPeriod(String username, LocalDate fromDate, LocalDate toDate);
}
