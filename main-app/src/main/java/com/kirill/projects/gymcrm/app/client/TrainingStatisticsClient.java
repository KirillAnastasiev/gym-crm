package com.kirill.projects.gymcrm.app.client;


import com.kirill.projects.gymcrm.app.domain.TrainingStatistics;

import java.time.LocalDate;

public interface TrainingStatisticsClient {
    TrainingStatistics getTrainingStatisticsForTrainerInPeriod(String trainingUsername, LocalDate fromDate, LocalDate toDate);
}
