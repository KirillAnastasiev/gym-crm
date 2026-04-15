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