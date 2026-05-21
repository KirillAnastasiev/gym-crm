package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.repository.TrainerDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerServiceImpl test suite")
class TrainerServiceImplTest {
    @Mock
    private TrainerDao trainerDao;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TrainerServiceImpl trainerService;


    // ==================== REGISTER NEW TESTS ====================

    @Test
    @DisplayName("Test of the method registerNew - should create trainer with generated password")
    void testRegisterNew_positive_uniqueUsername() {
        // given
        var trainer = createTestTrainer();
        var password = "generatedPassword";
        var username = "FirstName.LastName";

        try (var mockedStaticPasswordGenerator = mockStatic(PasswordGenerator.class);
                var mockedStaticUsernameHelper = mockStatic(UsernameHelper.class)) {
            mockedStaticPasswordGenerator.when(PasswordGenerator::generatePassword).thenReturn(password);
            mockedStaticUsernameHelper.when(() -> UsernameHelper.generateUsername(any(), any())).thenReturn(username);

            given(trainerDao.save(any(Trainer.class))).willReturn(trainer);

            // when
            var actualResult = trainerService.registerNew(trainer);

            // then
            assertThat(actualResult).isNotNull();
            assertThat(actualResult.getId()).isNotNull();
            assertThat(actualResult).isEqualTo(trainer);
            assertThat(actualResult.getPassword()).isEqualTo(password);
            assertThat(actualResult.getUsername()).isEqualTo(username);

            verify(trainerDao, times(1)).save(any());
            verifyNoMoreInteractions(trainerDao);
        }
    }

    @Test
    @DisplayName("Test of the method registerNew - should create trainer with generated password and username with suffix if username is not unique")
    void testRegisterNew_positive_nonUniqueUsername() {
        // given
        var trainer = createTestTrainer();
        var password = "generatedPassword";
        var usernameWithSuffix = "FirstName.LastName.2";

        try (var staticMockPasswordGenerator = mockStatic(PasswordGenerator.class);
                var mockedStaticUsernameHelper = mockStatic(UsernameHelper.class)) {
            staticMockPasswordGenerator.when(PasswordGenerator::generatePassword).thenReturn(password);
            mockedStaticUsernameHelper.when(() -> UsernameHelper.generateUsername(any(), any())).thenReturn(usernameWithSuffix);

            given(trainerDao.save(any(Trainer.class))).willReturn(trainer);

            // when
            var actualResult = trainerService.registerNew(trainer);

            // then
            assertThat(actualResult).isNotNull();
            assertThat(actualResult.getId()).isNotNull();
            assertThat(actualResult).isEqualTo(trainer);
            assertThat(actualResult.getPassword()).isEqualTo(password);
            assertThat(actualResult.getUsername()).isEqualTo(usernameWithSuffix);

            verify(trainerDao, times(1)).save(any(Trainer.class));
            verifyNoMoreInteractions(trainerDao);
        }
    }

