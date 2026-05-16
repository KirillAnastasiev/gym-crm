package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.aspect.annotation.RestCallLogging;
import com.epam.laboratory.app.aspect.annotation.ValidateArguments;
import com.epam.laboratory.app.dto.*;
import com.epam.laboratory.app.dto.mapper.TraineeCredentialsMapper;
import com.epam.laboratory.app.dto.mapper.TraineeMapper;
import com.epam.laboratory.app.dto.mapper.TrainerWithoutTraineesMapper;
import com.epam.laboratory.app.service.TraineeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/trainees")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class TraineeController {

    private final TraineeService traineeService;
    private final TraineeMapper traineeMapper;
    private final TraineeCredentialsMapper credentialsMapper;
    private final TrainerWithoutTraineesMapper trainerMapper;

    @GetMapping(
            path = "/{username}",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    TraineeDto getProfile(@PathVariable String username) {
        var trainee = traineeService.selectByUsername(username);
        return traineeMapper.toDto(trainee);
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    CredentialsDto registerTrainee(@RequestBody TraineeDto traineeDto) {
        var trainee = traineeService.registerNew(traineeMapper.toEntity(traineeDto));
        return credentialsMapper.toDto(trainee);
    }

    @PutMapping(
            path = "/{username}",
            consumes = "application/json",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    TraineeDto updateTrainee(@PathVariable String username,
                             @RequestBody TraineeDto traineeDto) {
        var trainee = traineeService.updateByUsername(username, traineeMapper.toEntity(traineeDto));
        return traineeMapper.toDto(trainee);
    }

    @DeleteMapping(
            path = "/{username}",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    ResponseEntity<String> deleteTrainee(@PathVariable String username) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.ok("Trainee with username " + username + " was deleted");
    }

    @PatchMapping(
            path = "/{username}",
            consumes = "application/json",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging(Level.INFO)
    ResponseEntity<String> changeTraineeStatus(@PathVariable String username,
                                               @RequestBody ChangeStatusRequestDto requestDto) {
        boolean isActive = requestDto.active();
        traineeService.changeStatus(username, isActive);
        return ResponseEntity.ok("Trainee with username " + username + " was " + (isActive ? "unblocked" : "blocked"));
    }

    @PutMapping(
            path = "/{username}/trainers",
            consumes = "application/json",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging(Level.INFO)
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

}
