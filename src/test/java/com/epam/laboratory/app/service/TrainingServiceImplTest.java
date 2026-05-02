package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.repository.TrainingDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static java.time.Duration.ofHours;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {
    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingServiceImpl trainingServiceImpl;

    @Test
    @DisplayName("Test of the method create - should create training")
    void testCreate() {
        // given
        var training = createTestTraining();

        given(trainingDao.save(any(Training.class))).willReturn(training);

        // when
        var actualResult = trainingServiceImpl.create(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(training);

        verify(trainingDao, times(1)).save(any(Training.class));
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method update - should update training")
    void testUpdate() {
        // given
        var training = createTestTraining();
        training.setTrainingName("Updated Training");

        given(trainingDao.update(any(Training.class))).willReturn(training);

        // when
        var actualResult = trainingServiceImpl.update(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(training);
        assertThat(actualResult.getTrainingName()).isEqualTo("Updated Training");

        verify(trainingDao, times(1)).update(any(Training.class));
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method delete - should delete training")
    void testDelete() {
        // given
        var training = createTestTraining();

        doNothing().when(trainingDao).delete(any(Training.class));

        // when
        trainingServiceImpl.delete(training);

        // then
        verify(trainingDao, times(1)).delete(any(Training.class));
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method selectById - should return training by id")
    void testSelectById_positive() {
        // given
        var training = createTestTraining();

        given(trainingDao.findById(anyLong(), any())).willReturn(Optional.of(training));

        // when
        var actualResult = trainingServiceImpl.selectById(1L, Training.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(training);

        verify(trainingDao, times(1)).findById(anyLong(), any());
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method selectById - should return empty optional if there is no training with given id")
    void testSelectById_negative() {
        // given
        given(trainingDao.findById(anyLong(), any())).willReturn(Optional.empty());

        // when
        var actualResult = trainingServiceImpl.selectById(1L, Training.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(trainingDao, times(1)).findById(anyLong(), any());
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method selectByCondition - should return collection of trainings that satisfy condition")
    void testSelectByCondition_positive() {
        // given
        var training = createTestTraining();

        given(trainingDao.findByCondition(any(), any())).willReturn(Collections.singletonList(training));

        // when
        var actualResult = trainingServiceImpl.selectByCondition((cb, root) ->
                cb.equal(root.get("trainingName"), "Test Training"), Training.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        actualResult.forEach(t ->  {
            assertThat(t).isInstanceOf(Training.class);
            assertThat(t.getTrainingName()).isEqualTo("Test Training");
        });

        verify(trainingDao, times(1)).findByCondition(any(), any());
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method selectByCondition - should return empty collection if there are no trainings that satisfy condition")
    void testSelectByCondition_negative() {
        // given
        given(trainingDao.findByCondition(any(), any())).willReturn(Collections.emptyList());

        // when
        var actualResult =  trainingServiceImpl.selectByCondition((cb, root) ->
                cb.equal(root.get("trainingName"), "Test Training"), Training.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(trainingDao, times(1)).findByCondition(any(), any());
        verifyNoMoreInteractions(trainingDao);
    }

    private Training createTestTraining() {
        var training = new Training();
        training.setId(1L);
        training.setTrainee(new Trainee());
        training.setTrainer(new Trainer());
        training.setTrainingName("Test Training");
        training.setTrainingType(new TrainingType());
        training.setTrainingDate(LocalDateTime.now());
        training.setTrainingDuration(ofHours(1));

        return training;
    }
}