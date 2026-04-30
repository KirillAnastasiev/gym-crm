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
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Nested
@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {
    @Mock
    private TraineeDao traineeDao;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    @DisplayName("Test of the method create - should create trainee with unique username and return")
    void testCreate_uniqueUsername() {
        // given
        var trainee = createTestTrainee();
        var generatedPassword = "1234567890";
        var generatedUsername = "FirstName.LastName";

        try (var staticMockPasswordGenerator = mockStatic(PasswordGenerator.class);
                var staticMockUserHelper = mockStatic(UsernameHelper.class)) {
            staticMockPasswordGenerator.when(PasswordGenerator::generatePassword).thenReturn(generatedPassword);
            staticMockUserHelper.when(() -> UsernameHelper.generateUsername(any(), any())).thenReturn(generatedUsername);

            given(traineeDao.save(any(Trainee.class))).willReturn(trainee);

            // when
            var actualResult = traineeService.create(trainee);

            // then
            assertThat(actualResult).isNotNull();
            assertThat(actualResult.getId()).isNotNull();
            assertThat(actualResult).isEqualTo(trainee);
            assertThat(actualResult.getPassword()).isEqualTo(generatedPassword);
            assertThat(actualResult.getUsername()).isEqualTo(generatedUsername);

            verify(traineeDao, times(1)).save(any(Trainee.class));
            verifyNoMoreInteractions(traineeDao);
        }
    }

    @Test
    @DisplayName("Test of the method create - should create trainee with non-unique username and return")
    void testCreate_nonUniqueUsername() {
        // given
        var trainee = createTestTrainee();
        var generatedPassword = "1234567890";
        var generatedUsernameWithSuffix = "FirstName.LastName2";

        try (var staticMockPasswordGenerator = mockStatic(PasswordGenerator.class);
                var staticMockUserHelper = mockStatic(UsernameHelper.class)) {
            staticMockPasswordGenerator.when(PasswordGenerator::generatePassword).thenReturn(generatedPassword);
            staticMockUserHelper.when(() -> UsernameHelper.generateUsername(any(), any())).thenReturn(generatedUsernameWithSuffix);

            given(traineeDao.save(any(Trainee.class))).willReturn(trainee);

            // when
            var actualResult = traineeService.create(trainee);

            // then
            assertThat(actualResult).isNotNull();
            assertThat(actualResult.getId()).isNotNull();
            assertThat(actualResult).isEqualTo(trainee);
            assertThat(actualResult.getPassword()).isEqualTo(generatedPassword);
            assertThat(actualResult.getUsername()).isEqualTo(generatedUsernameWithSuffix);

            verify(traineeDao, times(1)).save(any(Trainee.class));
            verifyNoMoreInteractions(traineeDao);
        }
    }

    @Test
    @DisplayName("Test of the method update - should update trainee with unique username and return")
    void testUpdate() {
        // given
        var trainee = createTestTrainee();
        trainee.setFirstName("UpdatedFirstName");

        given(traineeDao.update(any(Trainee.class))).willReturn(trainee);

        // when
        var actualResult = traineeService.update(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);

        verify(traineeDao, times(1)).update(any(Trainee.class));
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method delete - should delete trainee")
    void testDelete() {
        // given
        var trainee = createTestTrainee();

        doNothing().when(traineeDao).delete(any(Trainee.class));

        // when
        traineeService.delete(trainee);

        // then
        verify(traineeDao, times(1)).delete(any(Trainee.class));
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method selectById - should return trainee by id")
    void testSelectById_positive() {
        // given
        var trainee = createTestTrainee();

        given(traineeDao.findById(anyLong(), any())).willReturn(Optional.of(trainee));

        // when
        var actualResult = traineeService.selectById(1L, Trainee.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainee);

        verify(traineeDao, times(1)).findById(anyLong(), any());
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method selectById - should return empty optional if there is no trainee with given id")
    void testSelectById_negative() {
        // given
        given(traineeDao.findById(anyLong(), any())).willReturn(Optional.empty());

        // when
        var actualResult = traineeService.selectById(1L, Trainee.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotPresent();

        verify(traineeDao, times(1)).findById(anyLong(), any());
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method selectByCondition - should return collection of trainees that satisfy condition")
    void testSelectByCondition_positive() {
        // given
        var trainee = createTestTrainee();
        trainee.setUsername("FirstName.LastName");

        given(traineeDao.findByCondition(any(), any())).willReturn(Collections.singletonList(trainee));

        // when
        var actualResult = traineeService.selectByCondition((cb, root) ->
                cb.equal(root.get("username"), "FirstName.LastName"), Trainee.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).hasSize(1);
        assertThat(actualResult.iterator().next()).isEqualTo(trainee);

        verify(traineeDao, times(1)).findByCondition(any(), any());
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method selectByCondition - should return empty collection if there are no trainees that satisfy condition")
    void testSelectByCondition_negative() {
        // given
        given(traineeDao.findByCondition(any(), any())).willReturn(Collections.emptyList());

        // when
        var actualResult = traineeService.selectByCondition((cb, root) ->
                cb.equal(root.get("username"), "NonExistingUsername"), Trainee.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(traineeDao, times(1)).findByCondition(any(), any());
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method selectByUsername - should return trainee by username")
    void testSelectByUsername_positive() {
        // given
        var trainee = createTestTrainee();
        trainee.setUsername("FirstName.LastName");

        given(traineeDao.findByUsername(anyString())).willReturn(Optional.of(trainee));

        // when
        var actualResult = traineeService.selectByUsername("FirstName.LastName");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainee);

        verify(traineeDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method selectByUsername - should return empty optional if there is no trainee with given username")
    void testSelectByUsername_negative() {
        // given
        given(traineeDao.findByUsername(anyString())).willReturn(Optional.empty());

        // when
        var actualResult = traineeService.selectByUsername("NonExistingUsername");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotPresent();

        verify(traineeDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method checkPasswordForUsername - should return true if password is correct for given username")
    void testCheckPasswordForUsername_positive() {
        // given
        var trainee = createTestTrainee();
        trainee.setUsername("FirstName.LastName");
        trainee.setPassword("password");

        given(traineeDao.findByUsername(anyString())).willReturn(Optional.of(trainee));

        // when
        var actualResult = traineeService.checkPasswordForUsername("FirstName.LastName", "password");

        // then
        assertThat(actualResult).isTrue();

        verify(traineeDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method checkPasswordForUsername - should return false if password is incorrect for given username")
    void testCheckPasswordForUsername_negative() {
        // given
        given(traineeDao.findByUsername(anyString())).willReturn(Optional.empty());

        // when
        var actualResult = traineeService.checkPasswordForUsername("FirstName.LastName", "wrongPassword");

        // then
        assertThat(actualResult).isFalse();

        verify(traineeDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method changePassword - should change password for given trainee")
    void testChangePassword_withValidPassword() {
        // given
        var trainee = createTestTrainee();
        trainee.setUsername("FirstName.LastName");
        trainee.setPassword("oldPassword");

        given(traineeDao.update(any(Trainee.class))).willReturn(trainee);

        // when
        traineeService.changePassword(trainee, "newPassword");

        // then
        assertThat(trainee.getPassword()).isEqualTo("newPassword");

        verify(traineeDao, times(1)).update(any(Trainee.class));
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method changePassword - should change password to generated one if given password is null")
    void testChangePassword_withNullPassword() {
        // given
        var trainee = createTestTrainee();
        trainee.setUsername("FirstName.LastName");
        trainee.setPassword("oldPassword");

        var generatedPassword = "generatedPassword";

        try (var staticMockPasswordGenerator = mockStatic(PasswordGenerator.class)) {
            staticMockPasswordGenerator.when(PasswordGenerator::generatePassword).thenReturn(generatedPassword);

            given(traineeDao.update(any(Trainee.class))).willReturn(trainee);

            // when
            traineeService.changePassword(trainee, null);

            // then
            assertThat(trainee.getPassword()).isEqualTo(generatedPassword);

            verify(traineeDao, times(1)).update(any(Trainee.class));
            verifyNoMoreInteractions(traineeDao);
        }
    }

    @Test
    @DisplayName("Test of the method changeStatus - should change status for given trainee")
    void testChangeStatus() {
        // given
        var trainee = createTestTrainee();
        trainee.setActive(true);

        given(traineeDao.update(any(Trainee.class))).willReturn(trainee);

        // when
        traineeService.changeStatus(trainee, false);

        // then
        assertThat(trainee.isActive()).isFalse();

        verify(traineeDao, times(1)).update(any(Trainee.class));
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method deleteByUsername - should delete trainee by username")
    void testDeleteByUsername() {
        // given
        doNothing().when(traineeDao).deleteByUsername(anyString());

        // when
        traineeService.deleteByUsername("FirstName.LastName");

        // then
        verify(traineeDao, times(1)).deleteByUsername(anyString());
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