package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.config.TestSecurityConfig;
import com.epam.laboratory.app.domain.TrainingStatistics;
import com.epam.laboratory.app.dto.TrainingStatisticsDto;
import com.epam.laboratory.app.dto.mapper.TrainingStatisticsMapperImpl;
import com.epam.laboratory.app.exception.RestExceptionHandler;
import com.epam.laboratory.app.security.JwtAuthenticationConverter;
import com.epam.laboratory.app.service.TrainingStatisticsService;
import com.epam.laboratory.app.service.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingStatisticsController.class)
@Import({
        JsonMapper.class,
        TrainingStatisticsMapperImpl.class,
        RestExceptionHandler.class,
        TestSecurityConfig.class,
})
class TrainingStatisticsControllerTest {

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainingStatisticsService trainingStatisticsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationConverter converter;

    @MockitoBean
    private AuthenticationManager authenticationManager;


    // ==================== GET TRAININGS STATISTICS ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method getTrainingStatistics - should return training statistics for the given trainer username")
    void testGetTrainingStatistics_positive() throws Exception {
        // given
        var statistics = createTestTrainingStatistics();
        var username = "Sarah.Davis";
        var requestId = "53f9405d-eaa2-43cb-b940-5deaa263cb33";

        given(trainingStatisticsService.getTrainingStatisticsForTrainerInPeriod(anyString(), eq(null), eq(null))).willReturn(statistics);

        // when & then
        var actualResult = mockMvc.perform(get("/api/statistics/{trainerUsername}", username)
                        .header("X-Request-ID", requestId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        var response = jsonMapper.readValue(contentAsString, TrainingStatisticsDto.class);

        assertThat(response).isNotNull();
        assertThat(response).isInstanceOf(TrainingStatisticsDto.class);
        assertThat(response).extracting(TrainingStatisticsDto::trainerUsername).isEqualTo(username);
        assertThat(response).extracting(TrainingStatisticsDto::trainingSummary).isInstanceOf(Map.class);
        assertThat(response.trainingSummary()).containsKey(Year.of(2025));
        assertThat(response.trainingSummary()).containsValue(Map.of(Month.DECEMBER, Duration.ofMinutes(30)));

        verify(trainingStatisticsService, times(1)).getTrainingStatisticsForTrainerInPeriod(anyString(), eq(null), eq(null));
        verifyNoMoreInteractions(trainingStatisticsService);
    }

    @Test
    @DisplayName("Test of the method getTrainingStatistics - should return training statistics for the given trainer username and date parameters")
    void testGetTrainingStatistics_Positive_withDateParameters() throws Exception {
        // given
        var statistics = createTestTrainingStatistics();
        var username = "Sarah.Davis";
        var dateFrom = LocalDate.of(2025, Month.DECEMBER, 1);
        var dateTo = LocalDate.of(2025, Month.DECEMBER, 31);
        var requestId = "34562302-c451-4414-9623-02c4510414c5";

        given(trainingStatisticsService.getTrainingStatisticsForTrainerInPeriod(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(statistics);

        // when & then
        var actualResult = mockMvc.perform(get("/api/statistics/{username}", username)
                        .param("dateFrom", dateFrom.toString())
                        .param("dateTo", dateTo.toString())
                        .header("X-Request-ID", requestId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        var response = jsonMapper.readValue(contentAsString, TrainingStatisticsDto.class);

        assertThat(response).isNotNull();
        assertThat(response).isInstanceOf(TrainingStatisticsDto.class);
        assertThat(response).extracting(TrainingStatisticsDto::trainerUsername).isEqualTo(username);
        assertThat(response).extracting(TrainingStatisticsDto::trainingSummary).isInstanceOf(Map.class);
        assertThat(response.trainingSummary()).containsKey(Year.of(2025));
        assertThat(response.trainingSummary()).containsValue(Map.of(Month.DECEMBER, Duration.ofMinutes(30)));

        verify(trainingStatisticsService, times(1)).getTrainingStatisticsForTrainerInPeriod(anyString(), any(LocalDate.class), any(LocalDate.class));
        verifyNoMoreInteractions(trainingStatisticsService);
    }

    @Test
    @DisplayName("Test of the method getTrainingStatistics - should return Bad Request when invalid date parameters are provided")
    void testGetTrainingStatistics_negative_invalidDateParameters() throws Exception {
        // given
        var username = "Sarah.Davis";
        var invalidDateFrom = "invalid-date";
        var invalidDateTo = "invalid-date";
        var requestId = "34562302-c451-4414-9623-02c4510414c5";

        // when & then
        mockMvc.perform(get("/api/statistics/{username}", username)
                        .param("dateFrom", invalidDateFrom)
                        .param("dateTo", invalidDateTo)
                        .header("X-Request-ID", requestId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));

        verifyNoInteractions(trainingStatisticsService);
    }

    @Test
    @DisplayName("Test of the method getTrainingStatistics - should return Internal Server Error when service throws an exception")
    void testGetTrainingStatistics_negative_exceptionInService() throws Exception {
        // given
        var username = "Sarah.Davis";
        var dateFrom = LocalDate.of(2025, Month.DECEMBER, 1);
        var dateTo = LocalDate.of(2025, Month.DECEMBER, 31);
        var requestId = "34562302-c451-4414-9623-02c4510414c5";

        given(trainingStatisticsService.getTrainingStatisticsForTrainerInPeriod(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .willThrow(new RuntimeException("Service exception"));

        // when & then
        mockMvc.perform(get("/api/statistics/{username}", username)
                        .param("dateFrom", dateFrom.toString())
                        .param("dateTo", dateTo.toString())
                        .header("X-Request-ID", requestId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));

        verify(trainingStatisticsService, times(1)).getTrainingStatisticsForTrainerInPeriod(anyString(), any(LocalDate.class), any(LocalDate.class));
        verifyNoMoreInteractions(trainingStatisticsService);
    }

    private static TrainingStatistics createTestTrainingStatistics() {
        return new TrainingStatistics(
                "Sarah.Davis",
                Map.of(
                        Year.of(2025),
                        Map.of(Month.DECEMBER, Duration.ofMinutes(30))
                )
        );
    }

}