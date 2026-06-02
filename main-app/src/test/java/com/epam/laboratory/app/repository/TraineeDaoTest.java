package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.service.TraineeService;
import com.epam.laboratory.app.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
@DisplayName("TraineeDao test suite")
class TraineeDaoTest {

    @Autowired
    private TraineeDao traineeDao;


    // ==================== SAVE TESTS ====================

    @Test
    @DisplayName("Test of the method save - should save trainee and return saved trainee with generated id")
    void testSave() {
        // given
        var trainee = new Trainee();
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");
        trainee.setPassword("testPassword");
        trainee.setUsername("FirstName.LastName");
        trainee.setDateOfBirth(LocalDate.of(2024, 12, 15));
        trainee.setAddress("Test Address");
        trainee.setActive(true);

        // when
        var actualResult = traineeDao.save(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).usingRecursiveComparison().isEqualTo(trainee);
    }


    // ==================== FIND BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method findByUsername - should return trainee with given username")
    void testFindByUsername() {
        // given
        var trainee = createTestTrainee();
        var trainer = createTestTrainer();
        var trainingType = createTestTrainingType();
        var training = createTestTraining();
        var training2 = createTestTraining2();
        training.setTrainingType(trainingType);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training2.setTrainingType(trainingType);
        training2.setTrainer(trainer);
        training2.setTrainee(trainee);
        trainer.setSpecialization(trainingType);
        trainee.addTrainer(trainer);
        trainee.addTrainings(List.of(training, training2));

        // when
        var actualResult = traineeDao.findByUsername("John.Doe");

        // then
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainee);
        assertThat(actualResult.map(Trainee::getTrainers).get()).isNotEmpty();
        assertThat(actualResult.map(Trainee::getTrainers).get()).contains(trainer);
        assertThat(actualResult.map(Trainee::getTrainings).get()).isNotEmpty();
        assertThat(actualResult.map(Trainee::getTrainings).get()).contains(training, training2);
    }


    // ==================== FIND BY CONDITION TESTS ====================

    @Test
    @DisplayName("Test of the method findByCondition - should return trainees with given trainer username")
    void testFindByCondition() {
        // given
        var trainee = createTestTrainee();
        var trainer = createTestTrainer();
        var trainingType = createTestTrainingType();
        var training = createTestTraining();
        var training2 = createTestTraining2();
        training.setTrainingType(trainingType);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training2.setTrainingType(trainingType);
        training2.setTrainer(trainer);
        training2.setTrainee(trainee);
        trainer.setSpecialization(trainingType);
        trainee.addTrainer(trainer);
        trainee.addTrainings(List.of(training, training2));

        // when
        var actualResult = traineeDao.findByCondition(TraineeService.byTrainerUsernames("Sarah.Davis"));

        // then
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).contains(trainee);
        assertThat(actualResult.stream()
                .flatMap(t -> t.getTrainers().stream())
                .filter(tr -> tr.getUsername().equals("Sarah.Davis"))
                .toList())
                .isNotEmpty();
    }


    // ==================== COUNT TESTS ====================

    @Test
    @DisplayName("Test of the method count - should return count of all trainees")
    void testCount() {
        // when
        var actualResult = traineeDao.count();

        // then
        assertThat(actualResult).isEqualTo(4);
    }


    // ==================== COUNT BY CONDITION TESTS ====================

    @Test
    @DisplayName("Test of the method countByCondition - should return count of trainees with given active status")
    void testCountByCondition() {
        // given
        boolean isActive = true;

        // when
        var actualResult = traineeDao.countByCondition(UserService.byStatus(isActive));

        // then
        assertThat(actualResult).isGreaterThan(0);
    }

    // ==================== UPDATE BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method updateByUsername - should update trainee with given username and return updated trainee")
    void testUpdateByUsername() {
        // given
        var trainee = createTestTrainee();
        var updatedTrainee = new Trainee();
        updatedTrainee.setFirstName("Joan");
        updatedTrainee.setLastName("Williams");
        updatedTrainee.setDateOfBirth(LocalDate.of(1993, 5, 13));
        updatedTrainee.setAddress("New Address");
        updatedTrainee.setActive(false);

        // when
        var actualResult = traineeDao.updateByUsername("John.Doe", updatedTrainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(trainee.getId());
        assertThat(actualResult.getUsername()).isNotNull();
        assertThat(actualResult.getUsername()).isEqualTo(trainee.getUsername());
        assertThat(actualResult).usingRecursiveComparison()
                .ignoringFields("id", "username", "password", "trainers", "trainings", "security")
                .isEqualTo(updatedTrainee);
    }


    // ==================== DELETE BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method deleteByUsername - should delete trainee with given username and return empty optional when findByUsername is called")
    void testDeleteByUsername() {
        // given
        var username = "John.Doe";

        // when
        traineeDao.deleteByUsername(username);
        var actualResult = traineeDao.findByUsername(username);

        // then
        assertThat(actualResult).isNotPresent();
    }


    // ==================== CHANGE STATUS BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method changeStatusByUsername - should change active status of trainee with given username and return trainee with updated status when findByUsername is called")
    void testChangeStatusByUsername() {
        // given
        var username = "John.Doe";

        // when
        traineeDao.changeStatusByUsername(username, false);
        var actualResult = traineeDao.findByUsername(username);

        // then
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getActive()).isFalse();
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

    private static Training createTestTraining2() {
        var training = new Training();
        training.setId(5L);
        training.setTrainingName("Weekend Crossfit");
        training.setTrainingDate(LocalDateTime.of(2024, 7, 5, 12, 30, 0));
        training.setTrainingDuration(Duration.ofMinutes(90));
        return training;
    }

}