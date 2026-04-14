package com.epam.laboratory.app.dao;

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
import static org.mockito.ArgumentMatchers.anyString;
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
    public void testFindById_positive() {
        // given
        var trainee = createTestTrainee();
        trainee.setId(1L);
        String key = "trainee:1";
        given(storage.get(key)).willReturn(trainee);

        // when
        var actualResult = traineeDao.findById(1L);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(trainee);

        verify(storage, times(1)).get(anyString());
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findById - should return empty optional when trainee with given id does not exist")
    public void testFindById_negative() {
        // given
        String key = "trainee:1";
        given(storage.get(key)).willReturn(null);

        // when
        var actualResult = traineeDao.findById(1L);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(storage, times(1)).get(anyString());
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method findAll - should return collection with trainees when there are trainees in storage")
    public void testFindAll_positive() {
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
    public void testFindAll_negative() {
        // given
        given(storage.values()).willReturn(Collections.EMPTY_LIST);

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
    public void testSave() {
        // given
        var trainee = createTestTrainee();
        given(storage.keySet()).willReturn(Collections.emptySet());

        // when
        var actualResult = traineeDao.save(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(1L);
        assertThat(actualResult).isEqualTo(trainee);

        verify(storage, times(1)).keySet();
        verify(storage, times(1)).put(anyString(), any(Trainee.class));
        verifyNoMoreInteractions(storage);
    }

    @Test
    @DisplayName("Test of the method update - should update trainee and return it")
    public void testUpdate() {
        // given
        var trainee = createTestTrainee();
        trainee.setId(1L);

        // when
        var actualResult = traineeDao.update(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);

        verify(storage, times(1)).put(anyString(), any(Trainee.class));
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