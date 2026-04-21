package com.epam.laboratory.app;

import com.epam.laboratory.app.config.AppConfig;
import com.epam.laboratory.app.config.CustomStorageBeanPostProcessor;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.repository.Storage;
import com.epam.laboratory.app.service.TraineeService;
import com.epam.laboratory.app.service.TrainerService;
import com.epam.laboratory.app.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class App {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(App.class);

        logger.info("Test logging");

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        BeanPostProcessor beanPostProcessor = context.getBean(CustomStorageBeanPostProcessor.class);

        Storage storage = context.getBean(Storage.class);

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
        trainer.setSpecialization(TrainingType.FITNESS);

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Test Training");
        training.setTrainingType(TrainingType.FITNESS);
        training.setTrainingDate(LocalDate.now().atStartOfDay());
        training.setTrainingDuration(java.time.Duration.ofHours(1));

        TrainerService trainerService = context.getBean(TrainerService.class);
        TraineeService traineeService = context.getBean(TraineeService.class);
        TrainingService trainingService = context.getBean(TrainingService.class);

        trainerService.createTrainer(trainer);
        traineeService.createTrainee(trainee);
        trainingService.createTraining(training);

        logger.trace(storage.getStorageMap().toString());

        context.close();
    }
}
