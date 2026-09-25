package com.kirill.projects.gymcrm.app.service;

import com.kirill.projects.gymcrm.app.client.TrainingReportMessagingClient;
import com.kirill.projects.gymcrm.app.domain.Trainee;
import com.kirill.projects.gymcrm.app.domain.Trainer;
import com.kirill.projects.gymcrm.app.exception.NoSuchEntityException;
import com.kirill.projects.gymcrm.app.repository.TraineeDao;
import com.kirill.projects.gymcrm.app.service.security.AuthenticationService;
import com.kirill.projects.gymcrm.app.util.PasswordGenerator;
import com.kirill.projects.gymcrm.app.util.UsernameHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraineeServiceImpl test suite")
class TraineeServiceImplTest {
    @Mock
    private TraineeDao traineeDao;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainingReportMessagingClient trainingReportMessagingClient;

    @InjectMocks
    private TraineeServiceImpl traineeService;


    // ==================== REGISTER NEW TESTS ====================

    @Test
    @DisplayName("Test of the method registerNew - should create trainee with unique username and return updated trainee")
    void testRegisterNew_positive_uniqueUsername() {
        // given
        var trainee = createTestTrainee();
        var generatedPassword = "1234567890";
        var generatedUsername = "FirstName.LastName";
        var encodedPassword = "encodedPassword";

        try (var staticMockPasswordGenerator = mockStatic(PasswordGenerator.class);
             var staticMockUserHelper = mockStatic(UsernameHelper.class)) {
            staticMockPasswordGenerator.when(PasswordGenerator::generatePassword).thenReturn(generatedPassword);
            staticMockUserHelper.when(() -> UsernameHelper.generateUsername(any(), any())).thenReturn(generatedUsername);

            given(passwordEncoder.encode(generatedPassword)).willReturn(encodedPassword);
            given(traineeDao.save(any(Trainee.class))).willReturn(trainee);

            // when
            var actualResult = traineeService.registerNew(trainee);

            // then
            assertThat(actualResult).isNotNull();
            assertThat(actualResult.getPassword()).isEqualTo(generatedPassword);
            assertThat(actualResult.getUsername()).isEqualTo(generatedUsername);

            verify(passwordEncoder, times(1)).encode(generatedPassword);
            verify(traineeDao, times(1)).save(any(Trainee.class));
            verifyNoMoreInteractions(traineeDao);
        }
    }

