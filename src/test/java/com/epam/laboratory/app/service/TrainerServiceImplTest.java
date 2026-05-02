package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.repository.TrainerDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Predicate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {
    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    @DisplayName("Test of the method create - should create trainer with generated password")
    void testCreate_uniqueUsername() {
        // given
        var trainer = createTrainer();
        var password = "generatedPassword";
        var username = "FirstName.LastName";

        try (var mockedStaticPasswordGenerator = mockStatic(PasswordGenerator.class);
                var mockedStaticUsernameHelper = mockStatic(UsernameHelper.class)) {
            mockedStaticPasswordGenerator.when(PasswordGenerator::generatePassword).thenReturn(password);
            mockedStaticUsernameHelper.when(() -> UsernameHelper.generateUsername(any(Trainer.class), any(Predicate.class))).thenReturn(username);

            given(trainerDao.save(any(Trainer.class))).willReturn(trainer);

            // when
            var actualResult = trainerService.create(trainer);

            // then
            assertThat(actualResult).isNotNull();
            assertThat(actualResult.getId()).isNotNull();
            assertThat(actualResult).isEqualTo(trainer);
            assertThat(actualResult.getPassword()).isEqualTo(password);
            assertThat(actualResult.getUsername()).isEqualTo(username);

            verify(trainerDao, times(1)).save(any(Trainer.class));
            verifyNoMoreInteractions(trainerDao);
        }
    }

    @Test
    @DisplayName("Test of the method create - should create trainer with generated password and username with suffix if username is not unique")
    void testCreate_nonUniqueUsername() {
        // given
        var trainer = createTrainer();
        var password = "generatedPassword";
        var usernameWithSuffix = "FirstName.LastName.2";

        try (var staticMockPasswordGenerator = mockStatic(PasswordGenerator.class);
                var mockedStaticUsernameHelper = mockStatic(UsernameHelper.class)) {
            staticMockPasswordGenerator.when(PasswordGenerator::generatePassword).thenReturn(password);
            mockedStaticUsernameHelper.when(() -> UsernameHelper.generateUsername(any(), any())).thenReturn(usernameWithSuffix);

            given(trainerDao.save(any(Trainer.class))).willReturn(trainer);

            // when
            var actualResult = trainerService.create(trainer);

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
    @DisplayName("Test of the method update - should update trainer with generated username")
    void testUpdate() {
        // given
        var trainer = createTrainer();
        trainer.setFirstName("UpdatedFirstName");
        var generatedUsername = "UpdatedFirstName.LastName";
        trainer.setUsername(generatedUsername);

        given(trainerDao.update(any(Trainer.class))).willReturn(trainer);

        // when
        var actualResult = trainerService.update(trainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(trainer);
        assertThat(actualResult.getUsername()).isEqualTo(generatedUsername);

        verify(trainerDao, times(1)).update(any(Trainer.class));
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method delete - should delete trainer")
    void testDelete() {
        // given
        var trainer = createTrainer();

        doNothing().when(trainerDao).delete(any(Trainer.class));

        // when
        trainerService.delete(trainer);

        // then
        verify(trainerDao, times(1)).delete(any(Trainer.class));
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method selectById - should return trainer if it exists")
    void testSelectById_positive() {
        // given
        var trainer = createTrainer();

        given(trainerDao.findById(anyLong(), any(Class.class))).willReturn(Optional.of(trainer));

        // when
        var actualResult = trainerService.selectById(1L, Trainer.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainer);

        verify(trainerDao, times(1)).findById(anyLong(), any(Class.class));
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method selectById - should return empty optional if trainer does not exist")
    void testSelectById_negative() {
        // given
        given(trainerDao.findById(anyLong(), any(Class.class))).willReturn(Optional.empty());

        // when
        var actualResult = trainerService.selectById(1L, Trainer.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(trainerDao, times(1)).findById(anyLong(), any(Class.class));
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method selectByCondition - should return collection of trainers that satisfy condition")
    void testSelectByCondition_positive() {
        // given
        var trainer = createTrainer();

        given(trainerDao.findByCondition(any(), any(Class.class))).willReturn(java.util.List.of(trainer));

        // when
        var actualResult = trainerService.selectByCondition((cb, root) ->
                cb.equal(root.get("username"), "FirstName.LastName"), Trainer.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(java.util.Collection.class);
        assertThat(actualResult).hasSize(1);
        assertThat(actualResult).contains(trainer);

        verify(trainerDao, times(1)).findByCondition(any(), any(Class.class));
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method selectByCondition - should return empty collection if there are no trainers that match the condition")
    void testSelectByCondition_negative() {
        // given
        given(trainerDao.findByCondition(any(), any(Class.class))).willReturn(java.util.List.of());

        // when
        var actualResult = trainerService.selectByCondition((cb, root) ->
                cb.equal(root.get("username"), "FirstName.LastName"), Trainer.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(java.util.Collection.class);
        assertThat(actualResult).isEmpty();

        verify(trainerDao, times(1)).findByCondition(any(), any(Class.class));
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method selectByUsername - should return trainer if it exists")
    void testSelectByUsername_positive() {
        // given
        var trainer = createTrainer();

        given(trainerDao.findByUsername(anyString())).willReturn(Optional.of(trainer));

        // when
        var actualResult = trainerService.selectByUsername("FirstName.LastName");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainer);

        verify(trainerDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method selectByUsername - should return empty optional if trainer does not exist")
    void testSelectByUsername_negative() {
        // given
        given(trainerDao.findByUsername(anyString())).willReturn(Optional.empty());

        // when
        var actualResult = trainerService.selectByUsername("FirstName.LastName");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(trainerDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method checkPasswordForUsername - should return true if password is correct for username")
    void testCheckPasswordForUsername_positive() {
        // given
        var trainer = createTrainer();
        trainer.setUsername("FirstName.LastName");
        trainer.setPassword("password");

        given(trainerDao.findByUsername(anyString())).willReturn(Optional.of(trainer));

        // when
        var actualResult = trainerService.checkPasswordForUsername("FirstName.LastName", "password");

        // then
        assertThat(actualResult).isTrue();

        verify(trainerDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method checkPasswordForUsername - should return false if password is incorrect for username")
    void testCheckPasswordForUsername_negative() {
        // given
        given(trainerDao.findByUsername(anyString())).willReturn(Optional.empty());

        // when
        var actualResult = trainerService.checkPasswordForUsername("FirstName.LastName", "wrongPassword");

        // then
        assertThat(actualResult).isFalse();

        verify(trainerDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method changePassword - should change password for username if new password is valid")
    void testChangePasswordForUsername_withValidPassword() {
        // given
        var trainer = createTrainer();
        trainer.setUsername("FirstName.LastName");
        trainer.setPassword("oldPassword");

        given(trainerDao.update(any(Trainer.class))).willReturn(trainer);

        // when
        trainerService.changePassword(trainer, "newPassword");

        // then
        assertThat(trainer.getPassword()).isEqualTo("newPassword");

        verify(trainerDao, times(1)).update(any(Trainer.class));
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method changePassword - should not change password for username if new password is empty")
    void testChangePasswordForUsername_withEmptyPassword() {
        // given
        var trainer = createTrainer();
        trainer.setUsername("FirstName.LastName");
        trainer.setPassword("oldPassword");

        var generatedPassword = "generatedPassword";

        try (var mockedStaticPasswordGenerator = mockStatic(PasswordGenerator.class)) {
            mockedStaticPasswordGenerator.when(PasswordGenerator::generatePassword).thenReturn(generatedPassword);

            given(trainerDao.update(any(Trainer.class))).willReturn(trainer);

            // when
            trainerService.changePassword(trainer, "");

            // then
            assertThat(trainer.getPassword()).isEqualTo(generatedPassword);

            verify(trainerDao, times(1)).update(any(Trainer.class));
            verifyNoMoreInteractions(trainerDao);
        }
    }

    @Test
    @DisplayName("Test of the method changeStatus - should change status of trainer")
    void testChangeStatus() {
        // given
        var trainer = createTrainer();
        trainer.setActive(true);

        given(trainerDao.update(any(Trainer.class))).willReturn(trainer);

        // when
        trainerService.changeStatus(trainer, false);

        // then
        assertThat(trainer.isActive()).isFalse();

        verify(trainerDao, times(1)).update(any(Trainer.class));
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method deleteByUsername - should delete trainer by username")
    void testDeleteByUsername() {
        // given
        doNothing().when(trainerDao).deleteByUsername(anyString());

        // when
        trainerService.deleteByUsername("FirstName.LastName");

        // then
        verify(trainerDao, times(1)).deleteByUsername(anyString());
        verifyNoMoreInteractions(trainerDao);
    }

    private Trainer createTrainer() {
        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("FirstName");
        trainer.setLastName("LastName");
        trainer.setSpecialization(new TrainingType());
        trainer.setActive(true);

        return trainer;
    }
}