package com.epam.laboratory.app.service;

import com.epam.laboratory.app.repository.TraineeDao;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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

    @Mock
    private UsernameHelper usernameHelper;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    @DisplayName("Test of the method createTrainee - should create trainee with unique username and return")
    void testCreateTrainee_uniqueUsername() {
        // given
        var trainee = createTestTrainee();
        var generatedPassword = "1234567890";
        var generatedUsername = "FirstName.LastName";

        given(passwordGenerator.generatePassword()).willReturn(generatedPassword);
        given(usernameHelper.generateUsername(any(Trainee.class))).willReturn(generatedUsername);
        given(traineeDao.existsByUsername(anyString())).willReturn(false);
        given(traineeDao.save(any(Trainee.class))).willReturn(trainee);

        // when
        var actualResult = traineeService.createTrainee(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);
        assertThat(actualResult.getPassword()).isEqualTo(generatedPassword);
        assertThat(actualResult.getUsername()).isEqualTo(generatedUsername);

        verify(passwordGenerator, times(1)).generatePassword();
        verify(usernameHelper, times(1)).generateUsername(any(Trainee.class));
        verify(traineeDao, times(1)).existsByUsername(anyString());
        verify(traineeDao, times(1)).save(any(Trainee.class));
        verifyNoMoreInteractions(passwordGenerator);
        verifyNoMoreInteractions(usernameHelper);
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method createTrainee - should create trainee with non-unique username and return")
    void testCreateTrainee_nonUniqueUsername() {
        // given
        var trainee = createTestTrainee();
        var generatedPassword = "1234567890";
        var generatedUsername = "FirstName.LastName";
        var generatedUsernameWithSuffix = "FirstName.LastName2";

        given(passwordGenerator.generatePassword()).willReturn(generatedPassword);
        given(usernameHelper.generateUsername(any(Trainee.class))).willReturn(generatedUsername);
        given(traineeDao.existsByUsername(anyString())).willReturn(true);
        given(traineeDao.calculateTraineesWithFirstNameAndLastName(anyString(), anyString())).willReturn(1L);
        given(usernameHelper.generateUsername(any(Trainee.class), anyString())).willReturn(generatedUsernameWithSuffix);
        given(traineeDao.save(any(Trainee.class))).willReturn(trainee);

        // when
        var actualResult = traineeService.createTrainee(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);
        assertThat(actualResult.getPassword()).isEqualTo(generatedPassword);
        assertThat(actualResult.getUsername()).isEqualTo(generatedUsernameWithSuffix);

        verify(passwordGenerator, times(1)).generatePassword();
        verify(usernameHelper, times(1)).generateUsername(any(Trainee.class));
        verify(traineeDao, times(1)).existsByUsername(anyString());
        verify(traineeDao, times(1)).calculateTraineesWithFirstNameAndLastName(anyString(), anyString());
        verify(usernameHelper, times(1)).generateUsername(any(Trainee.class), anyString());
        verify(traineeDao, times(1)).save(any(Trainee.class));
        verifyNoMoreInteractions(passwordGenerator);
        verifyNoMoreInteractions(usernameHelper);
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method updateTrainee - should update trainee with unique username and return")
    void testUpdateTrainee_uniqueUsername() {
        // given
        var trainee = createTestTrainee();
        trainee.setFirstName("UpdatedFirstName");
        var generatedUsername = "UpdatedFirstName.LastName";

        given(usernameHelper.generateUsername(any(Trainee.class))).willReturn(generatedUsername);
        given(traineeDao.existsByUsername(anyString())).willReturn(false);
        given(traineeDao.update(any(Trainee.class))).willReturn(trainee);

        // when
        var actualResult = traineeService.updateTrainee(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);
        assertThat(actualResult.getFirstName()).isEqualTo("UpdatedFirstName");
        assertThat(actualResult.getUsername()).isEqualTo(generatedUsername);

        verify(usernameHelper, times(1)).generateUsername(any(Trainee.class));
        verify(traineeDao, times(1)).existsByUsername(anyString());
        verify(traineeDao, times(1)).update(any(Trainee.class));
        verifyNoMoreInteractions(usernameHelper);
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method updateTrainee - should update trainee with non-unique username and return")
    void testUpdateTrainee_nonUniqueUsername() {
        // given
        var trainee = createTestTrainee();
        trainee.setFirstName("UpdatedFirstName");
        var generatedUsername = "UpdatedFirstName.LastName";
        var generatedUsernameWithSuffix = "UpdatedFirstName.LastName2";

        given(usernameHelper.generateUsername(any(Trainee.class))).willReturn(generatedUsername);
        given(traineeDao.existsByUsername(anyString())).willReturn(true);
        given(traineeDao.calculateTraineesWithFirstNameAndLastName(anyString(), anyString())).willReturn(1L);
        given(usernameHelper.generateUsername(any(Trainee.class), anyString())).willReturn(generatedUsernameWithSuffix);
        given(traineeDao.update(any(Trainee.class))).willReturn(trainee);

        // when
        var actualResult = traineeService.updateTrainee(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);
        assertThat(actualResult.getFirstName()).isEqualTo("UpdatedFirstName");
        assertThat(actualResult.getUsername()).isEqualTo(generatedUsernameWithSuffix);

        verify(usernameHelper, times(1)).generateUsername(any(Trainee.class));
        verify(traineeDao, times(1)).existsByUsername(anyString());
        verify(traineeDao, times(1)).calculateTraineesWithFirstNameAndLastName(anyString(), anyString());
        verify(usernameHelper, times(1)).generateUsername(any(Trainee.class), anyString());
        verify(traineeDao, times(1)).update(any(Trainee.class));
        verifyNoMoreInteractions(usernameHelper);
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method deleteTrainee - should delete trainee")
    void testDeleteTrainee() {
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
    @DisplayName("Test of the method selectTrainee - successful execution, should return trainee by username")
    void testSelectTrainee_positive() {
        // given
        var trainee = createTestTrainee();
        trainee.setUsername("FirstName.LastName");

        given(traineeDao.findByUsername(anyString())).willReturn(Optional.of(trainee));

        // when
        var actualResult = traineeService.selectTrainee("FirstName.LastName");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);
        assertThat(actualResult.getUsername()).isEqualTo("FirstName.LastName");

        verify(traineeDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method selectTrainee - failure execution, should throw NoSuchEntityException")
    void testSelectTrainee_negative() {
        // given
        given(traineeDao.findByUsername(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> traineeService.selectTrainee("FirstName.LastName"))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("Trainee with username FirstName.LastName not found");

        verify(traineeDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method selectAllTrainees - should return collection of all trainees")
    void testSelectAllTrainees_positive() {
        // given
        given(traineeDao.findAll()).willReturn(List.of(new Trainee() {{ setId(1L); }}, new Trainee() {{ setId(2L); }}, new Trainee() {{ setId(3L);}}));

        // when
        var actualResult = traineeService.selectAllTrainees();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).hasSize(3);
        actualResult.forEach(trainee -> assertThat(trainee).isInstanceOf(Trainee.class));

        verify(traineeDao, times(1)).findAll();
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method selectAllTrainees - should return empty collection if there are no trainees")
    void testSelectAllTrainees_negative() {
        // given
        given(traineeDao.findAll()).willReturn(Collections.emptyList());

        // when
        var actualResult = traineeService.selectAllTrainees();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(traineeDao, times(1)).findAll();
        verifyNoMoreInteractions(traineeDao);
    }

    private Trainee createTestTrainee() {
        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");
        trainee.setDateOfBirth(LocalDate.now());
        trainee.setActive(true);

        return trainee;
    }
}