    @Test
    @DisplayName("Test of the method registerNew - should create trainee with non-unique username and return")
    void testRegisterNew_positive_nonUniqueUsername() {
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
            assertThat(actualResult.getPassword()).isEqualTo(generatedPassword);
            assertThat(actualResult.getUsername()).isEqualTo(generatedUsernameWithSuffix);

            verify(traineeDao, times(1)).save(any(Trainee.class));
            verifyNoMoreInteractions(traineeDao);
        }
    }

    @Test
    @DisplayName("Test of the method registerNew - should throw exception if input is null")
    void testRegisterNew_negative_nullTrainee() {
        // when & then
        assertThatThrownBy(() -> traineeService.registerNew(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainee must not be null");

        verifyNoInteractions(traineeDao);
    }


    // ==================== UPDATE TESTS ====================

    @Test
    @DisplayName("Test of the method update - should update trainee with unique username and return updated trainee")
    void testUpdate_positive() {
        // given
        var trainee = createTestTrainee();
        trainee.setFirstName("UpdatedFirstName");

        given(traineeDao.save(any(Trainee.class))).willReturn(trainee);

        // when
        var actualResult = traineeService.update(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);

        verify(traineeDao, times(1)).save(any(Trainee.class));
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method update - should throw exception if input is null")
    void testUpdate_negative_nullTrainee() {
        // when & then
        assertThatThrownBy(() -> traineeService.update(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainee must not be null");

        verifyNoInteractions(traineeDao);
    }


    // ==================== UPDATE BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method updateByUsername - should update trainee by username and return updated trainee")
    void testUpdateByUsername_positive() {
        // given
        var trainee = createTestTrainee();
        var username = "FirstName.LastName";
        trainee.setFirstName("UpdatedFirstName");
        trainee.setUsername(username);
        trainee.addTrainers(Collections.emptyList());

        given(authenticationService.checkExistsByUsername(anyString())).willReturn(true);
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

        verify(authenticationService, times(1)).checkExistsByUsername(anyString());
        verify(traineeDao, times(1)).updateByUsername(anyString(), any(Trainee.class));
        verifyNoMoreInteractions(authenticationService, traineeDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, Trainee username must not be null",
            "'', Trainee username must not be blank",
            "'   ', Trainee username must not be blank",
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
    @DisplayName("Test of the method updateByUsername - should throw exception if trainee is null")
    void testUpdateByUsername_negative_nullTrainee() {
        // when & then
        assertThatThrownBy(() -> traineeService.updateByUsername("FirstName.LastName", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainee must not be null");

        verifyNoInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method updateByUsername - should throw exception if there is no trainee with given username")
    void testUpdateByUsername_negative_notExistingUsername() {
        // given
        var username = "NonExistingUsername";
        var trainee = createTestTrainee();

        given(authenticationService.checkExistsByUsername(anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> traineeService.updateByUsername(username, trainee))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("Trainee with username NonExistingUsername not found");

        verify(authenticationService, times(1)).checkExistsByUsername(anyString());
        verifyNoMoreInteractions(authenticationService);
        verifyNoInteractions(traineeDao);
    }


    // ==================== SELECT BY CONDITION TESTS ====================

    @Test
    @DisplayName("Test of the method selectByCondition - should return collection of trainees that satisfy condition")
    void testSelectByCondition_positive() {
        // given
        var trainee = createTestTrainee();
        trainee.setUsername("FirstName.LastName");

        given(traineeDao.findByCondition(any())).willReturn(Collections.singletonList(trainee));

        // when
        var actualResult = traineeService.selectByCondition((cb, root) ->
                cb.equal(root.get("username"), "FirstName.LastName"));

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).hasSize(1);
        assertThat(actualResult.iterator().next()).isEqualTo(trainee);

        verify(traineeDao, times(1)).findByCondition(any());
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method selectByCondition - should return empty collection if there are no trainees that satisfy condition")
    void testSelectByCondition_negative() {
        // given
        given(traineeDao.findByCondition(any())).willReturn(Collections.emptyList());

        // when
        var actualResult = traineeService.selectByCondition((cb, root) ->
                cb.equal(root.get("username"), "NonExistingUsername"));

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(traineeDao, times(1)).findByCondition(any());
        verifyNoMoreInteractions(traineeDao);
    }


    // ==================== COUNT BY CONDITION TESTS ====================

    @Test
    @DisplayName("Test of the method countByCondition - should return count of trainees that satisfy condition")
    void testCountByCondition_positive() {
        // given
        given(traineeDao.countByCondition(any())).willReturn(5L);

        // when
        var actualResult = traineeService.countByCondition(UserService.byStatus(true));

        // then
        assertThat(actualResult).isEqualTo(5L);

        verify(traineeDao, times(1)).countByCondition(any());
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method countByCondition - should return zero if there are no trainees that satisfy condition")
    void testCountByCondition_negative() {
        // given
        given(traineeDao.countByCondition(any())).willReturn(0L);

        // when
        var actualResult = traineeService.countByCondition(TraineeService.byUsernames("NonExistingUsername"));

        // then
        assertThat(actualResult).isEqualTo(0L);

        verify(traineeDao, times(1)).countByCondition(any());
        verifyNoMoreInteractions(traineeDao);
    }


    // ==================== SELECT BY USERNAME TESTS ====================

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
    @CsvSource(value = {
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


    // ==================== DELETE BY USERNAME TESTS ====================

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


    // ==================== CHANGE STATUS TESTS ====================

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


    // ==================== UPDATE TRAINERS TESTS ====================

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
        given(traineeDao.save(any(Trainee.class))).willReturn(trainee);

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
        verify(traineeDao, times(1)).save(any(Trainee.class));
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

    private static Trainee createTestTrainee() {
        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");
        trainee.setDateOfBirth(LocalDate.now());
        trainee.setActive(true);

        return trainee;
    }

}