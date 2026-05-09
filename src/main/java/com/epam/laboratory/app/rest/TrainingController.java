package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.dto.TrainingDto;
import com.epam.laboratory.app.dto.TrainingFilterDto;
import com.epam.laboratory.app.dto.mapper.TrainingFilterMapper;
import com.epam.laboratory.app.dto.mapper.TrainingMapper;
import com.epam.laboratory.app.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;
    private final TrainingMapper trainingMapper;
    private final TrainingFilterMapper trainingFilterMapper;

    @GetMapping(path = "/trainee/{username}", consumes = "application/json", produces = "application/json")
    public Collection<TrainingDto> getTraineeTrainings(@PathVariable String username,
                                                       @RequestBody(required = false) TrainingFilterDto trainingFilterDto) {
        var trainingFilter = trainingFilterMapper.toEntity(trainingFilterDto);
        var trainings = trainingService.selectForTrainee(username, trainingFilter);
        return trainings.stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    @GetMapping(path = "/trainer/{username}", consumes = "application/json", produces = "application/json")
    public Collection<TrainingDto> getTrainerTrainings(@PathVariable String username,
                                                       @RequestBody(required = false) TrainingFilterDto trainingFilterDto) {
        var trainingFilter = trainingFilterMapper.toEntity(trainingFilterDto);
        var trainings = trainingService.selectForTrainer(username, trainingFilter);
        return trainings.stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> registerTraining(@RequestBody TrainingDto trainingDto) {
        var training = trainingMapper.toEntity(trainingDto);
        trainingService.registerNew(training);
        return ResponseEntity.ok("Training was registered");
    }

}
