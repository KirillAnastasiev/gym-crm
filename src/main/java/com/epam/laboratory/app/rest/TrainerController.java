package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.dto.ChangeStatusRequestDto;
import com.epam.laboratory.app.dto.CredentialsDto;
import com.epam.laboratory.app.dto.TrainerDto;
import com.epam.laboratory.app.dto.mapper.TrainerCredentialsMapper;
import com.epam.laboratory.app.dto.mapper.TrainerMapper;
import com.epam.laboratory.app.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainerMapper trainerMapper;
    private final TrainerCredentialsMapper credentialsMapper;

    @GetMapping("/{username}")
    TrainerDto getProfile(@PathVariable String username) {
        var trainer = trainerService.selectByUsername(username);
        return trainerMapper.toDto(trainer);
    }

    @PostMapping
    CredentialsDto registerTrainer(@RequestBody TrainerDto trainerDto) {
        var trainer = trainerService.registerNew(trainerMapper.toEntity(trainerDto));
        return credentialsMapper.toDto(trainer);
    }

    @PutMapping
    TrainerDto updateTrainer(@RequestBody TrainerDto trainerDto) {
        var trainer = trainerService.update(trainerMapper.toEntity(trainerDto));
        return trainerMapper.toDto(trainer);
    }

    @DeleteMapping("/{username}")
    ResponseEntity<String> deleteTrainer(@PathVariable String username) {
        trainerService.deleteByUsername(username);
        return ResponseEntity.ok("Trainer with username " + username + " was deleted");
    }

    @PatchMapping
    ResponseEntity<String> changeTrainerStatus(@RequestBody ChangeStatusRequestDto requestDto) {
        String username = requestDto.username();
        boolean isActive = requestDto.active();
        trainerService.changeStatus(username, isActive);
        return ResponseEntity.ok("Trainer with username " + username + " was " + (isActive ? "unblocked" : "blocked"));
    }

}
