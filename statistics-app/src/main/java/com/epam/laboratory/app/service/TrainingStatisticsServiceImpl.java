package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingStatistics;
import com.epam.laboratory.app.exception.NoContentException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Validated
public class TrainingStatisticsServiceImpl implements TrainingStatisticsService {

    private final TrainingService trainingService;

    @Override
    public TrainingStatistics getStatisticsForTrainer(@NotBlank String trainerUsername) {
        try {
            log.debug("Getting training report for trainer {}", trainerUsername);
            var trainings = trainingService.getTrainingsByTrainerUsername(trainerUsername);
            return getTrainingStatistics(trainerUsername, trainings);
        } catch (Exception e) {
            log.warn("Error while getting training report for trainer {}: {}", trainerUsername, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public TrainingStatistics getStatisticsForTrainerInPeriod(@NotBlank String trainingUsername,
                                                              @NotNull LocalDate startDate,
                                                              @NotNull LocalDate endDate) {
        try {
            log.debug("Getting training report for trainer {} between dates {} and {}", trainingUsername, startDate, endDate);
            var trainings = trainingService.getTrainingsByTrainerUsernameBetweenDates(trainingUsername, startDate, endDate);
            return getTrainingStatistics(trainingUsername, trainings);
        } catch (Exception e) {
            log.warn("Error while getting training report for trainer {} between dates: {}", trainingUsername, e.getMessage(), e);
            throw e;
        }
    }

    private static Map<Year, Map<Month, Duration>> getTrainingsSummary(Collection<? extends Training> trainings) {
        return trainings.stream()
                .collect(groupingBy(training -> Year.from(training.getTrainingDate()),
                        groupingBy(training -> training.getTrainingDate().getMonth(),
                                reducing(Duration.ZERO, Training::getTrainingDuration, Duration::plus))));

    }

    private static TrainingStatistics getTrainingStatistics(String trainerUsername, Collection<Training> trainings) {
        var summary = getTrainingsSummary(trainings);
        if (summary.isEmpty()) {
            throw new NoContentException("No trainings found for trainer %s".formatted(trainerUsername));
        }
        var report = new TrainingStatistics();
        report.setTrainerUsername(trainerUsername);
        report.setTrainingSummary(summary);
        log.debug("Training report for trainer {} got", trainerUsername);
        return report;
    }

}
