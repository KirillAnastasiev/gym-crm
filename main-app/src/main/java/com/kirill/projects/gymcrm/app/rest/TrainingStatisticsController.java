package com.kirill.projects.gymcrm.app.rest;

import com.kirill.projects.gymcrm.app.aspect.annotation.RestCallLogging;
import com.kirill.projects.gymcrm.app.aspect.annotation.ValidateArguments;
import com.kirill.projects.gymcrm.app.dto.TrainingStatisticsDto;
import com.kirill.projects.gymcrm.app.dto.mapper.TrainingStatisticsMapper;
import com.kirill.projects.gymcrm.app.service.TrainingStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Training Statistics Management", description = "Endpoint for handling requests related to training statistics")
public class TrainingStatisticsController implements Controller {

    private final TrainingStatisticsService trainingStatisticsService;
    private final TrainingStatisticsMapper trainingStatisticsMapper;


    // ==================== GET MAPPINGS ====================

    @GetMapping(
            path = "/{trainerUsername}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ValidateArguments
    @RestCallLogging(Level.TRACE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            description = "Get training statistics for a specific trainer within an optional date range.",
            parameters = {
                    @Parameter(name = "trainerUsername", description = "The username of the trainer", required = true),
                    @Parameter(name = "dateFrom", description = "The start date of the period (optional)", required = false),
                    @Parameter(name = "dateTo", description = "The end date of the period (optional)", required = false)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved training statistics",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TrainingStatisticsDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    public ResponseEntity<TrainingStatisticsDto> getTrainingStatistics(@PathVariable String trainerUsername,
                                                                       @RequestParam(required = false) LocalDate dateFrom,
                                                                       @RequestParam(required = false) LocalDate dateTo,
                                                                       @RequestHeader(REQUEST_ID_HEADER)  String requestId) {
        return performRequest(requestId, () -> {
            var statistics = trainingStatisticsService.getTrainingStatisticsForTrainerInPeriod(trainerUsername, dateFrom, dateTo);
            return trainingStatisticsMapper.toDto(statistics);
        }, HttpStatus.OK);
    }

}

