package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageTest {
    @Mock
    private Storage.EntityKeyMapper entityKeyMapper;

    @InjectMocks
    private Storage storage;

    @BeforeEach
    void setUp() {
        storage.getStorageMap().clear();
    }

    @Test
    @DisplayName("Test of the method store - should store entity in storage map with generated key")
    void testStore() {
        // given
        var storageMap = storage.getStorageMap();
        var trainee = createTestTrainee();

        given(entityKeyMapper.incrementAndGetLastUsedId(any(Class.class))).willReturn(1L);
        given(entityKeyMapper.getKey(anyLong(), any(Class.class))).willReturn("trainee:1");

        // when
        storage.store(trainee);

        // then
        assertThat(storageMap).isNotEmpty();
        assertThat(storageMap).containsKey("trainee:1");
        assertThat(storageMap).containsValue(trainee);
        assertThat(storageMap.get("trainee:1")).isSameAs(trainee);

        verify(entityKeyMapper).incrementAndGetLastUsedId(any(Class.class));
        verify(entityKeyMapper).getKey(anyLong(), any(Class.class));
        verifyNoMoreInteractions(entityKeyMapper);
    }

    @Test
    @DisplayName("Test of the method update - should update entity in storage map")
    void testUpdate() {
        // given
        var storageMap = storage.getStorageMap();
        var trainee = createTestTrainee();
        trainee.setFirstName("UpdatedFirstName");

        given(entityKeyMapper.getKey(anyLong(), any(Class.class))).willReturn("trainee:1");

        // when
        storage.update(trainee);

        // then
        assertThat(storageMap).isNotEmpty();
        assertThat(storageMap).containsKey("trainee:1");
        assertThat(storageMap).containsValue(trainee);
        assertThat(storageMap.get("trainee:1")).isSameAs(trainee);

        verify(entityKeyMapper).getKey(anyLong(), any(Class.class));
        verifyNoMoreInteractions(entityKeyMapper);
    }

    @Test
    @DisplayName("Test of the method remove - should remove entity from storage map by key")
    void testRemove() {
        // given
        var storageMap = storage.getStorageMap();
        var trainee = createTestTrainee();
        storageMap.put("trainee:1", trainee);

        given(entityKeyMapper.getKey(anyLong(), any(Class.class))).willReturn("trainee:1");

        // when
        storage.remove(trainee);

        // then
        assertThat(storageMap).doesNotContainKey("trainee:1");
        assertThat(storageMap).doesNotContainValue(trainee);

        verify(entityKeyMapper).getKey(anyLong(), any(Class.class));
        verifyNoMoreInteractions(entityKeyMapper);
    }

    @Test
    @DisplayName("Test of the method retrieveById - should retrieve entity from storage map by key")
    void testRetrieveById_positive() {
        // given
        var storageMap = storage.getStorageMap();
        var trainee = createTestTrainee();
        storageMap.put("trainee:1", trainee);

        given(entityKeyMapper.getKey(anyLong(), eq(Trainee.class))).willReturn("trainee:1");

        // when
        var retrievedTrainee = storage.retrieveById(1L, Trainee.class);

        // then
        assertThat(retrievedTrainee).isSameAs(trainee);

        verify(entityKeyMapper).getKey(anyLong(), eq(Trainee.class));
        verifyNoMoreInteractions(entityKeyMapper);
    }

    @Test
    @DisplayName("Test of the method retrieveById - should return null if entity with given id does not exist in storage map")
    void testRetrieveById_negative() {
        // given
        given(entityKeyMapper.getKey(anyLong(), eq(Trainee.class))).willReturn("trainee:1");

        // when
        var retrievedTrainee = storage.retrieveById(1L, Trainee.class);

        // then
        assertThat(retrievedTrainee).isNull();

        verify(entityKeyMapper).getKey(anyLong(), eq(Trainee.class));
        verifyNoMoreInteractions(entityKeyMapper);
    }

    @Test
    @DisplayName("Test of the method retrieveByCondition - should retrieve collection of entities that satisfy given condition")
    void testRetrieveByCondition_positive() {
        // given
        var storageMap = storage.getStorageMap();
        var training = createTestTraining();
        var trainee = createTestTrainee();
        storageMap.put("training:1", training);
        storageMap.put("trainee:1", trainee);

        // when
        var actualResult = storage.retrieveByCondition(t -> t.getTrainingType() == TrainingType.FITNESS, Training.class);

        // then
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(1);
        assertThat(actualResult).contains(training);
    }

    @Test
    @DisplayName("Test of the method retrieveByCondition - should return empty collection if no entities satisfy given condition")
    void testRetrieveByCondition_negative() {
        // given
        var storageMap = storage.getStorageMap();
        var training = createTestTraining();
        var trainee = createTestTrainee();
        storageMap.put("training:1", training);
        storageMap.put("trainee:1", trainee);

        // when
        var actualResult = storage.retrieveByCondition(t -> t.getTrainingType() == TrainingType.YOGA, Training.class);

        // then
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();
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

