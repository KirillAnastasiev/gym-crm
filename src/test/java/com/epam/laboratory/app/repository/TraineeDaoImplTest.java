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
    @DisplayName("Test of the method findAll - should return collection with trainees when there are trainees in storage")
    void testFindAll_positive() {
        // given
        var trainee1 = createTestTrainee();
        var trainee2 = createTestTrainee();
        trainee1.setId(1L);
        trainee2.setId(2L);

        given(storage.values()).willReturn(List.of(trainee1, trainee2));

        // when
        var actualResult = traineeDao.findAll();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).containsExactly(trainee1, trainee2);

        verify(storage, times(1)).values();
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findAll - should return empty collection when there are no trainees in storage")
    void testFindAll_negative() {
        // given
        given(storage.values()).willReturn(Collections.emptyList());

        // when
        var actualResult = traineeDao.findAll();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(storage, times(1)).values();
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method save - should save trainee and return it with generated id")
    void testSave() {
        // given
        var trainee = createTestTrainee();
        trainee.setId(1L);

        doNothing().when(storage).store(any(Trainee.class));

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

        doNothing().when(storage).update(any(Trainee.class));

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

    @Test
    @DisplayName("Test of the method findByUsername - should return trainee when trainee with given username exists")
    void testFindByUsername_positive() {
        // given
        var trainee1 = createTestTrainee();
        var trainee2 = createTestTrainee();
        trainee1.setId(1L);
        trainee2.setId(2L);
        trainee1.setUsername("FirstName.LastName");
        trainee2.setUsername("FirstName.LastName2");

        given(storage.values()).willReturn(List.of(trainee1));

        // when
        var actualResult = traineeDao.findByUsername("FirstName.LastName");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainee1);

        verify(storage, times(1)).values();
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findByUsername - should return empty optional when trainee with given username does not exist")
    void testFindByUsername_negative() {
        // given
        given(storage.values()).willReturn(Collections.emptyList());

        // when
        var actualResult = traineeDao.findByUsername("FirstName.LastName");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(storage, times(1)).values();
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method existsByUsername - should return true when trainee with given username exists")
    void testExistsByUsername_positive() {
        // given
        var trainee = createTestTrainee();
        trainee.setId(1L);
        trainee.setUsername("FirstName.LastName");

        given(storage.values()).willReturn(Collections.singletonList(trainee));

        // when
        var actualResult = traineeDao.existsByUsername("FirstName.LastName");

        // then
        assertThat(actualResult).isTrue();

        verify(storage, times(1)).values();
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method existsByUsername - should return false when trainee with given username does not exist")
    void testExistsByUsername_negative() {
        // given
        given(storage.values()).willReturn(Collections.emptyList());

        // when
        var actualResult = traineeDao.existsByUsername("FirstName.LastName");

        // then
        assertThat(actualResult).isFalse();

        verify(storage, times(1)).values();
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method calculateTraineesWithFirstNameAndLastName - should return count of trainees with given first name and last name")
    void testCalculateTraineesWithFirstNameAndLastName() {
        // given
        var trainee1 = createTestTrainee();
        var trainee2 = createTestTrainee();
        var trainee3 = createTestTrainee();
        trainee1.setId(1L);
        trainee2.setId(2L);
        trainee3.setId(3L);

        given(storage.values()).willReturn(List.of(trainee1, trainee2, trainee3));

        // when
        var actualResult = traineeDao.calculateTraineesWithFirstNameAndLastName("FirstName", "LastName");

        // then
        assertThat(actualResult).isEqualTo(3);

        verify(storage, times(1)).values();
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