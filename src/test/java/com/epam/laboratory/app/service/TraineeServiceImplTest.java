package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.repository.TraineeDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
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
    private AuthenticationService authenticationService;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    @DisplayName("Test of the method registerNew - should create trainee with unique username and return updated trainee")
    void testRegisterNew_uniqueUsername() {
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
            var actualResult = traineeService.registerNew(trainee);

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
    @DisplayName("Test of the method registerNew - should create trainee with non-unique username and return")
    void testRegisterNew_nonUniqueUsername() {
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
            var actualResult = traineeService.registerNew(trainee);

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
    @DisplayName("Test of the method update - should update trainee with unique username and return updated trainee")
    void testUpdate_positive() {
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
    @DisplayName("Test of the method update - should throw exception if input is null")
    void testUpdate_negative_nullTrainee() {
        // when & then
        assertThatThrownBy(() -> traineeService.update(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Entity must not be null");

        verifyNoInteractions(traineeDao);
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
        assertThat(actualResult).isEmpty();

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
        assertThat(actualResult).isEqualTo(trainee);

        verify(traineeDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method selectByUsername - should throw exception if there is no trainee with given username")
    void testSelectByUsername_negative_notExistingUsername() {
        // given
        var username = "NonExistingUsername";

        given(traineeDao.findByUsername(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> traineeService.selectByUsername(username))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("Trainee with username " + username + " not found");

        verify(traineeDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(traineeDao);
    }

    @ParameterizedTest
    @CsvSource(value ={
        "NULL, Username must not be null",
        "'', Username must not be blank",
        "'   ', Username must not be blank"
    }, nullValues = {"NULL"})
    @DisplayName("Test of the method selectByUsername - should throw exception if input is invalid")
    void testSelectByUsername_negative_invalidInput(String username, String expectedMessage) {
        // when & then
        assertThatThrownBy(() -> traineeService.selectByUsername(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessage);

        verifyNoInteractions(traineeDao);
    }



    @Test
    @DisplayName("Test of the method deleteByUsername - should delete trainee by username")
    void testDeleteByUsername_positive() {
        // given
        given(authenticationService.checkExistsByUsername(anyString())).willReturn(true);
        doNothing().when(traineeDao).deleteByUsername(anyString());

        // when
        traineeService.deleteByUsername("FirstName.LastName");

        // then
        verify(authenticationService, times(1)).checkExistsByUsername(anyString());
        verify(traineeDao, times(1)).deleteByUsername(anyString());
        verifyNoMoreInteractions(authenticationService, traineeDao);
    }

    @Test
    @DisplayName("Test of the method deleteByUsername - should throw exception if there is no trainee with given username")
    void testDeleteByUsername_negative_notExistingUsername() {
        // given
        var username = "NonExistingUsername";

        given(authenticationService.checkExistsByUsername(anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> traineeService.deleteByUsername(username))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("Trainee with username " + username + " not found");

        verify(authenticationService, times(1)).checkExistsByUsername(anyString());
        verifyNoMoreInteractions(authenticationService);
        verifyNoInteractions(traineeDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "NULL, Username must not be null",
        "'', Username must not be blank",
        "'   ', Username must not be blank"
    }, nullValues = {"NULL"})
    @DisplayName("Test of the method deleteByUsername - should throw exception if input is invalid")
    void testDeleteByUsername_negative_invalidInput(String username, String expectedMessage) {
        // when & then
        assertThatThrownBy(() -> traineeService.deleteByUsername(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessage);

        verifyNoInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method changeStatus - should change trainee status")
    void testChangeStatus_positive() {
        // given
        var username = "FirstName.LastName";
        var isActive = false;

        given(authenticationService.checkExistsByUsername(anyString())).willReturn(true);
        doNothing().when(traineeDao).changeStatus(anyString(), anyBoolean());

        // when
        traineeService.changeStatus(username, isActive);

        // then
        verify(authenticationService, times(1)).checkExistsByUsername(anyString());
        verify(traineeDao, times(1)).changeStatus(anyString(), anyBoolean());
        verifyNoMoreInteractions(authenticationService, traineeDao);
    }

    @Test
    @DisplayName("Test of the method changeStatus - should throw exception if there is no trainee with given username")
    void testChangeStatus_negative_notExistingUsername() {
        // given
        var username = "NonExistingUsername";
        var isActive = false;

        given(authenticationService.checkExistsByUsername(anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> traineeService.changeStatus(username, isActive))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("Trainee with username " + username + " not found");

        verify(authenticationService, times(1)).checkExistsByUsername(anyString());
        verifyNoMoreInteractions(authenticationService);
        verifyNoInteractions(traineeDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "NULL, Username must not be null",
        "'', Username must not be blank",
        "'   ', Username must not be blank"
    }, nullValues = {"NULL"})
    @DisplayName("Test of the method changeStatus - should throw exception if input is invalid")
    void testChangeStatus_negative_invalidInput(String username, String expectedMessage) {
        // given
        given(authenticationService.checkExistsByUsername(username)).willThrow(new IllegalArgumentException(expectedMessage));

        // when & then
        assertThatThrownBy(() -> traineeService.changeStatus(username, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessage);

        verify(authenticationService, times(1)).checkExistsByUsername(username);
        verifyNoMoreInteractions(authenticationService);
        verifyNoInteractions(traineeDao);
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