package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.TrainerStatus;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.exception.NoContentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TrainingStatisticsServiceImplTest {

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainingStatisticsServiceImpl trainingStatisticsService;


    // ==================== GET STATISTICS FOR TRAINER TESTS ====================

    @Test
    @DisplayName("Test of the method getStatisticsForTrainer - should return correct training statistics for trainer")
    void testGetStatisticsForTrainer_positive() {
        // given
        var trainings = getTestTrainings();
        var trainerUsername = "FirstName.LastName";

        given(trainingService.getTrainingsByTrainerUsername(anyString())).willReturn(trainings);

        // when
        var actualResult = trainingStatisticsService.getStatisticsForTrainer(trainerUsername);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getTrainerUsername()).isEqualTo(trainerUsername);
        assertThat(actualResult.getTrainingSummary()).isNotNull();
        assertThat(actualResult.getTrainingSummary()).containsKey(Year.of(2024));
        assertThat(actualResult.getTrainingSummary().get(Year.of(2024))).isNotNull();
        assertThat(actualResult.getTrainingSummary().get(Year.of(2024))).containsEntry(Month.JULY, Duration.ofMinutes(135));

        verify(trainingService).getTrainingsByTrainerUsername(anyString());
        verifyNoMoreInteractions(trainingService);
    }

    @Test
    @DisplayName("Test of the method getStatisticsForTrainer - should return empty training statistics for trainer with unknown username")
    void testGetStatisticsForTrainer_negative_unknownUsername() {
        // given
        var trainerUsername = "Unknown.Username";

        given(trainingService.getTrainingsByTrainerUsername(anyString())).willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> trainingStatisticsService.getStatisticsForTrainer(trainerUsername))
                .isInstanceOf(NoContentException.class)
                .hasMessageContaining("No trainings found for trainer Unknown.Username");

        verify(trainingService).getTrainingsByTrainerUsername(anyString());
        verifyNoMoreInteractions(trainingService);
    }

    @Test
    @DisplayName("Test of the method getStatisticsForTrainer - should throw exception when downstream service throws exception")
    void testGetStatisticsForTrainer_negative_exceptionThrown() {
        // given
        var trainerUsername = "FirstName.LastName";

        given(trainingService.getTrainingsByTrainerUsername(anyString())).willThrow(new RuntimeException("Service error"));

        // when & then
        assertThatThrownBy(() -> trainingStatisticsService.getStatisticsForTrainer(trainerUsername))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service error");

        verify(trainingService).getTrainingsByTrainerUsername(anyString());
        verifyNoMoreInteractions(trainingService);
    }


    // ==================== GET STATISTICS FOR TRAINER IN PERIOD TESTS ====================

    @Test
    @DisplayName("Test of the method getStatisticsForTrainerInPeriod - should return correct training statistics for trainer in specified period")
    void testGetStatisticsForTrainerInPeriod_positive() {
        // given
        var trainings = getTestTrainings();
        var trainerUsername = "FirstName.LastName";
        var startDate = LocalDate.of(2024, 7, 1);
        var endDate = LocalDate.of(2024, 7, 3);

        given(trainingService.getTrainingsByTrainerUsernameBetweenDates(anyString(), any(LocalDate.class), any(LocalDate.class))).willReturn(trainings);

        // when
        var actualResult = trainingStatisticsService.getStatisticsForTrainerInPeriod(trainerUsername, startDate, endDate);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getTrainerUsername()).isEqualTo(trainerUsername);
        assertThat(actualResult.getTrainingSummary()).isNotNull();
        assertThat(actualResult.getTrainingSummary()).containsKey(Year.of(2024));
        assertThat(actualResult.getTrainingSummary().get(Year.of(2024))).isNotNull();
        assertThat(actualResult.getTrainingSummary().get(Year.of(2024))).containsEntry(Month.JULY, Duration.ofMinutes(135));

        verify(trainingService).getTrainingsByTrainerUsernameBetweenDates(anyString(), any(LocalDate.class), any(LocalDate.class));
        verifyNoMoreInteractions(trainingService);
    }

    @Test
    @DisplayName("Test of the method getStatisticsForTrainerInPeriod - should return empty training statistics for trainer with unknown username in specified period")
    void testGetStatisticsForTrainerInPeriod_negative_unknownUsername() {
        // given
        var username = "Unknown.Username";
        var startDate = LocalDate.of(2024, 7, 1);
        var endDate = LocalDate.of(2024, 7, 3);

        given(trainingService.getTrainingsByTrainerUsernameBetweenDates(anyString(), any(LocalDate.class), any(LocalDate.class))).willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> trainingStatisticsService.getStatisticsForTrainerInPeriod(username, startDate, endDate))
                .isInstanceOf(NoContentException.class)
                .hasMessageContaining("No trainings found for trainer Unknown.Username");

        verify(trainingService).getTrainingsByTrainerUsernameBetweenDates(anyString(), any(LocalDate.class), any(LocalDate.class));
        verifyNoMoreInteractions(trainingService);
    }

    @Test
    @DisplayName("Test of the method getStatisticsForTrainerInPeriod - should return empty training statistics for trainer when there are no trainings in specified period")
    void testGetStatisticsForTrainerInPeriod_negative_unsatisfiedDates() {
        // given
        var trainerUsername = "FirstName.LastName";
        var startDate = LocalDate.of(2026, 7, 1);
        var endDate = LocalDate.of(2026, 7, 3);

        given(trainingService.getTrainingsByTrainerUsernameBetweenDates(anyString(), any(LocalDate.class), any(LocalDate.class))).willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> trainingStatisticsService.getStatisticsForTrainerInPeriod(trainerUsername, startDate, endDate))
                .isInstanceOf(NoContentException.class)
                .hasMessageContaining("No trainings found for trainer FirstName.LastName");

        verify(trainingService).getTrainingsByTrainerUsernameBetweenDates(anyString(), any(LocalDate.class), any(LocalDate.class));
        verifyNoMoreInteractions(trainingService);
    }

    @Test
    @DisplayName("Test of the method getStatisticsForTrainerInPeriod - should throw exception when downstream service throws exception")
    void testGetStatisticsForTrainerInPeriod_negative_exceptionThrown() {
        // given
        var trainerUsername = "FirstName.LastName";
        var startDate = LocalDate.of(2026, 7, 1);
        var endDate = LocalDate.of(2026, 7, 3);

        given(trainingService.getTrainingsByTrainerUsernameBetweenDates(anyString(), any(LocalDate.class), any(LocalDate.class))).willThrow(new RuntimeException("Service error"));

        // when & then
        assertThatThrownBy(() -> trainingStatisticsService.getStatisticsForTrainerInPeriod(trainerUsername, startDate, endDate))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service error");

        verify(trainingService).getTrainingsByTrainerUsernameBetweenDates(anyString(), any(LocalDate.class), any(LocalDate.class));
        verifyNoMoreInteractions(trainingService);
    }

    private static Collection<Training> getTestTrainings() {
        var trainings = new ArrayList<Training>();
        trainings.add(new Training(1L,
                "FirstName.LastName",
                "FirstName",
                "LastName",
                TrainerStatus.ACTIVE,
                LocalDateTime.of(2024, 7, 1, 10, 0),
                Duration.ofMinutes(60)));

        trainings.add(new Training(2L,
                "FirstName.LastName",
                "FirstName",
                "LastName",
                TrainerStatus.ACTIVE,
                LocalDateTime.of(2024, 7, 2, 15, 30),
                Duration.ofMinutes(45)));

        trainings.add(new Training(3L,
                "FirstName.LastName",
                "FirstName",
                "LastName",
                TrainerStatus.ACTIVE,
                LocalDateTime.of(2024, 7, 2, 9, 0),
                Duration.ofMinutes(30)));

        return trainings;
    }
}