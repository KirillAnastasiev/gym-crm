package com.epam.laboratory.app.service;

import com.epam.laboratory.app.repository.TrainingDao;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.exception.NoSuchEntityException;
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
import java.util.Optional;

import static java.time.Duration.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
    @DisplayName("Test of the method creteTraining - should create training")
    void testCreteTraining() {
        // given
        var training = createTestTraining();

        given(trainingDao.save(any(Training.class))).willReturn(training);

        // when
        var actualResult = trainingServiceImpl.createTraining(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(training);

        verify(trainingDao, times(1)).save(any(Training.class));
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method updateTraining - should update training")
    void testUpdateTraining() {
        // given
        var training = createTestTraining();
        training.setTrainingName("Updated Training");

        given(trainingDao.update(any(Training.class))).willReturn(training);

        // when
        var actualResult = trainingServiceImpl.updateTraining(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(training);
        assertThat(actualResult.getTrainingName()).isEqualTo("Updated Training");

        verify(trainingDao, times(1)).update(any(Training.class));
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method deleteTraining - should delete training")
    void testDeleteTraining() {
        // given
        var training = createTestTraining();

        doNothing().when(trainingDao).delete(any(Training.class));

        // when
        trainingServiceImpl.deleteTraining(training);

        // then
        verify(trainingDao, times(1)).delete(any(Training.class));
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method selectTraining - successful execution, should return training by training name")
    void testSelectTraining_positive() {
        // given
        var training = createTestTraining();

        given(trainingDao.findByTrainingName(anyString())).willReturn(Optional.of(training));

        // when
        var actualResult = trainingServiceImpl.selectTraining("Test Training");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(training);

        verify(trainingDao, times(1)).findByTrainingName(anyString());
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method selectTraining - failure execution, should throw NoSuchEntityException")
    void testSelectTraining_negative() {
        // given
        given(trainingDao.findByTrainingName(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> trainingServiceImpl.selectTraining("Test Training"))
                 .isInstanceOf(NoSuchEntityException.class)
                 .hasMessageContaining("Training with training name Test Training not found");

        verify(trainingDao, times(1)).findByTrainingName(anyString());
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method selectAllTrainings - successful execution, should return collection of all trainings")
    void testSelectAllTrainings_positive() {
        // given
        given(trainingDao.findAll()).willReturn(List.of(new Training() {{ setId(1L); }}, new Training() {{ setId(2L); }}, new Training() {{ setId(3L);}}));

        // when
        var actualResult = trainingServiceImpl.selectAllTrainings();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).hasSize(3);
        actualResult.forEach(training -> assertThat(training).isInstanceOf(Training.class));

        verify(trainingDao, times(1)).findAll();
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method selectAllTrainings - should return empty collection if there are no trainings")
    void testSelectAllTrainings_negative() {
        // given
        given(trainingDao.findAll()).willReturn(Collections.emptyList());

        // when
        var actualResult = trainingServiceImpl.selectAllTrainings();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(trainingDao, times(1)).findAll();
        verifyNoMoreInteractions(trainingDao);
    }

    private Training createTestTraining() {
        var training = new Training();
        training.setId(1L);
        training.setTrainee(new Trainee());
        training.setTrainer(new Trainer());
        training.setTrainingName("Test Training");
        training.setTrainingType(TrainingType.FITNESS);
        training.setTrainingDate(LocalDateTime.now());
        training.setTrainingDuration(ofHours(1));

        return training;
    }
}