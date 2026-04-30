package com.epam.laboratory.app;

import com.epam.laboratory.app.config.AppConfig;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.service.TraineeService;
import com.epam.laboratory.app.service.TrainerService;
import com.epam.laboratory.app.service.TrainingService;
import com.epam.laboratory.app.service.TrainingTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.Collection;

import static com.epam.laboratory.app.service.Service.*;
import static com.epam.laboratory.app.service.TraineeService.*;

public class App {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(App.class);

        logger.info("Test logging");

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        Trainee trainee = new Trainee();
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");
        trainee.setAddress("Test Address");
        trainee.setDateOfBirth(LocalDate.now());
        trainee.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setFirstName("FirstName");
        trainer.setLastName("LastName");
        trainer.setActive(false);

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Test Training");
        training.setTrainingDate(LocalDate.now().atStartOfDay());
        training.setTrainingDuration(java.time.Duration.ofHours(1));

        var traineeService = context.getBean(TraineeService.class);
        var trainerService = context.getBean(TrainerService.class);
        var trainingTypeService = context.getBean(TrainingTypeService.class);
        var trainingService = context.getBean(TrainingService.class);

        Collection<Trainee> traineesWithDateOfBirthFrom = traineeService.selectByCondition(not(dateOfBirthFrom(LocalDate.of(1990, 1, 1))), Trainee.class);
        logger.info("Trainees with date of birth from {}: {}", LocalDate.now().minusYears(20), traineesWithDateOfBirthFrom);

        Collection<Trainee> traineesWithTrainers = traineeService.selectByCondition(TraineeService.byTrainerUsernames("Sarah.Davis", "Laura.Miller"), Trainee.class);
        logger.info("Trainees with trainers: {}", traineesWithTrainers);

        Collection<Trainee> traineesByUsername = traineeService.selectByCondition(not(byUsernames("John.Doe", "Jane.Smith")), Trainee.class);
        logger.info("Trainees by username: {}", traineesByUsername);

        context.close();
    }
}
