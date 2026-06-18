package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.domain.TrainingStatistics;
import com.epam.laboratory.app.dto.mapper.TrainingStatisticsResponseMapperImpl;
import com.epam.laboratory.app.exception.NoContentException;
import com.epam.laboratory.app.exception.RestExceptionHandler;
import com.epam.laboratory.app.service.TrainingStatisticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingStatisticsController.class)
@Import({
        TrainingStatisticsResponseMapperImpl.class,
        RestExceptionHandler.class,
})
@DisplayName("TrainingStatisticsController test suite")
class TrainingStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainingStatisticsService trainingStatisticsService;


    // ==================== GET TRAINING STATISTICS ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method getTrainingStatistics - should return 200 OK with correct statistics when valid request is sent without period parameters")
    void testGetTrainingStatistics_positive_withoutPeriod() throws Exception {
        // given
        var statistics = getTestTrainingStatistics();
        var requestId = "53f9405d-eaa2-43cb-b940-5deaa263cb33";

        given(trainingStatisticsService.getStatisticsByTrainerUsername(anyString())).willReturn(Optional.of(statistics));

        // when & then
        mockMvc.perform(get("/statistics/{username}", "Sarah.Davis")
                        .header("X-Request-ID", requestId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "trainerUsername": "Sarah.Davis",
                            "trainingSummary": {
                                "2025": {
                                    "DECEMBER": "PT30M"
                                }
                            }
                        }
                        """));

        verify(trainingStatisticsService, times(1)).getStatisticsByTrainerUsername("Sarah.Davis");
        verifyNoMoreInteractions(trainingStatisticsService);
    }

    @Test
    @DisplayName("Test of the method getTrainingStatistics - should return 200 OK with correct statistics when valid request is sent with period parameters")
    void testGetTrainingStatistics_positive_withPeriod() throws Exception {
        var statistics = getTestTrainingStatistics();
        var requestId = "53f9405d-eaa2-43cb-b940-5deaa263cb33";

        given(trainingStatisticsService.getStatisticsByTrainerUsernameInPeriod(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(Optional.of(statistics));

        // when & then
        mockMvc.perform(get("/statistics/{username}", "Sarah.Davis")
                        .param("fromDate", "2025-12-01")
                        .param("toDate", "2025-12-31")
                        .header("X-Request-ID", requestId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "trainerUsername": "Sarah.Davis",
                            "trainingSummary": {
                                "2025": {
                                    "DECEMBER": "PT30M"
                                }
                            }
                        }
                        """));

        verify(trainingStatisticsService, times(1)).getStatisticsByTrainerUsernameInPeriod(anyString(), any(LocalDate.class), any(LocalDate.class));
        verifyNoMoreInteractions(trainingStatisticsService);
    }

    @Test
    @DisplayName("Test of the method getTrainingStatistics - should return 204 No Content when no statistics found for trainer")
    void testGetTrainingStatistics_negative_exceptionInService() throws Exception {
        // given
        var requestId = "53f9405d-eaa2-43cb-b940-5deaa263cb33";

        doThrow(new NoContentException("No training statistics found for trainer %s".formatted("Sarah.Davis")))
                .when(trainingStatisticsService).getStatisticsByTrainerUsername(anyString());

        // when & then
        mockMvc.perform(get("/statistics/{username}", "Sarah.Davis")
                        .header("X-Request-ID", requestId))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

        verify(trainingStatisticsService, times(1)).getStatisticsByTrainerUsername(anyString());
        verifyNoMoreInteractions(trainingStatisticsService);
    }


    private static TrainingStatistics getTestTrainingStatistics() {
        var statistics = new TrainingStatistics();
        statistics.setTrainerUsername("Sarah.Davis");
        var monthStatistics = new TrainingStatistics.MonthStatistics();
        monthStatistics.setMonth(Month.DECEMBER);
        monthStatistics.setTotalDuration(Duration.ofMinutes(30));
        var yearStatistics = new TrainingStatistics.YearStatistics();
        yearStatistics.setYear(Year.of(2025));
        var monthStatisticsSet = new HashSet<TrainingStatistics.MonthStatistics>();
        monthStatisticsSet.add(monthStatistics);
        yearStatistics.setMonthStatistics(monthStatisticsSet);
        var yearStatisticSet = new HashSet<TrainingStatistics.YearStatistics>();
        yearStatisticSet.add(yearStatistics);
        statistics.setYearStatisticsSet(yearStatisticSet);
        return statistics;
    }
}