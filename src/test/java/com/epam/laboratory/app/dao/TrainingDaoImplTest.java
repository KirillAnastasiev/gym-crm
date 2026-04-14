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
import java.util.Map;
import java.util.Set;

import static java.time.Duration.ofHours;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class TrainingDaoImplTest {
    @Mock
    private Map<Long, Training> storage;

    @InjectMocks
    private TrainingDaoImpl trainingDao;

    @Test
    @DisplayName("Test of the method save - should save training with id 1 when storage is empty")
    public void testSaveWithEmptyStorage() {
        // given
        var training = createTestTraining();
        given(storage.keySet()).willReturn(Collections.emptySet());
        given(storage.put(anyLong(), any(Training.class))).willReturn(training);

        // when
        var actualResult = trainingDao.save(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(1);
        assertThat(actualResult).isEqualTo(training);

        verify(storage, times(1)).keySet();
        verify(storage, times(1)).put(anyLong(), any(Training.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method save - should save training with id 4 when storage contains trainings with ids 1, 2 and 3")
    public void testSaveWithNotEmptyStorage() {
        // given
        var training = createTestTraining();
        given(storage.keySet()).willReturn(Set.of(1L, 2L, 3L));
        given(storage.put(anyLong(), any(Training.class))).willReturn(training);

        // when
        var actualResult = trainingDao.save(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(4);
        assertThat(actualResult).isEqualTo(training);

        verify(storage, times(1)).keySet();
        verify(storage, times(1)).put(anyLong(), any(Training.class));
        verifyNoMoreInteractions(storage);
    }

    private Training createTestTraining() {
        var training = new Training();
        training.setTrainee(new Trainee());
        training.setTrainer(new Trainer());
        training.setTrainingName("Test Training");
        training.setTrainingType(TrainingType.FITNESS);
        training.setTrainingDate(LocalDateTime.now());
        training.setTrainingDuration(ofHours(1));

        return training;
    }
}