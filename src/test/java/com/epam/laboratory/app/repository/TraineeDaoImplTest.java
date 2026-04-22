package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeDaoImplTest {
    @Mock
    private Storage storage;

    @InjectMocks
    private TraineeDaoImpl traineeDao;

    @Test
    @DisplayName("Test of the method findById - should return trainee when trainee with given id exists")
    void testFindById_positive() {
        // given
        var trainee = createTestTrainee();
        trainee.setId(1L);

        given(storage.retrieveById(anyLong(), any())).willReturn(trainee);

        // when
        var actualResult = traineeDao.findById(1L, Trainee.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainee);

        verify(storage, times(1)).retrieveById(anyLong(), any());
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findById - should return empty optional when trainee with given id does not exist")
    void testFindById_negative() {
        // given
        given(storage.retrieveById(anyLong(), any())).willReturn(null);

        // when
        var actualResult = traineeDao.findById(1L, Trainee.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(storage, times(1)).retrieveById(anyLong(), any());
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findByCondition - should return collection of trainees that satisfy the condition")
    void testFindByCondition_positive() {
        // given
        var trainee1 = createTestTrainee();
        var trainee2 = createTestTrainee();
        trainee1.setId(1L);
        trainee2.setId(2L);

        given(storage.retrieveByCondition(any(Predicate.class), any(Class.class))).willReturn(List.of(trainee1, trainee2));

        // when
        var actualResult = traineeDao.findByCondition(t -> t.getFirstName().equals("FirstName"), Trainee.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        actualResult.forEach(t -> {
            assertThat(t).isInstanceOf(Trainee.class);
            assertThat(t.getFirstName()).isEqualTo("FirstName");
        });

        verify(storage, times(1)).retrieveByCondition(any(Predicate.class), any(Class.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findByCondition - should return empty collection when no trainees satisfy the condition")
    void testFindByCondition_negative() {
        // given
        given(storage.retrieveByCondition(any(Predicate.class), any(Class.class))).willReturn(Collections.emptyList());

        // when
        var actualResult = traineeDao.findByCondition(t -> t.getFirstName().equals("FirstName"), Trainee.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(storage, times(1)).retrieveByCondition(any(Predicate.class), any(Class.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method save - should save trainee and return it with generated id")
    void testSave() {
        // given
        var trainee = createTestTrainee();
        trainee.setId(1L);

        given(storage.store(any(Trainee.class))).willReturn(trainee);

        // when
        var actualResult = traineeDao.save(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(1L);
        assertThat(actualResult).isEqualTo(trainee);

        verify(storage, times(1)).store(any(Trainee.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method update - should update trainee and return it")
    void testUpdate() {
        // given
        var trainee = createTestTrainee();
        trainee.setId(1L);

        given(storage.update(any(Trainee.class))).willReturn(trainee);

        // when
        var actualResult = traineeDao.update(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);

        verify(storage, times(1)).update(any(Trainee.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method delete - should delete trainee")
    void testDelete() {
        // given
        var trainee = createTestTrainee();
        trainee.setId(1L);

        doNothing().when(storage).remove(any(Trainee.class));

        // when
        traineeDao.delete(trainee);

        // then
        verify(storage, times(1)).remove(any(Trainee.class));
        verifyNoMoreInteractions(storage);
    }

    private Trainee createTestTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");
        trainee.setPassword("1234567890");
        trainee.setAddress("Test Address");
        trainee.setDateOfBirth(LocalDate.now());
        trainee.setActive(true);

        return trainee;
    }
}