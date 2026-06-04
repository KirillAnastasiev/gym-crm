package com.epam.laboratory.app.client;


import com.epam.laboratory.app.domain.TrainingStatistics;

import java.time.LocalDate;

public interface TrainingStatisticsClient {
    TrainingStatistics getTrainingStatisticsForTrainerInPeriod(String trainingUsername, LocalDate fromDate, LocalDate toDate);
}
