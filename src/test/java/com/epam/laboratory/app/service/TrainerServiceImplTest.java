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

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {
    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UsernameHelper usernameHelper;

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

        try (var mockedStaticPasswordGenerator = mockStatic(PasswordGenerator.class)) {
            mockedStaticPasswordGenerator.when(PasswordGenerator::generatePassword).thenReturn(password);

            try (var mockedStaticUsernameHelper = mockStatic(UsernameHelper.class)) {
                mockedStaticUsernameHelper.when(() -> UsernameHelper.generateUsername(any(Trainer.class), any(Predicate.class))).thenReturn(usernameWithSuffix);

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
    }

    @Test
    @DisplayName("Test of the method update - should update trainer with generated username")
    void testUpdate_uniqueUsername() {
        // given
        var trainer = createTrainer();
        trainer.setFirstName("UpdatedFirstName");
        var generatedUsername = "UpdatedFirstName.LastName";

        try (var mockedStaticUsernameHelper = mockStatic(UsernameHelper.class)) {
            mockedStaticUsernameHelper.when(() -> UsernameHelper.generateUsername(any(Trainer.class), any(Predicate.class))).thenReturn(generatedUsername);

            given(usernameHelper.generateUsername(any(Trainer.class), any(Predicate.class))).willReturn(generatedUsername);
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
    }

    @Test
    @DisplayName("Test of the method update - should update trainer with generated username with suffix if username is not unique")
    void testUpdate_nonUniqueUsername() {
        // given
        var trainer = createTrainer();
        trainer.setFirstName("UpdatedFirstName");
        var generatedUsernameWithSuffix = "UpdatedFirstName.LastName2";

        try (var mockedStaticUsernameHelper = mockStatic(UsernameHelper.class)) {
            mockedStaticUsernameHelper.when(() -> UsernameHelper.generateUsername(any(Trainer.class), any(Predicate.class))).thenReturn(generatedUsernameWithSuffix);

            given(trainerDao.update(any(Trainer.class))).willReturn(trainer);

            // when
            var actualResult = trainerService.update(trainer);

            // then
            assertThat(actualResult).isNotNull();
            assertThat(actualResult.getId()).isNotNull();
            assertThat(actualResult).isEqualTo(trainer);
            assertThat(actualResult.getUsername()).isEqualTo(generatedUsernameWithSuffix);

            verify(trainerDao, times(1)).update(any(Trainer.class));
            verifyNoMoreInteractions(trainerDao);
        }
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
    @DisplayName("Test of the method selectByCondition - should return collection of trainers that satisfy condition")
    void testSelectByCondition_positive() {
        // given
        var trainer = createTrainer();
        trainer.setUsername("FirstName.LastName");

        given(trainerDao.findByCondition(any(Predicate.class), any(Class.class))).willReturn(Collections.singletonList(trainer));

        // when
        var actualResult = trainerService.selectByCondition(t -> "FirstName.LastName".equals(t.getUsername()), Trainer.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).hasSize(1);
        actualResult.forEach(t -> {
            assertThat(t).isInstanceOf(Trainer.class);
            assertThat(t.getUsername()).isEqualTo("FirstName.LastName");
        });

        verify(trainerDao, times(1)).findByCondition(any(Predicate.class), any(Class.class));
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method selectByCondition - should return empty collection if there are no trainers that match the condition")
    void testSelectByCondition_negative() {
        // given
        given(trainerDao.findByCondition(any(Predicate.class), any(Class.class))).willReturn(Collections.emptyList());

        // when
        var actualResult = trainerService.selectByCondition(t -> "FirstName.LastName".equals(t.getUsername()), Trainer.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(trainerDao, times(1)).findByCondition(any(Predicate.class), any(Class.class));
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