    @Test
    @DisplayName("Test of the method registerNew - should throw exception if input is null")
    void testRegisterNew_negative_nullTrainer() {
        // when & then
        assertThatThrownBy(() -> trainerService.registerNew(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer must not be null");

        verifyNoInteractions(trainerDao);
    }


    // ==================== UPDATE TESTS ====================

    @Test
    @DisplayName("Test of the method update - should update trainer with unique username and return updated trainer")
    void testUpdate_positive() {
        // given
        var trainer = createTestTrainer();
        trainer.setFirstName("UpdatedFirstName");

        given(trainerDao.save(any(Trainer.class))).willReturn(trainer);

        // when
        var actualResult = trainerService.update(trainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainer);

        verify(trainerDao, times(1)).save(any(Trainer.class));
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method update - should throw exception if input is null")
    void testUpdate_negative_nullTrainer() {
        // when & then
        assertThatThrownBy(() -> trainerService.update(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer must not be null");

        verifyNoInteractions(trainerDao);
    }


    // ==================== UPDATE BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method updateByUsername - should update trainer with unique username and return updated trainer")
    void testUpdateByUsername_positive() {
        // given
        var trainer = createTestTrainer();
        var username = "FirstName.LastName";
        trainer.setFirstName("UpdatedFirstName");
        trainer.setUsername(username);
        trainer.addTrainees(Collections.emptyList());

        given(authenticationService.checkExistsByUsername(anyString())).willReturn(true);
        given(trainerDao.updateByUsername(eq(username), any(Trainer.class))).willReturn(trainer);

        // when
        var actualResult = trainerService.updateByUsername(username, trainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainer);
        assertThat(actualResult.getUsername()).isEqualTo(username);
        assertThat(actualResult.getTrainees()).isNotNull();
        assertThat(actualResult.getTrainees()).isInstanceOf(Collection.class);
        assertThat(actualResult.getTrainees()).isEmpty();

        verify(authenticationService, times(1)).checkExistsByUsername(anyString());
        verify(trainerDao, times(1)).updateByUsername(anyString(), any(Trainer.class));
        verifyNoMoreInteractions(authenticationService, trainerDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, Trainer username must not be null",
            "'', Trainer username must not be blank",
            "'   ', Trainer username must not be blank"
    }, nullValues = "NULL")
    @DisplayName("Test of the method updateByUsername - should throw exception if input is invalid")
    void testUpdateByUsername_negative_invalidInput(String username, String errorMessage) {
        // when & then
        assertThatThrownBy(() -> trainerService.updateByUsername(username, createTestTrainer()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(errorMessage);

        verifyNoInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method updateByUsername - should throw exception if input trainer is null")
    void testUpdateByUsername_negative_nullTrainer() {
        // when & then
        assertThatThrownBy(() -> trainerService.updateByUsername("FirstName.LastName", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer must not be null");

        verifyNoInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method updateByUsername - should throw exception if trainer with given username does not exist")
    void testUpdateByUsername_negative_nonExistentTrainer() {
        // given
        var username = "NonExistentUsername";
        var trainer = createTestTrainer();

        given(authenticationService.checkExistsByUsername(anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> trainerService.updateByUsername(username, trainer))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("Trainer with username NonExistentUsername not found");

        verify(authenticationService, times(1)).checkExistsByUsername(anyString());
        verifyNoMoreInteractions(authenticationService);
        verifyNoInteractions(trainerDao);
    }


    // ==================== SELECT BY CONDITION TESTS ====================

    @Test
    @DisplayName("Test of the method selectByCondition - should return collection of trainers that satisfy condition")
    void testSelectByCondition_positive() {
        // given
        var trainer = createTestTrainer();

        given(trainerDao.findByCondition(any())).willReturn(java.util.List.of(trainer));

        // when
        var actualResult = trainerService.selectByCondition((cb, root) ->
                cb.equal(root.get("username"), "FirstName.LastName"), Trainer.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(java.util.Collection.class);
        assertThat(actualResult).hasSize(1);
        assertThat(actualResult).contains(trainer);

        verify(trainerDao, times(1)).findByCondition(any());
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method selectByCondition - should return empty collection if there are no trainers that match the condition")
    void testSelectByCondition_negative() {
        // given
        given(trainerDao.findByCondition(any())).willReturn(Collections.emptyList());

        // when
        var actualResult = trainerService.selectByCondition((cb, root) ->
                cb.equal(root.get("username"), "FirstName.LastName"), Trainer.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(java.util.Collection.class);
        assertThat(actualResult).isEmpty();

        verify(trainerDao, times(1)).findByCondition(any());
        verifyNoMoreInteractions(trainerDao);
    }


    // ==================== SELECT BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method selectByUsername - should return trainer if it exists")
    void testSelectByUsername_positive() {
        // given
        var trainer = createTestTrainer();

        given(trainerDao.findByUsername(anyString())).willReturn(Optional.of(trainer));

        // when
        var actualResult = trainerService.selectByUsername("FirstName.LastName");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainer);

        verify(trainerDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method selectByUsername - should throw exception if trainer with given username does not exist")
    void testSelectByUsername_negative_nonExistentUsername() {
        // given
        var username = "NonExistentUsername";

        given(trainerDao.findByUsername(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> trainerService.selectByUsername(username))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("Trainer with username " + username + " not found");

        verify(trainerDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(trainerDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, Trainer username must not be null",
            "'', Trainer username must not be blank",
            "'   ', Trainer username must not be blank"
    }, nullValues = "NULL")
    @DisplayName("Test of the method selectByUsername - should throw exception if input is invalid")
    void testSelectByUsername_negative_invalidInput(String username, String expectedMessage) {
        // when & then
        assertThatThrownBy(() -> trainerService.selectByUsername(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessage);
    }


    // ==================== DELETE BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method deleteByUsername - should delete trainer by username")
    void testDeleteByUsername_positive() {
        // given
        given(authenticationService.checkExistsByUsername(anyString())).willReturn(true);
        doNothing().when(trainerDao).deleteByUsername(anyString());

        // when
        trainerService.deleteByUsername("FirstName.LastName");

        // then
        verify(authenticationService, times(1)).checkExistsByUsername(anyString());
        verify(trainerDao, times(1)).deleteByUsername(anyString());
        verifyNoMoreInteractions(authenticationService, trainerDao);
    }

    @Test
    @DisplayName("Test of the method deleteByUsername - should throw exception if trainer with given username does not exist")
    void testDeleteByUsername_negative_nonExistentUsername() {
        // given
        var username = "NonExistentUsername";

        given(authenticationService.checkExistsByUsername(anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> trainerService.deleteByUsername(username))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("Trainer with username " + username + " not found");

        verify(authenticationService, times(1)).checkExistsByUsername(anyString());
        verifyNoMoreInteractions(authenticationService);
        verifyNoInteractions(trainerDao);
    }


    @ParameterizedTest
    @CsvSource(value = {
            "NULL, Trainer username must not be null",
            "'', Trainer username must not be blank",
            "'   ', Trainer username must not be blank"
    }, nullValues = "NULL")
    @DisplayName("Test of the method deleteByUsername - should throw exception if input is invalid")
    void testDeleteByUsername_negative_invalidInput(String username, String expectedMessage) {
        // when & then
        assertThatThrownBy(() -> trainerService.deleteByUsername(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessage);

        verifyNoInteractions(trainerDao);
    }


    // ==================== CHANGE STATUS TESTS ====================

    @Test
    @DisplayName("Test of the method changeStatus - should change status of trainer")
    void testChangeStatus_positive() {
        // given
        var username = "FirstName.LastName";

        given(authenticationService.checkExistsByUsername(anyString())).willReturn(true);
        doNothing().when(trainerDao).changeStatusByUsername(anyString(), anyBoolean());

        // when
        trainerService.changeStatus(username, false);

        // then
        verify(authenticationService, times(1)).checkExistsByUsername(anyString());
        verify(trainerDao, times(1)).changeStatusByUsername(anyString(), anyBoolean());
        verifyNoMoreInteractions(authenticationService, trainerDao);
    }

    @Test
    @DisplayName("Test of the method changeStatus - should throw exception if trainer with given username does not exist")
    void testChangeStatus_negative_nonExistentUsername() {
        // given
        var username = "NonExistentUsername";

        given(authenticationService.checkExistsByUsername(anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> trainerService.changeStatus(username, false))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("Trainer with username " + username + " not found");

        verify(authenticationService, times(1)).checkExistsByUsername(anyString());
        verifyNoMoreInteractions(authenticationService);
        verifyNoInteractions(trainerDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, Username must not be null",
            "'', Username must not be blank",
            "'   ', Username must not be blank"
    }, nullValues = "NULL")
    @DisplayName("Test of the method changeStatus - should throw exception if input is invalid")
    void testChangeStatus_negative_invalidInput(String username, String expectedMessage) {
        // given
        given(authenticationService.checkExistsByUsername(username)).willThrow(new IllegalArgumentException(expectedMessage));

        // when & then
        assertThatThrownBy(() -> trainerService.changeStatus(username, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessage);

        verify(authenticationService, times(1)).checkExistsByUsername(username);
        verifyNoMoreInteractions(authenticationService);
        verifyNoInteractions(trainerDao);
    }


    // ==================== UPDATE TRAINERS TESTS ====================

    @Test
    @DisplayName("Test of the method updateTrainees - should update trainer's trainees and return updated collection of trainees")
    void testUpdateTrainees_positive() {
        // given
        var trainer = createTestTrainer();
        trainer.setUsername("FirstName.LastName");

        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("TraineeFirstName");
        trainee.setLastName("TraineeLastName");
        trainee.setUsername("TraineeFirstName.TraineeLastName");
        trainee.setActive(true);

        given(trainerDao.findByUsername(anyString())).willReturn(Optional.of(trainer));
        given(traineeService.selectByUsername(anyString())).willReturn(trainee);
        given(trainerDao.save(any(Trainer.class))).willReturn(trainer);

        // when
        var actualResult = trainerService.updateTrainees("FirstName.LastName", Collections.singletonList(trainee));

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        actualResult.forEach(t -> {
            assertThat(t).isNotNull();
            assertThat(t).isInstanceOf(Trainee.class);
            assertThat(t).isEqualTo(trainee);
        });

        verify(trainerDao, times(1)).findByUsername(anyString());
        verify(traineeService, times(1)).selectByUsername(anyString());
        verify(trainerDao, times(1)).save(any(Trainer.class));
        verifyNoMoreInteractions(trainerDao, traineeService);
    }

    @Test
    @DisplayName("Test of the method updateTrainees - should throw exception if trainer with given username does not exist")
    void testUpdateTrainees_negative_nonExistentTrainer() {
        // given
        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("TraineeFirstName");
        trainee.setLastName("TraineeLastName");
        trainee.setUsername("TraineeFirstName.TraineeLastName");
        trainee.setActive(true);

        given(trainerDao.findByUsername(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> trainerService.updateTrainees("NonExistentUsername", Collections.emptyList()))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("Trainer with username NonExistentUsername not found");

        verify(trainerDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(trainerDao);
        verifyNoInteractions(traineeService);
    }

    @Test
    @DisplayName("Test of the method updateTrainees - should throw exception if trainee with given username does not exist")
    void testUpdateTrainees_negative_nonExistentTrainee() {
        // given
        var trainer = createTestTrainer();
        trainer.setUsername("FirstName.LastName");

        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("TraineeFirstName");
        trainee.setLastName("TraineeLastName");
        trainee.setUsername("TraineeFirstName.TraineeLastName");
        trainee.setActive(true);

        given(trainerDao.findByUsername(anyString())).willReturn(Optional.of(trainer));
        given(traineeService.selectByUsername(anyString())).willThrow(new NoSuchEntityException("Trainee with username TraineeFirstName.TraineeLastName not found"));

        // when & then
        assertThatThrownBy(() -> trainerService.updateTrainees("FirstName.LastName", Collections.singletonList(trainee)))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("Trainee with username TraineeFirstName.TraineeLastName not found");

        verify(trainerDao, times(1)).findByUsername(anyString());
        verify(traineeService, times(1)).selectByUsername(anyString());
        verifyNoMoreInteractions(trainerDao, traineeService);
    }

    private static Trainer createTestTrainer() {
        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("FirstName");
        trainer.setLastName("LastName");
        trainer.setSpecialization(new TrainingType());
        trainer.setActive(true);

        return trainer;
    }

}