package com.epam.laboratory.app.service;

import com.epam.laboratory.app.dao.TraineeDao;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.util.PasswordGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Nested
@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {
    @Mock
    private TraineeDao traineeDao;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    @DisplayName("Test of the method createTrainee - should create trainee with generated password")
    public void testCreateTrainee() {
        // given
        var trainee = createTestTrainee();
        var generatedPassword = "generatedPassword";

        given(passwordGenerator.generatePassword()).willReturn(generatedPassword);
        given(traineeDao.save(any(Trainee.class))).willReturn(trainee);

        // when
        var actualResult = traineeService.createTrainee(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);
        assertThat(actualResult.getPassword()).isEqualTo(generatedPassword);

        verify(passwordGenerator, times(1)).generatePassword();
        verify(traineeDao, times(1)).save(any(Trainee.class));
        verifyNoMoreInteractions(passwordGenerator);
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method updateTrainee - should update trainee and return")
    public void testUpdateTrainee() {
        // given
        var trainee = createTestTrainee();
        trainee.setFirstName("UpdatedFirstName");

        given(traineeDao.update(any(Trainee.class))).willReturn(trainee);

        // when
        var actualResult = traineeService.updateTrainee(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);
        assertThat(actualResult.getFirstName()).isEqualTo("UpdatedFirstName");

        verify(traineeDao, times(1)).update(any(Trainee.class));
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method deleteTrainee - should delete trainee")
    public void testDeleteTrainee() {
        // given
        var trainee = createTestTrainee();

        doNothing().when(traineeDao).delete(any(Trainee.class));

        // when
        traineeService.deleteTrainee(trainee);

        // then
        verify(traineeDao, times(1)).delete(any(Trainee.class));
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method selectTrainee - successful execution, should return trainee by id")
    public void testSelectTrainee_positive() {
        // given
        var trainee = createTestTrainee();

        given(traineeDao.findById(anyString())).willReturn(java.util.Optional.of(trainee));

        // when
        var actualResult = traineeService.selectTrainee("trainee:1");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);

        verify(traineeDao, times(1)).findById(anyString());
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method selectTrainee - failure execution, should throw NoSuchEntityException")
    public void testSelectTrainee_negative() {
        // given
         given(traineeDao.findById(anyString())).willReturn(java.util.Optional.empty());

         // when & then
         assertThatThrownBy(() -> traineeService.selectTrainee("trainee:1"))
                  .isInstanceOf(NoSuchEntityException.class)
                  .hasMessageContaining("Trainee not found");

         verify(traineeDao, times(1)).findById(anyString());
         verifyNoMoreInteractions(traineeDao);
    }

    private Trainee createTestTrainee() {
        var trainee =  new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");
        trainee.setUsername("FirstName.LastName");
        trainee.setDateOfBirth(LocalDate.now());
        trainee.setActive(true);

        return trainee;
    }
}