package com.kirill.projects.gymcrm.app.rest;

import com.kirill.projects.gymcrm.app.aspect.annotation.RestCallLogging;
import com.kirill.projects.gymcrm.app.aspect.annotation.ValidateArguments;
import com.kirill.projects.gymcrm.app.dto.*;
import com.kirill.projects.gymcrm.app.dto.mapper.CredentialsMapper;
import com.kirill.projects.gymcrm.app.dto.mapper.TraineeWithoutTrainersMapper;
import com.kirill.projects.gymcrm.app.dto.mapper.TrainerMapper;
import com.kirill.projects.gymcrm.app.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Trainers Management", description = "Endpoints for managing trainers profiles")
public class TrainerController implements Controller {

    private final TrainerService trainerService;
    private final TrainerMapper trainerMapper;
    private final CredentialsMapper credentialsMapper;
    private final TraineeWithoutTrainersMapper traineeMapper;


    // ==================== GET MAPPINGS ====================

    @GetMapping(
            path = "/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ValidateArguments
    @RestCallLogging(Level.TRACE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            description = "Get trainer profile by username",
            parameters = @Parameter(
                    name = "username",
                    description = "Username of the trainer to retrieve",
                    required = true,
                    example = "Sarah.Davis"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Trainer profile retrieved successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TrainerDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Trainer with the specified username not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    ResponseEntity<TrainerDto> getProfile(@PathVariable String username,
                                          @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        return performRequest(requestId, () -> {
            var trainer = trainerService.selectByUsername(username);
            return trainerMapper.toDto(trainer);
        }, HttpStatus.OK);
    }


    // ==================== POST MAPPINGS ====================

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ValidateArguments
    @RestCallLogging(Level.TRACE)
    @Operation(
            description = "Register a new trainer",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Trainer registration data",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TrainerDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                  "firstName": "FirstName",
                                                  "lastName": "LastName",
                                                  "specialization": {
                                                      "id": 1,
                                                      "trainingTypeName": "Fitness"
                                                  }
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Trainer registered successfully, credentials returned",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CredentialsDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid trainer registration data",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    ResponseEntity<CredentialsDto> registerTrainer(@RequestBody TrainerDto trainerDto,
                                                   @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        return performRequest(requestId, () -> {
            var userCredentials = trainerService.registerNew(trainerMapper.toEntity(trainerDto));
            return credentialsMapper.toDto(userCredentials);
        }, HttpStatus.CREATED);
    }


    // ==================== PUT MAPPINGS ====================

    @PutMapping(
            path = "/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ValidateArguments
    @RestCallLogging(Level.TRACE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            description = "Update trainer profile by username",
            parameters = @Parameter(
                    name = "username",
                    description = "Username of the trainer to update",
                    required = true,
                    example = "Sarah.Davis"
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated trainer profile data",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TrainerDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                 "firstName": "Laura",
                                                 "lastName": "Palmer",
                                                 "specialization": {
                                                     "id": 2,
                                                     "trainingTypeName": "Yoga"
                                                 },
                                                 "active": true
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Trainer profile updated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TrainerDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid trainer profile data",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Trainer with the specified username not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    ResponseEntity<TrainerDto> updateTrainer(@PathVariable String username,
                                             @RequestBody TrainerDto trainerDto,
                                             @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        return performRequest(requestId, () -> {
            var trainer = trainerService.updateByUsername(username, trainerMapper.toEntity(trainerDto));
            return trainerMapper.toDto(trainer);
        }, HttpStatus.OK);
    }

    @PutMapping(
            path = "/{username}/trainees",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ValidateArguments
    @RestCallLogging(Level.TRACE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            description = "Update the list of trainees assigned to a trainer",
            parameters = @Parameter(
                    name = "username",
                    description = "Username of the trainer whose trainees are to be updated",
                    required = true,
                    example = "Sarah.Davis"
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Collection of trainee usernames to assign to the trainer",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TraineeDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                                {"username": "Jane.Smith"},
                                                {"username": "Emily.Johnson"}
                                            ]
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Trainer's trainees updated successfully, updated list of trainees returned",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TraineeDto.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    [ 
                                                        {
                                                            "id" : 2,
                                                            "firstName" : "Jane",
                                                            "lastName" : "Smith",
                                                            "username" : "Jane.Smith",
                                                            "password" : "password456",
                                                            "dateOfBirth" : "1985-05-15",
                                                            "address" : "456 Elm St",
                                                            "active" : true
                                                        }, {
                                                            "id" : 3,
                                                            "firstName" : "Emily",
                                                            "lastName" : "Johnson",
                                                            "username" : "Emily.Johnson",
                                                            "password" : "password789",
                                                            "dateOfBirth" : "1992-09-30",
                                                            "address" : "789 Oak St",
                                                            "active" : true
                                                        } 
                                                    ]
                                                    """
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
                            description = "Trainer with the specified username not found or one or more trainees not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    ResponseEntity<List<TraineeDto>> updateTrainerTrainees(@PathVariable String username,
                                                           @RequestBody Collection<UserDto> traineeDtoCollection,
                                                           @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        return performRequest(requestId, () -> {
            var trainees = traineeDtoCollection.stream()
                    .map(traineeMapper::toEntity)
                    .toList();
            return trainerService.updateTrainees(username, trainees)
                    .stream()
                    .map(traineeMapper::toDto)
                    .toList();
        }, HttpStatus.OK);
    }

    // ==================== PATCH MAPPINGS ====================


    @PatchMapping(
            path = "/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ValidateArguments
    @RestCallLogging(Level.TRACE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            description = "Change trainer's active status by username",
            parameters = @Parameter(
                    name = "username",
                    description = "Username of the trainer whose status is to be changed",
                    required = true,
                    example = "Sarah.Davis"
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New active status for the trainer",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChangeStatusRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Trainer status changed successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MessageResponseDto.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "message": "Trainer with username Sarah.Davis was blocked"
                                            }
                                            """
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
                            description = "Trainer with the specified username not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    ResponseEntity<MessageResponseDto> changeTrainerStatus(@PathVariable String username,
                                                           @RequestBody ChangeStatusRequestDto requestDto,
                                                           @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        return performRequest(requestId, () -> {
            boolean isActive = requestDto.active();
            trainerService.changeStatus(username, isActive);
            return new MessageResponseDto("Trainer with username %s was %s".formatted(username, isActive ? "unblocked" : "blocked"));
        }, HttpStatus.OK);
    }


    // ==================== DELETE MAPPINGS ====================

    @DeleteMapping(
            path = "/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ValidateArguments
    @RestCallLogging(Level.TRACE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            description = "Delete trainer by username",
            parameters = @Parameter(
                    name = "username",
                    description = "Username of the trainer to delete",
                    required = true,
                    example = "Sarah.Davis"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Trainer deleted successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MessageResponseDto.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "message": "Trainer with username Sarah.Davis was deleted"
                                            }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Trainer with the specified username not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                            )
                    )
            }
    )
    ResponseEntity<MessageResponseDto> deleteTrainer(@PathVariable String username,
                                                     @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        return performRequest(requestId, () -> {
            trainerService.deleteByUsername(username);
            return new MessageResponseDto("Trainer with username %s was deleted".formatted(username));
        }, HttpStatus.OK);
    }

}
