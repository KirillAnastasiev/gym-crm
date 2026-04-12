package com.epam.laboratory.app.service;

import com.epam.laboratory.app.dao.TrainingDao;
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
import java.util.Optional;

import static java.time.Duration.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceImplTest {
    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingServiceImpl trainingServiceImpl;

    @Test
    @DisplayName("Test of the method creteTraining - should create training")
    public void testCreteTraining() {
        // given
        var training = createTestTraining();

        given(trainingDao.save(any(Training.class))).willReturn(training);

        // when
        var actualResult = trainingServiceImpl.creteTraining(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(training);

        verify(trainingDao, times(1)).save(any(Training.class));
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method selectTraining - successful execution, should return training by id")
    public void testSelectTraining_positive() {
        // given
        var training = createTestTraining();

        given(trainingDao.findById(anyLong())).willReturn(Optional.of(training));

        // when
        var actualResult = trainingServiceImpl.selectTraining(1L);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(training);

        verify(trainingDao, times(1)).findById(anyLong());
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method selectTraining - failure execution, should throw NoSuchEntityException")
    public void testSelectTraining_negative() {
        // given
        given(trainingDao.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> trainingServiceImpl.selectTraining(1L))
                 .isInstanceOf(NoSuchEntityException.class)
                 .hasMessageContaining("Training with id 1 not found");

        verify(trainingDao, times(1)).findById(anyLong());
        verifyNoMoreInteractions(trainingDao);
    }

    private Training createTestTraining() {
        return Training.builder()
                .id(1L)
                .trainee(new Trainee())
                .trainer(new Trainer())
                .trainingName("Test Training")
                .trainingType(TrainingType.FITNESS)
                .trainingDate(LocalDateTime.now())
                .trainingDuration(ofHours(1))
                .build();
    }
}