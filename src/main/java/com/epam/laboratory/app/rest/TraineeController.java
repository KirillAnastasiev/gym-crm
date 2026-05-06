package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.dto.ChangePasswordRequestDto;
import com.epam.laboratory.app.dto.ChangeStatusRequestDto;
import com.epam.laboratory.app.dto.CredentialsDto;
import com.epam.laboratory.app.dto.TraineeDto;
import com.epam.laboratory.app.dto.mapper.TraineeCredentialsMapper;
import com.epam.laboratory.app.dto.mapper.TraineeMapper;
import com.epam.laboratory.app.service.TraineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
class TraineeController {

    private final TraineeService traineeService;
    private final TraineeMapper traineeMapper;
    private final TraineeCredentialsMapper credentialsMapper;

    @GetMapping("/{username}")
    TraineeDto getProfile(@PathVariable String username) {
        var trainee = traineeService.selectByUsername(username);
        return traineeMapper.toDto(trainee);
    }

    @PostMapping
    CredentialsDto registerTrainee(@RequestBody TraineeDto traineeDto) {
        var trainee = traineeService.registerNew(traineeMapper.toEntity(traineeDto));
        return credentialsMapper.toDto(trainee);
    }

    @PutMapping
    TraineeDto updateTrainee(@RequestBody TraineeDto traineeDto) {
        var trainee = traineeService.update(traineeMapper.toEntity(traineeDto));
        return traineeMapper.toDto(trainee);
    }

    @DeleteMapping("/{username}")
    ResponseEntity<String> deleteTrainee(@PathVariable String username) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.ok("Trainee with username " + username + " was deleted");
    }

    @PatchMapping
    ResponseEntity<String> changeTraineeStatus(@RequestBody ChangeStatusRequestDto requestDto) {
        String username = requestDto.username();
        boolean isActive = requestDto.active();
        traineeService.changeStatus(username, isActive);
        return ResponseEntity.ok("Trainee with username " + username + " was " + (isActive ? "unblocked" : "blocked"));
    }

}
