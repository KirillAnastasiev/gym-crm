package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.TrainingType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TrainerDaoImplTest {
    @Mock
    private Storage storage;

    @InjectMocks
    private TrainerDaoImpl trainerDao;

    @Test
    @DisplayName("Test of the method findById - should return trainer when trainer with given id exists")
    void testFindById_positive() {
        // given
        var trainer = createTestTrainer();
        trainer.setId(1L);

        given(storage.retrieveById(anyLong(), any())).willReturn(trainer);

        // when
        var actualResult = trainerDao.findById(1L, Trainer.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainer);

        verify(storage, times(1)).retrieveById(anyLong(), any());
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findById - should return empty optional when trainer with given id does not exist")
    void testFindById_negative() {
        // given
        given(storage.retrieveById(anyLong(), any())).willReturn(null);

        // when
        var actualResult = trainerDao.findById(1L, Trainer.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(storage, times(1)).retrieveById(anyLong(), any());
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method save - should save trainer and return it with generated id")
    void testSave() {
        // given
        var trainer = createTestTrainer();
        trainer.setId(1L);

        doNothing().when(storage).store(any(Trainer.class));

        // when
        var actualResult = trainerDao.save(trainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(1L);
        assertThat(actualResult).isEqualTo(trainer);

        verify(storage, times(1)).store(any(Trainer.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method update - should update trainer and return it")
    void testUpdate() {
        // given
        var trainer = createTestTrainer();
        trainer.setUsername("FirstName.LastName");
        trainer.setSpecialization(TrainingType.YOGA);
        trainer.setId(1L);

        doNothing().when(storage).update(any(Trainer.class));

        // when
        var actualResult = trainerDao.update(trainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainer);

        verify(storage, times(1)).update(any(Trainer.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method delete - should delete trainer from storage")
    void testDelete() {
        // given
        var trainer = createTestTrainer();
        trainer.setId(1L);

        doNothing().when(storage).remove(any(Trainer.class));

        // when
        trainerDao.delete(trainer);

        // then
        verify(storage, times(1)).remove(any(Trainer.class));
        verifyNoMoreInteractions(storage);
    }

    private Trainer createTestTrainer() {
        var trainer = new Trainer();
        trainer.setFirstName("FirstName");
        trainer.setLastName("LastName");
        trainer.setSpecialization(TrainingType.FITNESS);
        trainer.setActive(true);

        return trainer;
    }
}