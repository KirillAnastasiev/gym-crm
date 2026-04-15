package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.time.Duration.ofHours;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingDaoImplTest {
    @Mock
    private Storage storage;

    @InjectMocks
    private TrainingDaoImpl trainingDao;

    @Test
    @DisplayName("Test of the method findById - should return training when training with given id exists")
    void testFindById_positive() {
        // given
        var training = createTestTraining();
        training.setId(1L);

        given(storage.get(anyString())).willReturn(training);

        // when
        var actualResult = trainingDao.findById(1L);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(training);

        verify(storage, times(1)).get(anyString());
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findById - should return empty optional when training with given id does not exist")
    void testFindById_negative() {
        // given
        given(storage.get(anyString())).willReturn(null);

        // when
        var actualResult = trainingDao.findById(1L);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(storage, times(1)).get(anyString());
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findAll - should return list of all trainings")
    void testFindAll_positive() {
        // given
        var training1 = createTestTraining();
        var training2 = createTestTraining();
        training1.setId(1L);
        training2.setId(2L);

        given(storage.values()).willReturn(List.of(training1, training2));

        // when
        var actualResult = trainingDao.findAll();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).hasSize(2);
        assertThat(actualResult).contains(training1, training2);

        verify(storage, times(1)).values();
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findAll - should return empty list when no trainings exist")
    void testFindAll_negative() {
        // given
        given(storage.values()).willReturn(Collections.emptyList());

        // when
        var actualResult = trainingDao.findAll();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(storage, times(1)).values();
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method save - should save training and return it with generated id")
    void testSave() {
        // given
        var training = createTestTraining();

        given(storage.keySet()).willReturn(Collections.emptySet());

        // when
        var actualResult = trainingDao.save(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(1);
        assertThat(actualResult).isEqualTo(training);

        verify(storage, times(1)).keySet();
        verify(storage, times(1)).put(anyString(), any(Training.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method update - should update existing training and return it")
    void testUpdate() {
        // given
        var training = createTestTraining();
        training.setId(1L);
        training.setTrainingType(TrainingType.YOGA);

        doNothing().when(storage).put(anyString(), any(Training.class));

        // when
        var actualResult = trainingDao.update(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(training);

        verify(storage, times(1)).put(anyString(), any(Training.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method delete - should delete existing training")
    void testDelete() {
        // given
        var training = createTestTraining();
        training.setId(1L);

        doNothing().when(storage).remove(anyString());

        // when
        trainingDao.delete(training);

        // then
        verify(storage, times(1)).remove(anyString());
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findByTrainingName - should return training when training with given name exists")
    void testFindByTrainingName_positive() {
        // given
        var training = createTestTraining();
        training.setId(1L);

        given(storage.values()).willReturn(List.of(training));

        // when
        var actualResult = trainingDao.findByTrainingName("Test Training");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(training);

        verify(storage, times(1)).values();
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findByTrainingName - should return empty optional when training with given name does not exist")
    void testFindByTrainingName_negative() {
        // given
        given(storage.values()).willReturn(Collections.emptyList());

        // when
        var actualResult = trainingDao.findByTrainingName("Test Training");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(storage, times(1)).values();
        verifyNoMoreInteractions(storage);
    }

    private Training createTestTraining() {
        var training = new Training();
        training.setTrainingName("Test Training");
        training.setTrainee(new Trainee());
        training.setTrainer(new Trainer());
        training.setTrainingType(TrainingType.FITNESS);
        training.setTrainingDate(LocalDateTime.now());
        training.setTrainingDuration(ofHours(1));

        return training;
    }
}