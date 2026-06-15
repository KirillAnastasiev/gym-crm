package com.epam.laboratory.app.service;


import com.epam.laboratory.app.domain.TrainerStatus;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingStatistics;
import com.epam.laboratory.app.repository.TrainingStatisticsDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingStatisticsServiceImpl test suite")
class TrainingStatisticsServiceImplTest {

    @Mock
    private TrainingStatisticsDao trainingStatisticsDao;

    @InjectMocks
    private TrainingStatisticsServiceImpl trainingStatisticsServiceImpl;


    // ==================== SAVE TRAINING STATISTICS FOR TRAINING TESTS ====================

    @Test
    @DisplayName("Test of the method saveTrainingStatisticsForTraining - should save training statistics when trainer username does not exist")
    void testSaveTrainingStatisticsForTraining_positive() {
        // given
        var username = "Sarah.Davis";
        var training = createTestTraining();
        var statistics = createTestTrainingStatistics();

        given(trainingStatisticsDao.findByTrainerUsername(anyString())).willReturn(Optional.empty());
        given(trainingStatisticsDao.save(any(TrainingStatistics.class))).willReturn(statistics);

        // when & then
        assertThatNoException().isThrownBy(() ->
                trainingStatisticsServiceImpl.saveTrainingStatisticsForTraining(training));

        verify(trainingStatisticsDao, times(1)).findByTrainerUsername(username);
        verify(trainingStatisticsDao, times(1)).save(any(TrainingStatistics.class));
        verifyNoMoreInteractions(trainingStatisticsDao);
    }


    // ==================== DELETE TRAINING STATISTICS FOR TRAINING TESTS ====================

    @Test
    @DisplayName("Test of the method deleteTrainingStatisticsForTraining - should delete training statistics when trainer username exists")
    void testDeleteTrainingStatisticsForTraining_positive() {
        // given
        var training = createTestTraining();
        var statistics = createTestTrainingStatistics();

        given(trainingStatisticsDao.findByTrainerUsername(anyString())).willReturn(Optional.of(statistics));
        given(trainingStatisticsDao.save(any(TrainingStatistics.class))).willReturn(statistics);

        // when & then
        assertThatNoException().isThrownBy(() ->
                trainingStatisticsServiceImpl.deleteTrainingStatisticsForTraining(training));

        verify(trainingStatisticsDao, times(1)).findByTrainerUsername(anyString());
        verify(trainingStatisticsDao, times(1)).save(any(TrainingStatistics.class));
        verifyNoMoreInteractions(trainingStatisticsDao);
    }


    // ==================== GET STATISTICS BY TRAINER USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method getStatisticsByTrainerUsername - should return statistics when trainer username exists")
    void testGetStatisticsByTrainerUsername_positive() {
        // given
        var username = "Sarah.Davis";
        var statistics = createTestTrainingStatistics();

        given(trainingStatisticsDao.findByTrainerUsername(anyString())).willReturn(Optional.of(statistics));

        // when
        var actualResult = trainingStatisticsServiceImpl.getStatisticsByTrainerUsername(username);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult.map(TrainingStatistics::getId)).isNotEmpty();
        assertThat(actualResult.map(TrainingStatistics::getTrainerUsername)).hasValue(username);

        verify(trainingStatisticsDao, times(1)).findByTrainerUsername(anyString());
        verifyNoMoreInteractions(trainingStatisticsDao);
    }

    @Test
    @DisplayName("Test of the method getStatisticsByTrainerUsername - should return empty when trainer username does not exist")
    void testGetStatisticsByTrainerUsername_negative_unexistedUser() {
        // given
        var username = "Unknown.Username";

        given(trainingStatisticsDao.findByTrainerUsername(anyString())).willReturn(Optional.empty());

        // when
        var actualResult = trainingStatisticsServiceImpl.getStatisticsByTrainerUsername(username);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(trainingStatisticsDao, times(1)).findByTrainerUsername(anyString());
        verifyNoMoreInteractions(trainingStatisticsDao);
    }


    private Training createTestTraining() {
        var training = new Training();
        training.setTrainerFirstName("Sarah");
        training.setTrainerLastName("Davis");
        training.setTrainerUsername("Sarah.Davis");
        training.setTrainerStatus(TrainerStatus.ACTIVE);
        training.setTrainingDate(LocalDateTime.of(2024, 7, 15, 12, 45));
        training.setTrainingDuration(Duration.ofMillis(3600000));
        return training;
    }

    private static TrainingStatistics createTestTrainingStatistics() {
        var statistics = new TrainingStatistics();
        statistics.setId("6a2ec294f8264c3b4fb4041f1");
        statistics.setTrainerUsername("Sarah.Davis");
        statistics.setTrainerFirstName("Sarah");
        statistics.setTrainerLastName("Davis");
        statistics.setTrainerStatus(true);

        var yearStatistics = new TrainingStatistics.YearStatistics();
        yearStatistics.setYear(Year.of(2024));

        var monthStatistics = new TrainingStatistics.MonthStatistics();
        monthStatistics.setMonth(Month.JULY);
        monthStatistics.setTotalDuration(Duration.ofMillis(32400000));
        var monthStatisticsSet = new HashSet<TrainingStatistics.MonthStatistics>();
        monthStatisticsSet.add(monthStatistics);
        yearStatistics.setMonthStatistics(monthStatisticsSet);
        var yearStatisticSet = new HashSet<TrainingStatistics.YearStatistics>();
        yearStatisticSet.add(yearStatistics);
        statistics.setYearStatisticsSet(yearStatisticSet);
        return statistics;
    }

}