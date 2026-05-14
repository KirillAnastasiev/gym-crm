package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.*;
import com.epam.laboratory.app.repository.TrainingDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static java.time.Duration.ofHours;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainingServiceImpl trainingServiceImpl;

    @Test
    @DisplayName("Test of the method registerNew - should register new training")
    void testRegisterNew_positive() {
        // given
        var training = createTestTraining();
        var trainee = training.getTrainee();
        var trainer = training.getTrainer();

        given(traineeService.selectByUsername(anyString())).willReturn(trainee);
        given(trainerService.selectByUsername(anyString())).willReturn(trainer);
        given(trainingDao.save(any(Training.class))).willReturn(training);

        // when
        var actualResult = trainingServiceImpl.registerNew(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(training);

        verify(traineeService, times(1)).selectByUsername(anyString());
        verify(trainerService, times(1)).selectByUsername(anyString());
        verify(trainingDao, times(1)).save(any(Training.class));
        verifyNoMoreInteractions(traineeService, trainerService, trainingTypeService, trainingDao);
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

    @Test
    @DisplayName("Test of the method selectForTrainee - should return collection of trainings for trainee that satisfy filter")
    void testSelectForTrainee_positive() {
        // given
        var training = createTestTraining();
        var trainingFilter = new TrainingFilter();
        trainingFilter.setTrainingTypeName("Test Training Type");

        given(trainingDao.findByCondition(any(), any())).willReturn(Collections.singletonList(training));

        // when
        var actualResult = trainingServiceImpl.selectForTrainee("FirstName.LastName", trainingFilter);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        actualResult.forEach(t ->  {
            assertThat(t).isInstanceOf(Training.class);
            assertThat(t.getTrainee()).isNotNull();
        });

        verify(trainingDao, times(1)).findByCondition(any(), any());
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method selectForTrainee - should return empty collection if there are no trainings for trainee that satisfy filter")
    void testSelectForTrainee_negative_notSatisfyingFilter() {
        // given
        var trainingFilter = new TrainingFilter();
        trainingFilter.setTrainingTypeName("Not Existing Training Type");

        given(trainingDao.findByCondition(any(), any())).willReturn(Collections.emptyList());

        // when
        var actualResult = trainingServiceImpl.selectForTrainee("FirstName.LastName", trainingFilter);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(trainingDao, times(1)).findByCondition(any(), any());
        verifyNoMoreInteractions(trainingDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, 'Trainee username must not be null'",
            "'', 'Trainee username must not be blank'"
    }, nullValues = "NULL")
    @DisplayName("Test of the method selectForTrainee - should throw IllegalArgumentException if trainee username is null or blank")
    void testSelectForTrainee_negative_invalidInput(String traineeUsername, String errorMessage) {
        // when && then
        assertThatThrownBy(() -> trainingServiceImpl.selectForTrainee(traineeUsername, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);

        verifyNoInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method selectForTrainer - should return collection of trainings for trainer that satisfy filter")
    void testSelectForTrainer_positive() {
        // given
        var training = createTestTraining();
        var trainingFilter = new TrainingFilter();
        trainingFilter.setTrainingTypeName("Test Training Type");

        given(trainingDao.findByCondition(any(), any())).willReturn(Collections.singletonList(training));

        // when
        var actualResult = trainingServiceImpl.selectForTrainer("FirstName.LastName1", trainingFilter);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        actualResult.forEach(t ->  {
            assertThat(t).isInstanceOf(Training.class);
            assertThat(t.getTrainer()).isNotNull();
        });

        verify(trainingDao, times(1)).findByCondition(any(), any());
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method selectForTrainer - should return empty collection if there are no trainings for trainer that satisfy filter")
    void testSelectForTrainer_negative_notSatisfyingFilter() {
        // given
        var trainingFilter = new TrainingFilter();
        trainingFilter.setTrainingTypeName("Not Existing Training Type");

        given(trainingDao.findByCondition(any(), any())).willReturn(Collections.emptyList());

        // when
        var actualResult = trainingServiceImpl.selectForTrainer("FirstName.LastName1", trainingFilter);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(trainingDao, times(1)).findByCondition(any(), any());
        verifyNoMoreInteractions(trainingDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, 'Trainer username must not be null'",
            "'', 'Trainer username must not be blank'"
    }, nullValues = "NULL")
    @DisplayName("Test of the method selectForTrainer - should throw IllegalArgumentException if trainer username is null or blank")
    void testSelectForTrainer_negative_invalidInput(String trainerUsername, String errorMessage) {
        // when && then
        assertThatThrownBy(() -> trainingServiceImpl.selectForTrainer(trainerUsername, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);

        verifyNoInteractions(trainingDao);
    }

    private Training createTestTraining() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");
        trainee.setUsername("FirstName.LastName");
        trainee.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setId(2L);
        trainer.setFirstName("FirstName");
        trainer.setLastName("LastName");
        trainer.setUsername("FirstName.LastName1");
        trainer.setActive(true);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Test Training Type");

        var training = new Training();
        training.setId(1L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Test Training");
        training.setTrainingType(trainingType);
        training.setTrainingDate(LocalDateTime.now());
        training.setTrainingDuration(ofHours(1));

        return training;
    }

}