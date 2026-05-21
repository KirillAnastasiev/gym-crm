package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.service.TrainerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Sql(scripts = {
        "classpath:schema.test.sql",
        "classpath:data.test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DisplayName("TrainerDao test suite")
class TrainerDaoTest {

    @Autowired
    private TrainerDao trainerDao;


    // ==================== SAVE TESTS ====================

    @Test
    @DisplayName("Test of the method save - should save trainer and return saved trainer with generated id")
    void testSave() {
        // given
        var trainer = new Trainer();
        trainer.setFirstName("FirstName");
        trainer.setLastName("LastName");
        trainer.setPassword("testPassword");
        trainer.setUsername("FirstName.LastName");
        trainer.setSpecialization(createTestTrainingType());
        trainer.setActive(true);

        // when
        var actualResult = trainerDao.save(trainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).usingRecursiveComparison().isEqualTo(trainer);
    }


    // ==================== FIND BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method findByUsername - should return trainer with given username and all associated entities")
    void testFindByUsername() {
        // given
        var trainer = createTestTrainer();
        var trainee = createTestTrainee();
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
        trainer.addTrainee(trainee);
        trainer.addTrainings(List.of(training, training2));

        // when
        var actualResult = trainerDao.findByUsername("Sarah.Davis");

        // then
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainer);
        assertThat(actualResult.map(Trainer::getTrainees).get()).isNotNull();
        assertThat(actualResult.map(Trainer::getTrainees).get()).contains(trainee);
        assertThat(actualResult.map(Trainer::getTrainings).get()).isNotNull();
        assertThat(actualResult.map(Trainer::getTrainings).get()).contains(training, training2);
    }

    // ==================== FIND BY CONDITION TESTS ====================

    @Test
    @DisplayName("Test of the method findByCondition - should return trainers with given specialization")
    void testFindByCondition() {
        // given
        var trainer = createTestTrainer();
        var trainee = createTestTrainee();
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
        trainer.addTrainee(trainee);
        trainer.addTrainings(List.of(training, training2));

        // when
        var actualResult = trainerDao.findByCondition(TrainerService.bySpecializations("Fitness"));

        // then
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).contains(trainer);
        actualResult.forEach(t -> {
            assertThat(t.getSpecialization()).isNotNull();
            assertThat(t.getSpecialization()).isEqualTo(trainingType);
        });
    }

    // ==================== UPDATE BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method updateByUsername - should update trainer with given username and return updated trainer")
    void testUpdateByUsername() {
        // given
        var trainer = createTestTrainer();
        var newTrainingType = createTestTrainingType();
        newTrainingType.setId(2L);
        newTrainingType.setTrainingTypeName("Yoga");
        var updatedTrainer = new Trainer();
        updatedTrainer.setFirstName("Laura");
        updatedTrainer.setLastName("Palmer");
        updatedTrainer.setSpecialization(newTrainingType);
        updatedTrainer.setActive(true);

        // when
        var actualResult = trainerDao.updateByUsername("Sarah.Davis", updatedTrainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(trainer.getId());
        assertThat(actualResult.getUsername()).isNotNull();
        assertThat(actualResult.getUsername()).isEqualTo(trainer.getUsername());
        assertThat(actualResult).usingRecursiveComparison()
                .ignoringFields("id", "username", "password", "trainees", "trainings")
                .isEqualTo(updatedTrainer);
    }


    // ==================== DELETE BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method deleteByUsername - should delete trainer with given username and return empty optional when findByUsername is called")
    void testDeleteByUsername() {
        // given
        var username = "Sarah.Davis";

        // when
        trainerDao.deleteByUsername(username);
        var actualResult = trainerDao.findByUsername(username);

        // then
        assertThat(actualResult).isNotPresent();
    }


    // ==================== CHANGE STATUS BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method changeStatusByUsername - should change status of trainer with given username and return trainer with updated status when findByUsername is called")
    void testChangeStatusByUsername() {
        // given
        var username = "Sarah.Davis";

        // when
        trainerDao.changeStatusByUsername(username, false);
        var actualResult = trainerDao.findByUsername(username);

        // then
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getActive()).isFalse();
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