package com.epam.laboratory.app.dao;

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
public class TrainerDaoImplTest {
    @Mock
    private Storage storage;

    @InjectMocks
    private TrainerDaoImpl trainerDao;

    @Test
    @DisplayName("Test of the method findById - should return trainer when trainer with given id exists")
    public void testFindById_positive() {
        // given
        var trainer = createTestTrainer();
        trainer.setId(1L);
        String key = "trainer:1";

        given(storage.get(key)).willReturn(trainer);

        // when
        var actualResult = trainerDao.findById(1L);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(trainer);

        verify(storage, times(1)).get(anyString());
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findById - should return empty optional when trainer with given id does not exist")
    public void testFindById_negative() {
        // given
        String key = "trainer:1";

        given(storage.get(key)).willReturn(null);

        // when
        var actualResult = trainerDao.findById(1L);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(storage, times(1)).get(anyString());
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findAll - should return list of trainers when trainers exist")
    public void testFindAll_positive() {
        // given
        var trainer1 = createTestTrainer();
        trainer1.setId(1L);
        var trainer2 = createTestTrainer();
        trainer2.setId(2L);

        given(storage.values()).willReturn(List.of(trainer1, trainer2));

        // when
        var actualResult = trainerDao.findAll();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).hasSize(2);
        assertThat(actualResult).containsExactlyInAnyOrder(trainer1, trainer2);

        verify(storage, times(1)).values();
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findAll - should return empty list when there are no trainers in storage")
    public void testFindAll_negative() {
        // given
        given(storage.values()).willReturn(Collections.EMPTY_LIST);

        // when
        var actualResult = trainerDao.findAll();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(storage, times(1)).values();
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method save - should save trainer and return it with generated id")
    public void testSave() {
        // given
        var trainer = createTestTrainer();
        String key = "trainer:1";

        given(storage.keySet()).willReturn(Collections.emptySet());

        // when
        var actualResult = trainerDao.save(trainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(1L);
        assertThat(actualResult).isEqualTo(trainer);

        verify(storage, times(1)).keySet();
        verify(storage, times(1)).put(anyString(), any(Trainer.class));
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