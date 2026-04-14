package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Trainee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
        assertThat(actualResult).isEmpty();

        verify(storage, times(1)).get(anyString());
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