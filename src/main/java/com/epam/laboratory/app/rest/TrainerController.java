package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.aspect.annotation.RestCallLogging;
import com.epam.laboratory.app.aspect.annotation.ValidateArguments;
import com.epam.laboratory.app.dto.*;
import com.epam.laboratory.app.dto.mapper.TraineeWithoutTrainersMapper;
import com.epam.laboratory.app.dto.mapper.TrainerCredentialsMapper;
import com.epam.laboratory.app.dto.mapper.TrainerMapper;
import com.epam.laboratory.app.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainerMapper trainerMapper;
    private final TrainerCredentialsMapper credentialsMapper;
    private final TraineeWithoutTrainersMapper traineeMapper;

    @GetMapping(
            path = "/{username}",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging
    TrainerDto getProfile(@PathVariable String username) {
        var trainer = trainerService.selectByUsername(username);
        return trainerMapper.toDto(trainer);
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging
    CredentialsDto registerTrainer(@RequestBody TrainerDto trainerDto) {
        var trainer = trainerService.registerNew(trainerMapper.toEntity(trainerDto));
        return credentialsMapper.toDto(trainer);
    }

    @PutMapping(
            path = "/{username}",
            consumes = "application/json",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging
    TrainerDto updateTrainer(@PathVariable String username,
                             @RequestBody TrainerDto trainerDto) {
        var trainer = trainerService.updateByUsername(username, trainerMapper.toEntity(trainerDto));
        return trainerMapper.toDto(trainer);
    }

    @DeleteMapping(
            path = "/{username}",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging
    ResponseEntity<String> deleteTrainer(@PathVariable String username) {
        trainerService.deleteByUsername(username);
        return ResponseEntity.ok("Trainer with username " + username + " was deleted");
    }

    @PatchMapping(
            path = "/{username}",
            consumes = "application/json",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging
    ResponseEntity<String> changeTrainerStatus(@PathVariable String username,
                                               @RequestBody ChangeStatusRequestDto requestDto) {
        boolean isActive = requestDto.active();
        trainerService.changeStatus(username, isActive);
        return ResponseEntity.ok("Trainer with username " + username + " was " + (isActive ? "unblocked" : "blocked"));
    }

    @PutMapping(
            path = "/{username}/trainees",
            consumes = "application/json",
            produces = "application/json"
    )
    @ValidateArguments
    @RestCallLogging
    Collection<TraineeDto> updateTrainerTrainees(@PathVariable String username,
                                                 @RequestBody Collection<UserDto> traineeDtoCollection) {
        var trainees = traineeDtoCollection.stream()
                .map(traineeMapper::toEntity)
                .toList();

        return trainerService.updateTrainees(username, trainees)
                .stream()
                .map(traineeMapper::toDto)
                .toList();
    }

}
