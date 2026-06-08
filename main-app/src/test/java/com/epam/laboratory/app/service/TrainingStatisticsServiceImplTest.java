package com.epam.laboratory.app.service;

import com.epam.laboratory.app.client.TrainingStatisticsClient;
import com.epam.laboratory.app.domain.TrainingStatistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
class TrainingStatisticsServiceImplTest {

    @Mock
    private TrainingStatisticsClient statisticsClient;

    @InjectMocks
    private TrainingStatisticsServiceImpl service;


    // ==================== GET TRAINING STATISTICS FOR TRAINER IN PERIOD TESTS ====================

    @Test
    @DisplayName("Test of the method getTrainingStatisticsForTrainerInPeriod - should return training statistics for the trainer in the specified period when the trainer exists and input dates are valid")
    void testGetTrainingStatisticsForTrainerInPeriod_positive() {
        // given
        var username = "Sarah.Davis";
        var dateFrom = LocalDate.of(2025, 5, 1);
        var dateTo = LocalDate.of(2026, 1, 2);
        var statistics = getTestTrainingStatistics();

        given(statisticsClient.getTrainingStatisticsForTrainerInPeriod(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(statistics);

        // when
        var actualResult = service.getTrainingStatisticsForTrainerInPeriod(username, dateFrom, dateTo);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getTrainerUsername()).isEqualTo(statistics.getTrainerUsername());
        assertThat(actualResult.getTrainingSummary()).isInstanceOf(Map.class);
        assertThat(actualResult.getTrainingSummary()).isNotEmpty();
        assertThat(actualResult).extracting(TrainingStatistics::getTrainingSummary).isEqualTo(statistics.getTrainingSummary());

        verify(statisticsClient, times(1)).getTrainingStatisticsForTrainerInPeriod(anyString(), any(LocalDate.class), any(LocalDate.class));
        verifyNoMoreInteractions(statisticsClient);
    }

    @Test
    @DisplayName("Test of the method getTrainingStatisticsForTrainerInPeriod - should return empty training statistics for the trainer in the specified period when the trainer exists but has no training records in that period")
    void testGetTrainingStatisticsForTrainerInPeriod_negative_unknownTrainerUsername() {
        // given
        var username = "Unknown.Username";
        var dateFrom = LocalDate.of(2025, 5, 1);
        var dateTo = LocalDate.of(2026, 1, 2);
        var statistics = new TrainingStatistics();
        statistics.setTrainerUsername(username);
        statistics.setTrainingSummary(Collections.emptyMap());

        given(statisticsClient.getTrainingStatisticsForTrainerInPeriod(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(statistics);

        // when
        var actualResult = service.getTrainingStatisticsForTrainerInPeriod(username, dateFrom, dateTo);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).extracting(TrainingStatistics::getTrainerUsername).isEqualTo(username);
        assertThat(actualResult.getTrainingSummary()).isInstanceOf(Map.class);
        assertThat(actualResult.getTrainingSummary()).isEmpty();

        verify(statisticsClient, times(1)).getTrainingStatisticsForTrainerInPeriod(anyString(), any(LocalDate.class), any(LocalDate.class));
        verifyNoMoreInteractions(statisticsClient);
    }

    @Test
    @DisplayName("Test of the method getTrainingStatisticsForTrainerInPeriod - should return empty training statistics for the trainer in the specified period when the trainer exists but has no training records in that period due to non-satisfied dates")
    void testGetTrainingStatisticsForTrainerInPeriod_negative_nonSatisfiedDates() {
        // given
        var username = "Sarah.Davis";
        var dateFrom = LocalDate.of(2000, 5, 1);
        var dateTo = LocalDate.of(2006, 1, 2);
        var statistics = getTestTrainingStatistics();
        statistics.setTrainerUsername(username);
        statistics.setTrainingSummary(Collections.emptyMap());

        given(statisticsClient.getTrainingStatisticsForTrainerInPeriod(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(statistics);

        // when
        var actualResult = service.getTrainingStatisticsForTrainerInPeriod(username, dateFrom, dateTo);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).extracting(TrainingStatistics::getTrainerUsername).isEqualTo(username);
        assertThat(actualResult.getTrainingSummary()).isInstanceOf(Map.class);
        assertThat(actualResult.getTrainingSummary()).isEmpty();

        verify(statisticsClient, times(1)).getTrainingStatisticsForTrainerInPeriod(anyString(), any(LocalDate.class), any(LocalDate.class));
        verifyNoMoreInteractions(statisticsClient);
    }

    @Test
    @DisplayName("Test of the method getTrainingStatisticsForTrainerInPeriod - should throw IllegalArgumentException when start date is after end date")
    void testGetTrainingStatisticsForTrainerInPeriod_negative_startDateAfterEndDate() {
        // given
        var username = "Sarah.Davis";
        var dateFrom = LocalDate.of(2025, 5, 1);
        var dateTo = LocalDate.of(2025, 1, 2);

        // when
        assertThatThrownBy(() -> service.getTrainingStatisticsForTrainerInPeriod(username, dateFrom, dateTo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("From date must be before to date");

        verifyNoInteractions(statisticsClient);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "'', 'Trainer username must not be blank'",
        "'   ', 'Trainer username must not be blank'",
        "NULL, 'Trainer username must not be null'",
    }, nullValues = "NULL")
    @DisplayName("Test of the method getTrainingStatisticsForTrainerInPeriod - should throw IllegalArgumentException when invalid input data is provided")
    void testGetTrainingStatisticsForTrainerInPeriod_negative_invalidInputData(String trainerUsername, String errorMessage) {
        // given
        var dateFrom = LocalDate.of(2025, 1, 1);
        var dateTo = LocalDate.of(2025, 5, 2);

        // when & then
        assertThatThrownBy(() -> service.getTrainingStatisticsForTrainerInPeriod(trainerUsername, dateFrom, dateTo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(errorMessage);

        verifyNoInteractions(statisticsClient);
    }

    private static TrainingStatistics getTestTrainingStatistics() {
        var statistics = new TrainingStatistics();
        statistics.setTrainerUsername("Sarah.Davis");
        var trainingsSummary = Map.of(
                Year.of(2025),
                Map.of(Month.DECEMBER, Duration.ofMinutes(30))
        );
        statistics.setTrainingSummary(trainingsSummary);
        return statistics;
    }

}