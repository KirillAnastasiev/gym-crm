package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.dto.TrainingDto;
import com.epam.laboratory.app.dto.mapper.TrainingMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TestController {

    private final TrainingMapper mapper;

    @Logging(Level.INFO)
    @GetMapping
    public TrainingDto getTestTraining() {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Fitness");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("John.Doe");
        trainee.setPassword("password123");
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setId(5L);
        trainer.setFirstName("Sarah");
        trainer.setLastName("Davis");
        trainer.setUsername("Sarah.Davis");
        trainer.setPassword("password654");
        trainer.setSpecialization(trainingType);
        trainer.setActive(true);

        Training training = new Training(1L, trainee, trainer, "Morning Fitness", trainingType, LocalDate.of(2024, 7, 1).atStartOfDay(), Duration.ofMinutes(60));
        training.setId(1L);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingName("Morning Fitness");
        training.setTrainingType(trainingType);
        training.setTrainingDate(LocalDate.of(2024, 7, 1).atStartOfDay());
        training.setTrainingDuration(Duration.ofMinutes(60));

        return mapper.toDto(training);
    }

}
