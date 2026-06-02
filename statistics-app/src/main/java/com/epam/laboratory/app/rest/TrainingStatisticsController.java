package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.domain.TrainingStatistics;
import com.epam.laboratory.app.dto.TrainingStatisticsResponseDto;
import com.epam.laboratory.app.dto.mapper.TrainingStatisticsResponseMapper;
import com.epam.laboratory.app.service.TrainingStatisticsService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class TrainingStatisticsController {

    private final TrainingStatisticsResponseMapper statisticsResponseMapper;
    private final TrainingStatisticsService trainingStatisticsService;

    @GetMapping("/{trainerUsername}")
    public ResponseEntity<TrainingStatisticsResponseDto> getTrainingStatistics(@PathVariable String trainerUsername,
                                                                               @RequestParam(required = false) LocalDate fromDate,
                                                                               @RequestParam(required = false) LocalDate toDate) {
        TrainingStatistics statistics;
        if (fromDate != null && toDate != null) {
            log.debug("Request for training statistics for trainer {} in period from {} to {}", trainerUsername, fromDate, toDate);
            statistics = handleForUsernameInPeriod(trainerUsername, fromDate, toDate);
        } else {
            log.debug("Request for training statistics for trainer {}", trainerUsername);
            statistics = handleForUsername(trainerUsername);
        }
        log.debug("Training statistics for trainer {}: {}", trainerUsername, statistics);
        return ResponseEntity.ok().body(statisticsResponseMapper.toDto(statistics));
    }

    private TrainingStatistics handleForUsernameInPeriod(@NotBlank String trainerUsername,
                                                         @NotNull LocalDate fromDate,
                                                         @NotNull LocalDate toDate) {
        return trainingStatisticsService.getStatisticsForTrainerInPeriod(trainerUsername, fromDate, toDate);
    }

    private TrainingStatistics handleForUsername(@NotBlank String trainerUsername) {
        return trainingStatisticsService.getStatisticsForTrainer(trainerUsername);
    }

}
