package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.TrainerStatus;
import com.epam.laboratory.app.domain.Training;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
@DisplayName("TrainingDao test suite")
class TrainingDaoTest {

    @Autowired
    TrainingDao trainingDao;


    // ==================== FIND BY TRAINER USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method findByTrainerUsername - should return list of trainings with given trainer username")
    void testFindByTrainerUsername_positive() {
        // given
        var username = "Sarah.Davis";

        // when
        var actualResult = trainingDao.findByTrainerUsername(username);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        actualResult.forEach(training -> assertThat(training.getTrainerUsername()).isEqualTo(username));
    }

    @Test
    @DisplayName("Test of the method findByTrainerUsername - should return empty list if there is no trainings with given trainer username")
    void testFindByTrainerUsername_negative_unknownUsername() {
        // given
        var username = "Unknown.Username";

        // when
        var actualResult = trainingDao.findByTrainerUsername(username);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();
    }


    // ==================== FIND BY TRAINER USERNAME AND TRAINING DATE BETWEEN TESTS ====================

    @Test
    @DisplayName("Test of the method findByTrainerUsernameAndTrainingDateBetween - should return list of trainings with given trainer username and training date between given dates")
    void testFindByTrainerUsernameAndTrainingDateBetween_positive_twoResults() {
        // given
        var username = "Sarah.Davis";
        var trainingDateFrom = LocalDateTime.of(2024, 7, 1, 8, 0);
        var trainingDateTo = LocalDateTime.of(2024, 7, 5, 12, 30);

        // when
        var actualResult = trainingDao.findByTrainerUsernameAndTrainingDateBetween(username, trainingDateFrom, trainingDateTo);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        actualResult.forEach(training -> {
            assertThat(training.getTrainerUsername()).isEqualTo(username);
            assertThat(training.getTrainingDate()).isAfterOrEqualTo(trainingDateFrom);
            assertThat(training.getTrainingDate()).isBeforeOrEqualTo(trainingDateTo);
        });
    }

    @Test
    @DisplayName("Test of the method findByTrainerUsernameAndTrainingDateBetween - should return list with a single result if there is only one training with given trainer username and training date between given dates")
    void testFindByTrainerUsernameAndTrainingDateBetween_positive_oneResult() {
        // given
        var username = "Sarah.Davis";
        var trainingDateFrom = LocalDateTime.of(2024, 6, 1, 0, 0);
        var trainingDateTo = LocalDateTime.of(2024, 7, 5, 0, 0);

        // when
        var actualResult = trainingDao.findByTrainerUsernameAndTrainingDateBetween(username, trainingDateFrom, trainingDateTo);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).hasSize(1);
        var training = actualResult.getFirst();
        assertThat(training.getTrainerUsername()).isEqualTo(username);
        assertThat(training.getTrainingDate()).isAfter(trainingDateFrom);
        assertThat(training.getTrainingDate()).isBefore(trainingDateTo);
    }

    @Test
    @DisplayName("Test of the method findByTrainerUsernameAndTrainingDateBetween - should return empty list if there is no trainings with given trainer username")
    void testFindByTrainerUsernameAndTrainingDateBetween_negative_unknownUsername() {
        // given
        var username = "Unknown.Username";
        var trainingDateFrom = LocalDateTime.of(2024, 6, 1, 0, 0);
        var trainingDateTo = LocalDateTime.of(2024, 7, 5, 0, 0);

        // when
        var actualResult = trainingDao.findByTrainerUsernameAndTrainingDateBetween(username, trainingDateFrom, trainingDateTo);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();
    }

    @Test
    @DisplayName("Test of the method findByTrainerUsernameAndTrainingDateBetween - should return empty list if there is no trainings with given training date between given dates")
    void testFindByTrainerUsernameAndTrainingDateBetween_negative_notSatisfiedDates() {
        // given
        var username = "Sarah.Davis";
        var trainingDateFrom = LocalDateTime.of(2024, 6, 1, 0, 0);
        var trainingDateTo = LocalDateTime.of(2024, 6, 30, 23, 59);

        // when
        var actualResult = trainingDao.findByTrainerUsernameAndTrainingDateBetween(username, trainingDateFrom, trainingDateTo);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();
    }


    // ==================== SAVE TESTS ====================

    @Test
    @DisplayName("Test of the method save - should save training and return saved entity with generated id")
    void testSave() {
        // given
        var training = createTestTraining();

        // when
        var actualResult = trainingDao.save(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).usingRecursiveComparison().ignoringFields("id").isEqualTo(training);
    }


    // ==================== DELETE TESTS ====================

    @Test
    @DisplayName("Test of the method deleteById - should delete already existed training")
    void testDelete() {
        // given
        var training = createTestTraining();
        trainingDao.save(training);
        var id = training.getId();

        // when
        trainingDao.delete(training);

        // then
        assertThat(trainingDao.findById(id)).isEmpty();
    }


    private static Training createTestTraining() {
        var training = new Training();
        training.setTrainerUsername("FirstName.LastName");
        training.setTrainerFirstName("FirstName");
        training.setTrainerLastName("LastName");
        training.setTrainerStatus(TrainerStatus.ACTIVE);
        training.setTrainingDate(LocalDateTime.of(2024, 7, 1, 8, 0));
        training.setTrainingDuration(java.time.Duration.ofHours(1));
        return training;
    }

}