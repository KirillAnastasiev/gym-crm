package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.repository.TraineeDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

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

        try (var staticMockPasswordGenerator = mockStatic(PasswordGenerator.class)) {
            staticMockPasswordGenerator.when(PasswordGenerator::generatePassword).thenReturn(generatedPassword);

            given(traineeDao.findByCondition(any(Predicate.class), any(Class.class))).willReturn(Collections.emptyList());
            given(usernameHelper.generateUsername(anyString(), anyString(), anyCollection())).willReturn(generatedUsername);
            given(traineeDao.save(any(Trainee.class))).willReturn(trainee);

            // when
            var actualResult = traineeService.createTrainee(trainee);

            // then
            assertThat(actualResult).isNotNull();
            assertThat(actualResult.getId()).isNotNull();
            assertThat(actualResult).isEqualTo(trainee);
            assertThat(actualResult.getPassword()).isEqualTo(generatedPassword);
            assertThat(actualResult.getUsername()).isEqualTo(generatedUsername);

            verify(traineeDao, times(1)).findByCondition(any(Predicate.class), any(Class.class));
            verify(usernameHelper, times(1)).generateUsername(anyString(), anyString(), anyCollection());
            verify(traineeDao, times(1)).save(any(Trainee.class));
            verifyNoMoreInteractions(usernameHelper);
            verifyNoMoreInteractions(traineeDao);
        }
    }

    @Test
    @DisplayName("Test of the method createTrainee - should create trainee with non-unique username and return")
    void testCreateTrainee_nonUniqueUsername() {
        // given
        var trainee = createTestTrainee();
        var generatedPassword = "1234567890";
        var generatedUsernameWithSuffix = "FirstName.LastName2";

        try (var staticMockPasswordGenerator = mockStatic(PasswordGenerator.class)) {
            staticMockPasswordGenerator.when(PasswordGenerator::generatePassword).thenReturn(generatedPassword);

            given(traineeDao.findByCondition(any(Predicate.class), any(Class.class))).willReturn(Collections.singletonList(trainee));
            given(usernameHelper.generateUsername(anyString(), anyString(), anyCollection())).willReturn(generatedUsernameWithSuffix);
            given(traineeDao.save(any(Trainee.class))).willReturn(trainee);

            // when
            var actualResult = traineeService.createTrainee(trainee);

            // then
            assertThat(actualResult).isNotNull();
            assertThat(actualResult.getId()).isNotNull();
            assertThat(actualResult).isEqualTo(trainee);
            assertThat(actualResult.getPassword()).isEqualTo(generatedPassword);
            assertThat(actualResult.getUsername()).isEqualTo(generatedUsernameWithSuffix);

            verify(traineeDao, times(1)).findByCondition(any(Predicate.class), any(Class.class));
            verify(usernameHelper, times(1)).generateUsername(anyString(), anyString(), anyCollection());
            verify(traineeDao, times(1)).save(any(Trainee.class));
            verifyNoMoreInteractions(usernameHelper);
            verifyNoMoreInteractions(traineeDao);
        }
    }

    @Test
    @DisplayName("Test of the method updateTrainee - should update trainee with unique username and return")
    void testUpdateTrainee_uniqueUsername() {
        // given
        var trainee = createTestTrainee();
        trainee.setFirstName("UpdatedFirstName");
        var generatedUsername = "UpdatedFirstName.LastName";

        given(traineeDao.findByCondition(any(Predicate.class), any(Class.class))).willReturn(Collections.emptyList());
        given(usernameHelper.generateUsername(anyString(), anyString(), anyCollection())).willReturn(generatedUsername);
        given(traineeDao.update(any(Trainee.class))).willReturn(trainee);

        // when
        var actualResult = traineeService.updateTrainee(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);
        assertThat(actualResult.getFirstName()).isEqualTo("UpdatedFirstName");
        assertThat(actualResult.getUsername()).isEqualTo(generatedUsername);

        verify(traineeDao, times(1)).findByCondition(any(Predicate.class), any(Class.class));
        verify(usernameHelper, times(1)).generateUsername(anyString(), anyString(), anyCollection());
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
        var generatedUsernameWithSuffix = "UpdatedFirstName.LastName2";

        given(traineeDao.findByCondition(any(Predicate.class), eq(Trainee.class))).willReturn(Collections.singletonList(trainee));
        given(usernameHelper.generateUsername(anyString(), anyString(), anyCollection())).willReturn(generatedUsernameWithSuffix);
        given(traineeDao.update(any(Trainee.class))).willReturn(trainee);

        // when
        var actualResult = traineeService.updateTrainee(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);
        assertThat(actualResult.getFirstName()).isEqualTo("UpdatedFirstName");
        assertThat(actualResult.getUsername()).isEqualTo(generatedUsernameWithSuffix);

        verify(traineeDao, times(1)).findByCondition(any(Predicate.class), eq(Trainee.class));
        verify(usernameHelper, times(1)).generateUsername(anyString(), anyString(), anyCollection());
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
    @DisplayName("Test of the method selectTraineesByCondition - should return collection of trainees that satisfy condition")
    void testSelectTraineesByCondition_positive() {
        // given
        var trainee = createTestTrainee();
        trainee.setUsername("FirstName.LastName");

        given(traineeDao.findByCondition(any(Predicate.class), any(Class.class))).willReturn(Collections.singletonList(trainee));

        // when
        var actualResult = traineeService.selectTraineesByCondition(t -> "FirstName.LastName".equals(t.getUsername()));

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).hasSize(1);
        actualResult.forEach(t -> {
            assertThat(t).isInstanceOf(Trainee.class);
            assertThat(t.getUsername()).isEqualTo("FirstName.LastName");
        });

        verify(traineeDao, times(1)).findByCondition(any(Predicate.class), any(Class.class));
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method selectTraineesByCondition - should return empty collection if there are no trainees that satisfy condition")
    void testSelectTraineeByCondition_negative() {
        // given
        given(traineeDao.findByCondition(any(Predicate.class), any(Class.class))).willReturn(Collections.emptyList());

        // when
        var actualResult = traineeService.selectTraineesByCondition(t -> "FirstName.LastName".equals(t.getUsername()));

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(traineeDao, times(1)).findByCondition(any(Predicate.class), any(Class.class));
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