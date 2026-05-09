package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.dto.ChangeStatusRequestDto;
import com.epam.laboratory.app.dto.CredentialsDto;
import com.epam.laboratory.app.dto.TraineeDto;
import com.epam.laboratory.app.dto.TrainerDto;
import com.epam.laboratory.app.dto.mapper.TraineeWithoutTrainersMapper;
import com.epam.laboratory.app.dto.mapper.TrainerCredentialsMapper;
import com.epam.laboratory.app.dto.mapper.TrainerMapper;
import com.epam.laboratory.app.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainerMapper trainerMapper;
    private final TrainerCredentialsMapper credentialsMapper;
    private final TraineeWithoutTrainersMapper traineeMapper;

    @GetMapping(path = "/{username}", produces = "application/json")
    TrainerDto getProfile(@PathVariable String username) {
        var trainer = trainerService.selectByUsername(username);
        return trainerMapper.toDto(trainer);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    CredentialsDto registerTrainer(@RequestBody TrainerDto trainerDto) {
        var trainer = trainerService.registerNew(trainerMapper.toEntity(trainerDto));
        return credentialsMapper.toDto(trainer);
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    TrainerDto updateTrainer(@RequestBody TrainerDto trainerDto) {
        var trainer = trainerService.update(trainerMapper.toEntity(trainerDto));
        return trainerMapper.toDto(trainer);
    }

    @DeleteMapping(path = "/{username}", produces = "application/json")
    ResponseEntity<String> deleteTrainer(@PathVariable String username) {
        trainerService.deleteByUsername(username);
        return ResponseEntity.ok("Trainer with username " + username + " was deleted");
    }

    @PatchMapping(consumes = "application/json", produces = "application/json")
    ResponseEntity<String> changeTrainerStatus(@RequestBody ChangeStatusRequestDto requestDto) {
        String username = requestDto.username();
        boolean isActive = requestDto.active();
        trainerService.changeStatus(username, isActive);
        return ResponseEntity.ok("Trainer with username " + username + " was " + (isActive ? "unblocked" : "blocked"));
    }

    @PutMapping(path = "/{username}/trainees", consumes = "application/json", produces = "application/json")
    Collection<TraineeDto> updateTrainerTrainees(@PathVariable String username,
                                                 @RequestBody Collection<TraineeDto> traineeDtoCollection) {
        var trainees = traineeDtoCollection.stream()
                .map(traineeMapper::toEntity)
                .toList();

        return trainerService.updateTrainees(username, trainees)
                .stream()
                .map(traineeMapper::toDto)
                .toList();
    }

}
