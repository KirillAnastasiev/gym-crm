package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.client.TrainingStatisticsClient;
import com.epam.laboratory.app.domain.TrainingStatistics;
import com.epam.laboratory.app.util.InputDataValidator;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TrainingStatisticsServiceImpl implements TrainingStatisticsService {

    private final TrainingStatisticsClient statisticsClient;

    @Logging(Level.INFO)
    @CircuitBreaker(name = "trainingStatisticsService", fallbackMethod = "getFallbackStatistics")
    @Override
    public TrainingStatistics getTrainingStatisticsForTrainerInPeriod(String username, LocalDate fromDate, LocalDate toDate) {
        InputDataValidator.validateNotBlank(username, "Trainer username");
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("From date must be before to date");
        }
        return statisticsClient.getTrainingStatisticsForTrainerInPeriod(username, fromDate, toDate);
    }

    public TrainingStatistics getFallbackStatistics(String username, LocalDate fromDate, LocalDate toDate, Exception ex) {
        var statistics = new TrainingStatistics();
        statistics.setTrainerUsername(username);
        statistics.setTrainingSummary(Collections.emptyMap());
        return statistics;
    }
}
