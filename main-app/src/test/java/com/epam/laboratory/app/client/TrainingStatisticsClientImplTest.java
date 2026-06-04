package com.epam.laboratory.app.client;

import com.epam.laboratory.app.domain.TrainingStatistics;
import com.epam.laboratory.app.dto.TrainingStatisticsDto;
import com.epam.laboratory.app.dto.mapper.TrainingStatisticsMapper;
import com.epam.laboratory.app.dto.mapper.TrainingStatisticsMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(TrainingStatisticsMapperImpl.class)
@DisplayName("TrainingStatisticsClientImpl test suite")
class TrainingStatisticsClientImplTest {
    private static final String STATISTICS_SERVICE_URL = "http://localhost:8081";


    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private TrainingStatisticsMapper statsMapper;

    private TrainingStatisticsClientImpl trainingStatisticsClient;

    @BeforeEach
    void setUp() {
        trainingStatisticsClient = new TrainingStatisticsClientImpl(restTemplate, statsMapper);
        ReflectionTestUtils.setField(trainingStatisticsClient, "statisticsServiceUrl", STATISTICS_SERVICE_URL);
    }


    // ==================== GET TRAINING STATISTICS FOR TRAINER IN PERIOD ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method getTrainingStatisticsForTrainerInPeriod - should return training statistics for the given trainer username and period")
    void testGetTrainingStatisticsForTrainerInPeriod_positive() {
        // given
        var username = "Sarah.Davis";
        var dateFrom = LocalDate.of(2025, Month.DECEMBER, 1);
        var dateTo = LocalDate.of(2025, Month.DECEMBER, 31);
        var serviceResponse = createTestTrainingStatisticsDto();

        given(restTemplate.getInterceptors()).willReturn(new ArrayList<>());
        given(restTemplate.getForObject(any(URI.class), eq(TrainingStatisticsDto.class))).willReturn(serviceResponse);

        // when
        var actualResult = trainingStatisticsClient.getTrainingStatisticsForTrainerInPeriod(username, dateFrom, dateTo);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).extracting(TrainingStatistics::getTrainerUsername).isEqualTo(username);
        assertThat(actualResult).extracting(TrainingStatistics::getTrainingSummary).isInstanceOf(Map.class);
        assertThat(actualResult.getTrainingSummary()).containsKey(Year.of(2025));
        assertThat(actualResult.getTrainingSummary()).containsValue(Map.of(Month.DECEMBER, Duration.ofMinutes(30)));

        verify(restTemplate, times(1)).getInterceptors();
        verify(restTemplate, times(1)).getForObject(any(URI.class), eq(TrainingStatisticsDto.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    @DisplayName("Test of the method getTrainingStatisticsForTrainerInPeriod - should throw RuntimeException when RestTemplate throws an exception")
    void testGetTrainingStatisticsForTrainerInPeriod_negative_restTemplateException() {
        // given
        var username = "Sarah.Davis";
        var dateFrom = LocalDate.of(2025, Month.DECEMBER, 1);
        var dateTo = LocalDate.of(2025, Month.DECEMBER, 31);

        given(restTemplate.getInterceptors()).willReturn(new ArrayList<>());
        given(restTemplate.getInterceptors()).willThrow(new RuntimeException("Service is unavailable"));

        // when & then
        assertThatThrownBy(() -> trainingStatisticsClient.getTrainingStatisticsForTrainerInPeriod(username, dateFrom, dateTo))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service is unavailable");

        verify(restTemplate, times(1)).getInterceptors();
        verifyNoMoreInteractions(restTemplate);
    }

    private static TrainingStatisticsDto createTestTrainingStatisticsDto() {
        return new TrainingStatisticsDto(
                "Sarah.Davis",
                Map.of(
                        Year.of(2025),
                        Map.of(Month.DECEMBER, Duration.ofMinutes(30))
                )
        );
    }

}