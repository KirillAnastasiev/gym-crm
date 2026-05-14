package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
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

    @Mock
    private TrainerService trainerService;

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
    @DisplayName("Test of the method updateByUsername - should update trainee by username and return updated trainee")
    void testUpdateByUsername_positive() {
        // given
        var trainee = createTestTrainee();
        var username = "FirstName.LastName";
        trainee.setFirstName("UpdatedFirstName");
        trainee.setUsername(username);
        trainee.addTrainers(Collections.emptyList());

        given(traineeDao.updateByUsername(eq(username), any(Trainee.class))).willReturn(trainee);

        // when
        var actualResult = traineeService.updateByUsername(username, trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);
        assertThat(actualResult.getUsername()).isEqualTo(username);
        assertThat(actualResult.getTrainers()).isNotNull();
        assertThat(actualResult.getTrainers()).isInstanceOf(Collection.class);
        assertThat(actualResult.getTrainers()).isEmpty();

        verify(traineeDao, times(1)).updateByUsername(anyString(), any(Trainee.class));
        verifyNoMoreInteractions(traineeDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "NULL, Trainee username must not be null",
        "'', Trainee username must not be blank",
        "'   ', Trainee username must not be blank"
    }, nullValues = {"NULL"})
    @DisplayName("Test of the method updateByUsername - should throw exception if input is invalid")
    void testUpdateByUsername_negative_invalidInput(String username, String expectedMessage) {
        // when & then
        assertThatThrownBy(() -> traineeService.updateByUsername(username, createTestTrainee()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessage);

        verifyNoInteractions(traineeDao);
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
        "NULL, Trainee username must not be null",
        "'', Trainee username must not be blank",
        "'   ', Trainee username must not be blank"
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
        "NULL, Trainee username must not be null",
        "'', Trainee username must not be blank",
        "'   ', Trainee username must not be blank"
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
    @DisplayName("Test of the method changeStatusByUsername - should change trainee status")
    void testChangeStatus_positive() {
        // given
        var username = "FirstName.LastName";
        var isActive = false;

        given(authenticationService.checkExistsByUsername(anyString())).willReturn(true);
        doNothing().when(traineeDao).changeStatusByUsername(anyString(), anyBoolean());

        // when
        traineeService.changeStatus(username, isActive);

        // then
        verify(authenticationService, times(1)).checkExistsByUsername(anyString());
        verify(traineeDao, times(1)).changeStatusByUsername(anyString(), anyBoolean());
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

    @Test
    @DisplayName("Test of the method updateTrainers - should update trainee's trainers and return updated collection of trainers")
    void testUpdateTrainers_positive() {
        // given
        var trainee = createTestTrainee();
        trainee.setUsername("FirstName.LastName");

        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("TrainerFirstName");
        trainer.setLastName("TrainerLastName");
        trainer.setUsername("TrainerFirstName.TrainerLastName");
        trainer.setActive(true);

        given(traineeDao.findByUsername(anyString())).willReturn(Optional.of(trainee));
        given(trainerService.selectByUsername(anyString())).willReturn(trainer);
        given(traineeDao.update(any(Trainee.class))).willReturn(trainee);

        // when
        var actualResult = traineeService.updateTrainers("FirstName.LastName", Collections.singletonList(trainer));

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        actualResult.forEach(t -> {
            assertThat(t).isNotNull();
            assertThat(t).isInstanceOf(Trainer.class);
            assertThat(t).isEqualTo(trainer);
        });

        verify(traineeDao, times(1)).findByUsername(anyString());
        verify(trainerService, times(1)).selectByUsername(anyString());
        verify(traineeDao, times(1)).update(any(Trainee.class));
        verifyNoMoreInteractions(traineeDao, trainerService);
    }

    @Test
    @DisplayName("Test of the method updateTrainers - should throw exception if there is no trainee with given username")
    void testUpdateTrainers_negative_notExistingTrainee() {
        // given
        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("TrainerFirstName");
        trainer.setLastName("TrainerLastName");
        trainer.setUsername("TrainerFirstName.TrainerLastName");
        trainer.setActive(true);

        given(traineeDao.findByUsername(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> traineeService.updateTrainers("NonExistingUsername", Collections.singletonList(trainer)))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("Trainee with username NonExistingUsername not found");

        verify(traineeDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(traineeDao);
        verifyNoInteractions(trainerService);
    }

    @Test
    @DisplayName("Test of the method updateTrainers - should throw exception if there is no trainer with given username in the list of trainers")
    void testUpdateTrainers_negative_notExistingTrainer() {
        // given
        var trainee = createTestTrainee();
        trainee.setUsername("FirstName.LastName");

        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("TrainerFirstName");
        trainer.setLastName("TrainerLastName");
        trainer.setUsername("TrainerFirstName.TrainerLastName");
        trainer.setActive(true);

        given(traineeDao.findByUsername(anyString())).willReturn(Optional.of(trainee));
        given(trainerService.selectByUsername(anyString())).willThrow(new NoSuchEntityException("Trainer with username TrainerFirstName.TrainerLastName not found"));

        // when & then
        assertThatThrownBy(() -> traineeService.updateTrainers("FirstName.LastName", Collections.singletonList(trainer)))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("Trainer with username TrainerFirstName.TrainerLastName not found");

        verify(traineeDao, times(1)).findByUsername(anyString());
        verify(trainerService, times(1)).selectByUsername(anyString());
        verifyNoMoreInteractions(traineeDao, trainerService);
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