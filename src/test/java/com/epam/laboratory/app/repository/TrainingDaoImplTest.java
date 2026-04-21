package com.epam.laboratory.app.repository;

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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static java.time.Duration.ofHours;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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

        given(storage.retrieveById(anyLong(), any())).willReturn(training);

        // when
        var actualResult = trainingDao.findById(1L, Training.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(training);

        verify(storage, times(1)).retrieveById(anyLong(), any());
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findById - should return empty optional when training with given id does not exist")
    void testFindById_negative() {
        // given
        given(storage.retrieveById(anyLong(), any())).willReturn(null);

        // when
        var actualResult = trainingDao.findById(1L, Training.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(storage, times(1)).retrieveById(anyLong(), any());
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method save - should save training and return it with generated id")
    void testSave() {
        // given
        var training = createTestTraining();
        training.setId(1L);

        doNothing().when(storage).store(any(Training.class));

        // when
        var actualResult = trainingDao.save(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(1);
        assertThat(actualResult).isEqualTo(training);

        verify(storage, times(1)).store(any(Training.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method update - should update existing training and return it")
    void testUpdate() {
        // given
        var training = createTestTraining();
        training.setId(1L);
        training.setTrainingType(TrainingType.YOGA);

        doNothing().when(storage).update(any(Training.class));

        // when
        var actualResult = trainingDao.update(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(training);

        verify(storage, times(1)).update(any(Training.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method delete - should delete existing training")
    void testDelete() {
        // given
        var training = createTestTraining();
        training.setId(1L);

        doNothing().when(storage).remove(any(Training.class));

        // when
        trainingDao.delete(training);

        // then
        verify(storage, times(1)).remove(any(Training.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findByCondition - should return collection of trainings that satisfy condition")
    void testFindByCondition_positive() {
        // given
        var training = createTestTraining();
        training.setId(1L);

        given(storage.retrieveByCondition(any(Predicate.class), any(Class.class))).willReturn(Collections.singletonList(training));

        // when
        var actualResult = trainingDao.findByCondition(t -> "Test Training".equals(t.getTrainingName()), Training.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).contains(training);

        verify(storage, times(1)).retrieveByCondition(any(Predicate.class), any(Class.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findByCondition - should return empty collection if there are no trainings that satisfy condition")
    void testFindByConditions_negative() {
        // given
        given(storage.retrieveByCondition(any(Predicate.class), any(Class.class))).willReturn(Collections.emptyList());

        // when
        var actualResult = trainingDao.findByCondition(t -> "Test Training".equals(t.getTrainingName()), Training.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(storage, times(1)).retrieveByCondition(any(Predicate.class), any(Class.class));
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