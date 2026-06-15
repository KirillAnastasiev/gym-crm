package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.TrainerStatus;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingStatistics;
import com.epam.laboratory.app.repository.TrainingStatisticsDao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TrainingStatisticsServiceImpl implements TrainingStatisticsService {

    private final TrainingStatisticsDao trainingStatisticsDao;

    @Logging(Level.INFO)
    @Override
    public void saveTrainingStatisticsForTraining(@NotNull Training training) {
        trainingStatisticsDao.findByTrainerUsername(training.getTrainerUsername())
                .map(stat -> updateCreatedStatistics(training, stat))
                .ifPresentOrElse(stat -> {
                            trainingStatisticsDao.save(stat);
                        },
                        () -> {
                            var stat = createNewTrainerStatistics(training);
                            trainingStatisticsDao.save(stat);
                        });
    }

    @Logging(Level.INFO)
    @Override
    public void deleteTrainingStatisticsForTraining(@NotNull Training training) {
        trainingStatisticsDao.findByTrainerUsername(training.getTrainerUsername())
                .ifPresent(stat -> stat.getYearStatisticsSet().stream()
                        .filter(ys -> ys.getYear().getValue() == training.getTrainingDate().getYear())
                        .findFirst()
                        .ifPresent(ys -> ys.getMonthStatistics().stream()
                                .filter(ms -> ms.getMonth() == training.getTrainingDate().getMonth())
                                .findFirst()
                                .ifPresent(ms -> {
                                        ms.setTotalDuration(ms.getTotalDuration().minus(training.getTrainingDuration()));
                                        trainingStatisticsDao.save(stat);
                                })
                        )
                );

    }


    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    @Override
    public Optional<TrainingStatistics> getStatisticsByTrainerUsername(@NotBlank String trainerUsername) {
        return trainingStatisticsDao.findByTrainerUsername(trainerUsername);
    }

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    @Override
    public Optional<TrainingStatistics> getStatisticsByTrainerUsernameInPeriod(@NotBlank String trainingUsername,
                                                                               @NotNull LocalDate startDate,
                                                                               @NotNull LocalDate endDate) {
        return trainingStatisticsDao.findByTrainerUsernameBetweenDates(trainingUsername,
                                                                       startDate.getYear(),
                                                                       startDate.getMonthValue(),
                                                                       endDate.getYear(),
                                                                       endDate.getMonthValue());
    }

    private TrainingStatistics updateCreatedStatistics(Training training, TrainingStatistics stat) {
        var yearStatistics = getOrCreateYearStatistics(training, stat);
        var monthStatistics = getOrCreateMonthStatistics(training, yearStatistics);
        monthStatistics.setTotalDuration(monthStatistics.getTotalDuration().plus(training.getTrainingDuration()));
        return stat;
    }

    private TrainingStatistics.MonthStatistics getOrCreateMonthStatistics(Training training, TrainingStatistics.YearStatistics yearStatistics) {
        return yearStatistics.getMonthStatistics().stream()
                .filter(ms -> ms.getMonth() == training.getTrainingDate().getMonth())
                .findFirst()
                .orElseGet(() -> {
                    var newMonthStatistics = createNewMonthStatistics(training);
                    yearStatistics.getMonthStatistics().add(newMonthStatistics);
                    return newMonthStatistics;
                });
    }

    private TrainingStatistics.YearStatistics getOrCreateYearStatistics(Training training, TrainingStatistics stat) {
        return stat.getYearStatisticsSet().stream()
                .filter(ys -> ys.getYear().getValue() == training.getTrainingDate().getYear())
                .findFirst()
                .orElseGet(() -> {
                    var newYearStatistics = createNewYearStatistics(training);
                    stat.getYearStatisticsSet().add(newYearStatistics);
                    return newYearStatistics;
                });
    }

    private TrainingStatistics createNewTrainerStatistics(Training training) {
        var statistics = new TrainingStatistics();
        statistics.setTrainerUsername(training.getTrainerUsername());
        statistics.setTrainerFirstName(training.getTrainerFirstName());
        statistics.setTrainerLastName(training.getTrainerLastName());
        statistics.setTrainerStatus(training.getTrainerStatus() == TrainerStatus.ACTIVE);

        var yearStatisticsSet = new HashSet<TrainingStatistics.YearStatistics>();
        yearStatisticsSet.add(createNewYearStatistics(training));
        statistics.setYearStatisticsSet(yearStatisticsSet);
        return statistics;
    }

    private TrainingStatistics.YearStatistics createNewYearStatistics(Training training) {
        var yearStatistics = new TrainingStatistics.YearStatistics();
        yearStatistics.setYear(Year.of(training.getTrainingDate().getYear()));

        var monthStatisticsSet = new HashSet<TrainingStatistics.MonthStatistics>();
        monthStatisticsSet.add(createNewMonthStatistics(training));
        yearStatistics.setMonthStatistics(monthStatisticsSet);

        return yearStatistics;
    }

    private TrainingStatistics.MonthStatistics createNewMonthStatistics(Training training) {
        var monthStatistics = new TrainingStatistics.MonthStatistics();
        monthStatistics.setMonth(training.getTrainingDate().getMonth());
        monthStatistics.setTotalDuration(training.getTrainingDuration());
        return monthStatistics;
    }

}
