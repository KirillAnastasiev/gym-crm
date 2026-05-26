package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.aspect.annotation.RestCallLogging;
import com.epam.laboratory.app.aspect.annotation.ValidateArguments;
import com.epam.laboratory.app.dto.*;
import com.epam.laboratory.app.dto.mapper.TraineeCredentialsMapper;
import com.epam.laboratory.app.dto.mapper.TraineeMapper;
import com.epam.laboratory.app.dto.mapper.TrainerWithoutTraineesMapper;
import com.epam.laboratory.app.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/trainees")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Trainees Management", description = "Endpoints for managing trainees profiles")
public class TraineeController {

    private final TraineeService traineeService;
    private final TraineeMapper traineeMapper;
    private final TraineeCredentialsMapper credentialsMapper;
    private final TrainerWithoutTraineesMapper trainerMapper;


    // ==================== GET MAPPINGS ====================

    @GetMapping(
            path = "/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    @Operation(
            description = "Get trainee profile by username",
            parameters = @Parameter(
                    name = "username",
                    description = "Username of the trainee to retrieve",
                    required = true,
                    example = "John.Doe"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Trainee profile retrieved successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TraineeDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Trainee with the specified username not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    TraineeDto getProfile(@PathVariable String username) {
        var trainee = traineeService.selectByUsername(username);
        return traineeMapper.toDto(trainee);
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
            description = "Register a new trainee",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Trainee registration data",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TraineeDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Trainee registered successfully, credentials returned",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CredentialsDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid trainee registration data",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    CredentialsDto registerTrainee(@RequestBody TraineeDto traineeDto) {
        var trainee = traineeService.registerNew(traineeMapper.toEntity(traineeDto));
        return credentialsMapper.toDto(trainee);
    }


    // ==================== PUT MAPPINGS ====================

    @PutMapping(
            path = "/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    @Operation(
            description = "Update trainee profile by username",
            parameters = @Parameter(
                    name = "username",
                    description = "Username of the trainee to update",
                    required = true,
                    example = "John.Doe"
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated trainee profile data",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TraineeDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Trainee profile updated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TraineeDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid trainee profile data",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Trainee with the specified username not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    TraineeDto updateTrainee(@PathVariable String username,
                             @RequestBody TraineeDto traineeDto) {
        var trainee = traineeService.updateByUsername(username, traineeMapper.toEntity(traineeDto));
        return traineeMapper.toDto(trainee);
    }

    @PutMapping(
            path = "/{username}/trainers",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    @Operation(
            description = "Update the list of trainers assigned to a trainee",
            parameters = @Parameter(
                    name = "username",
                    description = "Username of the trainee whose trainers are to be updated",
                    required = true,
                    example = "John.Doe"
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Collection of trainer usernames to assign to the trainee",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class, type = "array"),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                                {"username": "Laura.Miller"},
                                                {"username": "James.Taylor"}
                                            ]"""
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Trainee's trainers updated successfully, updated list of trainers returned",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TrainerDto.class, type = "array"),
                                    examples = @ExampleObject(
                                            value = """
                                                    [ 
                                                        {
                                                            "id" : 7,
                                                            "firstName" : "Laura",
                                                            "lastName" : "Miller",
                                                            "username" : "Laura.Miller",
                                                            "password" : "password111",
                                                            "specialization" : {
                                                                "id" : 3,
                                                                "trainingTypeName" : "Zumba"
                                                            },
                                                            "active" : true
                                                        }, {
                                                            "id" : 8,
                                                            "firstName" : "James",
                                                            "lastName" : "Taylor",
                                                            "username" : "James.Taylor",
                                                            "password" : "password222",
                                                            "specialization" : {
                                                                "id" : 4,
                                                                "trainingTypeName" : "Stretching"
                                                            },
                                                            "active" : true
                                                        } 
                                                    ]"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request format",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Trainee with the specified username not found or one or more trainers not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    Collection<TrainerDto> updateTraineeTrainers(@PathVariable String username,
                                                 @RequestBody Collection<UserDto> trainerUsernames) {
        var trainers = trainerUsernames.stream()
                .map(trainerMapper::toEntity)
                .toList();

        return traineeService.updateTrainers(username, trainers)
                .stream()
                .map(trainerMapper::toDto)
                .toList();
    }


    // ==================== PATCH MAPPINGS ====================

    @PatchMapping(
            path = "/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    @Operation(
            description = "Change trainee's active status by username",
            parameters = @Parameter(
                    name = "username",
                    description = "Username of the trainee whose status is to be changed",
                    required = true,
                    example = "John.Doe"
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New active status for the trainee",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChangeStatusRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Trainee's status changed successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MessageResponseDto.class),
                                    examples = @ExampleObject(value = """
                                            {   
                                              "message": "Trainee with username John.Doe was blocked"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request format",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Trainee with the specified username not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    MessageResponseDto changeTraineeStatus(@PathVariable String username,
                               @RequestBody ChangeStatusRequestDto requestDto) {
        boolean isActive = requestDto.active();
        traineeService.changeStatus(username, isActive);
        return new MessageResponseDto("Trainee with username %s was %s".formatted(username, isActive ? "unblocked" : "blocked"));
    }


    // ==================== DELETE MAPPINGS ====================

    @DeleteMapping(
            path = "/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    @Operation(
            description = "Delete trainee by username",
            parameters = @Parameter(
                    name = "username",
                    description = "Username of the trainee to delete",
                    required = true,
                    example = "John.Doe"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Trainee deleted successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MessageResponseDto.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "message": "Trainee with username John.Doe was deleted"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid username format",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Trainee with the specified username not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    MessageResponseDto deleteTrainee(@PathVariable String username) {
        traineeService.deleteByUsername(username);
        return new MessageResponseDto("Trainee with username %s was deleted".formatted(username));
    }

}
