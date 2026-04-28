package com.epam.laboratory.app;

import com.epam.laboratory.app.config.AppConfig;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingType;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class App {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(App.class);

        logger.info("Test logging");

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Test Training Type");

        Trainee trainee = new Trainee();
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");
        trainee.setPassword("password");
        trainee.setUsername("FirstName.LastName");
        trainee.setAddress("Test Address");
        trainee.setDateOfBirth(LocalDate.now());
        trainee.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setFirstName("FirstName");
        trainer.setLastName("LastName");
        trainer.setPassword("password");
        trainer.setUsername("FirstName.LastName1");
        trainer.setActive(false);
        trainer.setSpecialization(trainingType);

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Test Training");
        training.setTrainingType(trainingType);
        training.setTrainingDate(LocalDate.now().atStartOfDay());
        training.setTrainingDuration(java.time.Duration.ofHours(1));

        trainer.addTrainee(trainee);

        var emf = context.getBean(EntityManagerFactory.class);

        try (var entityManager = emf.createEntityManager()) {
            try {
                entityManager.getTransaction().begin();

                entityManager.persist(trainingType);
                entityManager.persist(trainer);
                entityManager.persist(trainee);
                entityManager.persist(training);

                entityManager.getTransaction().commit();
            } catch (Exception ex) {
                logger.error("Error during transaction, rolling back", ex);
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
            }
        }

        try (var entityManager = emf.createEntityManager()) {
            var foundTrainee = entityManager.find(Trainee.class, trainee.getId());
            logger.info("Found trainee: {}", foundTrainee);

            var trainings = foundTrainee.getTrainings();
            logger.info("Trainee's trainings: {}", trainings);

            var trainers = foundTrainee.getTrainers();
            logger.info("Trainee's trainers: {}", trainers);
        }

        try (var entityManager = emf.createEntityManager()) {
            try {
                entityManager.getTransaction().begin();

                var foundTrainee = entityManager.find(Trainee.class, trainee.getId());
                logger.info("Trainee: {}", foundTrainee);

                entityManager.remove(foundTrainee);
                entityManager.flush();
                entityManager.getTransaction().commit();
            } catch (Exception ex) {
                logger.error("Error during transaction, rolling back", ex);
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
            }
        }

        try (var entityManager = emf.createEntityManager()) {
            var foundTrainee = entityManager.find(Trainee.class, trainee.getId());
            logger.info("Found trainee after deletion: {}", foundTrainee);
        }

        context.close();
    }
}
