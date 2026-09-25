package com.kirill.projects.gymcrm.app.service;

import com.kirill.projects.gymcrm.app.domain.TrainingStatistics;

import java.time.LocalDate;

public interface TrainingStatisticsService {
    TrainingStatistics getTrainingStatisticsForTrainerInPeriod(String username, LocalDate fromDate, LocalDate toDate);
}
