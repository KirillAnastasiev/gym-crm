package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.aspect.annotation.RestCallLogging;
import com.epam.laboratory.app.aspect.annotation.ValidateArguments;
import com.epam.laboratory.app.dto.TrainingDto;
import com.epam.laboratory.app.dto.TrainingFilterDto;
import com.epam.laboratory.app.dto.mapper.TrainingFilterMapper;
import com.epam.laboratory.app.dto.mapper.TrainingMapper;
import com.epam.laboratory.app.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Trainings Management", description = "Endpoints for managing training sessions")
public class TrainingController {

    private final TrainingService trainingService;
    private final TrainingMapper trainingMapper;
    private final TrainingFilterMapper trainingFilterMapper;


    // ==================== GET MAPPINGS ====================

    @GetMapping(
            path = "/trainee/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    @Operation(
            description = "Get all trainings for a trainee. Optionally, you can provide a filter to narrow down the results.",
            parameters = @Parameter(
                    name = "username",
                    description = "The username of the trainee",
                    required = true
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Optional filter for trainings",
                    required = false,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TrainingFilterDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "dateFrom": "2024-07-01T00:00:00",
                                                "dateTo": "2024-07-31T23:59:59"
                                            }"""
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of trainings",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = TrainingDto.class,
                                                    example = """
                                                            {
                                                                 "id": 5,
                                                                 "traineeUsername": "John.Doe",
                                                                 "trainerUsername": "Sarah.Davis",
                                                                 "trainingName": "Weekend Crossfit",
                                                                 "trainingType": {
                                                                    "id": 5,
                                                                     "trainingTypeName": "Crossfit"
                                                                 },
                                                                 "trainingDate": "2024-07-05T12:30:00",
                                                                 "trainingDuration": "PT1H30M"
                                                            }"""
                                            )
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Trainee not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    public Collection<TrainingDto> getTraineeTrainings(@PathVariable String username,
                                                       @RequestBody(required = false) TrainingFilterDto trainingFilterDto) {
        var trainingFilter = trainingFilterMapper.toEntity(trainingFilterDto);
        var trainings = trainingService.selectForTrainee(username, trainingFilter);
        return trainings.stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    @GetMapping(
            path = "/trainer/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    @Operation(
            description = "Get all trainings for a trainer. Optionally, you can provide a filter to narrow down the results.",
            parameters = @Parameter(
                    name = "username",
                    description = "The username of the trainer",
                    required = true
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Optional filter for trainings",
                    required = false,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TrainingFilterDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "trainingTypeName":"Fitness"
                                            }"""
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of trainings",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = TrainingDto.class,
                                                    example = """
                                                            {
                                                                "id": 1,
                                                                "traineeUsername": "John.Doe",
                                                                "trainerUsername": "Sarah.Davis",
                                                                "trainingName": "Morning Fitness",
                                                                "trainingType": {
                                                                    "id": 1,
                                                                    "trainingTypeName": "Fitness"
                                                                },
                                                                "trainingDate": "2024-07-01T08:00:00",
                                                                "trainingDuration": "PT1H"
                                                            }"""
                                            )
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Trainer not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    public Collection<TrainingDto> getTrainerTrainings(@PathVariable String username,
                                                       @RequestBody(required = false) TrainingFilterDto trainingFilterDto) {
        var trainingFilter = trainingFilterMapper.toEntity(trainingFilterDto);
        var trainings = trainingService.selectForTrainer(username, trainingFilter);
        return trainings.stream()
                .map(trainingMapper::toDto)
                .toList();
    }


    // ==================== POST MAPPINGS ====================

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    @Operation(
            description = "Register a new training session. The request body should contain all necessary details about the training.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the training to be registered",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TrainingDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "traineeUsername": "John.Doe",
                                                "trainerUsername": "Sarah.Davis",
                                                "trainingName": "Evening Yoga",
                                                "trainingType": {
                                                    "id": 2,
                                                    "trainingTypeName": "Yoga"
                                                },
                                                "trainingDate": "2024-07-10T18:00:00",
                                                "trainingDuration": "PT1H"
                                            }"""
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Training successfully registered",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Trainee or trainer not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    public String registerTraining(@RequestBody TrainingDto trainingDto) {
        var training = trainingMapper.toEntity(trainingDto);
        trainingService.registerNew(training);
        return "Training was registered";
    }

}
