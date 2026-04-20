package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingType;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class StorageTest {
    @Mock
    private JsonMapper jsonMapper;

    @InjectMocks
    private Storage storage;

    @BeforeEach
    void setUp() {
        storage.getStorageMap().clear();
    }

    @Test
    @DisplayName("Test of the method update - should update entity in storage map")
    void testUpdate() {
        // given
        var storageMap = storage.getStorageMap();
        var trainee = createTestTrainee();
        trainee.setFirstName("UpdatedFirstName");

        // when
        storage.update(trainee);

        // then
        assertThat(storageMap).isNotEmpty();
        assertThat(storageMap).containsKey("trainee:1");
        assertThat(storageMap).containsValue(trainee);
        assertThat(storageMap.get("trainee:1")).isSameAs(trainee);
    }

    @Test
    @DisplayName("Test of the method remove - should remove entity from storage map by key")
    void testRemove() {
        // given
        var storageMap = storage.getStorageMap();
        var trainee = createTestTrainee();
        storageMap.put("trainee:1", trainee);

        // when
        storage.remove(trainee);

        // then
        assertThat(storageMap).doesNotContainKey("trainee:1");
        assertThat(storageMap).doesNotContainValue(trainee);
    }

    private Trainee createTestTrainee() {
        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");
        trainee.setUsername("FirstName.LastName");
        trainee.setPassword("0123456789");
        trainee.setAddress("Test Address");
        trainee.setDateOfBirth(LocalDate.now());
        trainee.setActive(true);

        return trainee;
    }

    private Trainer createTestTrainer() {
        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("FirstName");
        trainer.setLastName("LastName");
        trainer.setUsername("FirstName.LastName");
        trainer.setPassword("9876543210");
        trainer.setActive(false);
        trainer.setSpecialization(TrainingType.FITNESS);

        return trainer;
    }

    private Training createTestTraining() {
        var training = new Training();
        training.setId(1L);
        training.setTrainee(createTestTrainee());
        training.setTrainer(createTestTrainer());
        training.setTrainingName("Test Training");
        training.setTrainingType(TrainingType.FITNESS);
        training.setTrainingDate(LocalDate.now().atStartOfDay());
        training.setTrainingDuration(java.time.Duration.ofHours(1));

        return training;
    }
}

