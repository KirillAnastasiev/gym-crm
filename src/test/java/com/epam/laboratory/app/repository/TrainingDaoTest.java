package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.service.TrainingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
@DisplayName("TrainingDao test suite")
class TrainingDaoTest {

    @Autowired
    private TrainingDao trainingDao;


    // ==================== SAVE TESTS ====================

    @Test
    @DisplayName("Test of the method save - should save training and return saved training with generated id")
    void testSave() {
        // given
        var trainee = createTestTrainee();
        var trainer = createTestTrainer();
        var trainingType = new TrainingType();
        trainingType.setId(2L);
        trainingType.setTrainingTypeName("Yoga");
        var training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingName("Soft Yoga");
        training.setTrainingDate(LocalDateTime.of(2025, 12, 15, 12, 45, 0));
        training.setTrainingDuration(Duration.ofMinutes(30));

        // when
        var actualResult = trainingDao.save(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult.getTrainee()).usingRecursiveComparison().isEqualTo(trainee);
        assertThat(actualResult.getTrainer()).usingRecursiveComparison().isEqualTo(trainer);
        assertThat(actualResult.getTrainingType()).usingRecursiveComparison().isEqualTo(trainingType);
        assertThat(actualResult.getTrainingName()).isEqualTo(training.getTrainingName());
        assertThat(actualResult.getTrainingDate()).isEqualTo(training.getTrainingDate());
        assertThat(actualResult.getTrainingDuration()).isEqualTo(training.getTrainingDuration());
    }


    // ==================== FIND BY CONDITION TESTS ====================

    @Test
    @DisplayName("Test of the method findByCondition - should return list of trainings with given duration")
    void testFindByCondition() {
        // given
        var trainee = createTestTrainee();
        var trainer = createTestTrainer();
        var trainingType = createTestTrainingType();
        var training = createTestTraining();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        // when
        var actualResult = trainingDao.findByCondition(TrainingService.toDate(LocalDate.of(2024, 7, 2).atStartOfDay()));

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).contains(training);
        actualResult.forEach(tr -> assertThat(tr.getTrainingDate().isBefore(
                LocalDate.of(2024, 7, 2).atStartOfDay())).isTrue());
    }


    // ==================== COUNT TEST ====================

    @Test
    @DisplayName("Test of the method count - should return total number of trainings")
    void testCount() {
        // when
        var actualResult = trainingDao.count();

        // then
        assertThat(actualResult).isEqualTo(8L);
    }


    private static Trainee createTestTrainee() {
        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setPassword("password123");
        trainee.setUsername("John.Doe");
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setActive(true);
        return trainee;
    }

    private static Trainer createTestTrainer() {
        var trainer = new Trainer();
        trainer.setId(5L);
        trainer.setFirstName("Sarah");
        trainer.setLastName("Davis");
        trainer.setPassword("password654");
        trainer.setUsername("Sarah.Davis");
        trainer.setActive(true);
        return trainer;
    }

    private static TrainingType createTestTrainingType() {
        var trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Fitness");
        return trainingType;
    }

    private static Training createTestTraining() {
        var training = new Training();
        training.setId(1L);
        training.setTrainingName("Morning Fitness");
        training.setTrainingDate(LocalDateTime.of(2024, 7, 1, 8, 0, 0));
        training.setTrainingDuration(Duration.ofHours(1));
        return training;
    }

}