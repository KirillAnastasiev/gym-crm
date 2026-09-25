package com.kirill.projects.gymcrm.app.rest;

import com.kirill.projects.gymcrm.app.aspect.annotation.RestCallLogging;
import com.kirill.projects.gymcrm.app.domain.TrainingStatistics;
import com.kirill.projects.gymcrm.app.dto.TrainingStatisticsResponseDto;
import com.kirill.projects.gymcrm.app.dto.mapper.TrainingStatisticsResponseMapper;
import com.kirill.projects.gymcrm.app.exception.NoContentException;
import com.kirill.projects.gymcrm.app.service.TrainingStatisticsService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TrainingStatisticsController implements Controller {

    private final TrainingStatisticsResponseMapper statisticsResponseMapper;
    private final TrainingStatisticsService trainingStatisticsService;

    @RestCallLogging(Level.TRACE)
    @GetMapping("/{trainerUsername}")
    public ResponseEntity<TrainingStatisticsResponseDto> getTrainingStatistics(@PathVariable String trainerUsername,
                                                                               @RequestParam(required = false) LocalDate fromDate,
                                                                               @RequestParam(required = false) LocalDate toDate,
                                                                               @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        return performRequest(requestId, () -> {
            TrainingStatistics statistics;
            if (fromDate != null && toDate != null) {
                statistics = handleForUsernameInPeriod(trainerUsername, fromDate, toDate);
            } else {
                statistics = handleForUsername(trainerUsername);
            }
            return statisticsResponseMapper.toDto(statistics);
        }, HttpStatus.OK);
    }

    private TrainingStatistics handleForUsernameInPeriod(@NotBlank String trainerUsername,
                                                         @NotNull LocalDate fromDate,
                                                         @NotNull LocalDate toDate) {
        return trainingStatisticsService.getStatisticsByTrainerUsernameInPeriod(trainerUsername, fromDate, toDate)
                .orElseThrow(() ->
                        new NoContentException("No statistics found for trainer: %s in the period from %s to %s".formatted(trainerUsername, fromDate, toDate)));
    }

    private TrainingStatistics handleForUsername(@NotBlank String trainerUsername) {
        return trainingStatisticsService.getStatisticsByTrainerUsername(trainerUsername)
                .orElseThrow(() ->
                        new NoContentException("No statistics found for trainer: %s".formatted(trainerUsername)));
    }

}
