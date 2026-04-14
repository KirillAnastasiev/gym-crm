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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class TrainingDaoImplTest {
    @Mock
    private Map<String, Training> storage;

    @InjectMocks
    private TrainingDaoImpl trainingDao;

    @Test
    @DisplayName("Test of the method save - should save training with id 1 when storage is empty")
    public void testSaveWithEmptyStorage() {
        // given
        var training = createTestTraining();
        given(storage.values()).willReturn(Collections.emptySet());
        given(storage.put(anyString(), any(Training.class))).willReturn(training);

        // when
        var actualResult = trainingDao.save(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(1);
        assertThat(actualResult).isEqualTo(training);

        verify(storage, times(1)).values();
        verify(storage, times(1)).put(anyString(), any(Training.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method save - should save training with id 4 when storage contains trainings with ids 1, 2 and 3")
    public void testSaveWithNotEmptyStorage() {
        // given
        var training = createTestTraining();
        given(storage.values()).willReturn(Set.of(new Training() {{ setId(1L); }}, new Training() {{ setId(2L); }}, new Training() {{ setId(3L); }}));
        given(storage.put(anyString(), any(Training.class))).willReturn(training);

        // when
        var actualResult = trainingDao.save(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(4);
        assertThat(actualResult).isEqualTo(training);

        verify(storage, times(1)).values();
        verify(storage, times(1)).put(anyString(), any(Training.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findById - should return training when training with given id exists in storage")
    public void testFindById_positive() {
        // given
        var training = createTestTraining();
        training.setId(1L);
        given(storage.get(anyString())).willReturn(training);

        // when
        var actualResult = trainingDao.findById("training:1");

        // then
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(training);

        verify(storage, times(1)).get(anyString());
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findById - should return empty optional when training with given id does not exist in storage")
    public void testFindById_negative() {
        // given
        given(storage.get(anyString())).willReturn(null);

        // when
        var actualResult = trainingDao.findById("training:1");

        // then
        assertThat(actualResult).isNotPresent();

        verify(storage, times(1)).get(anyString());
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