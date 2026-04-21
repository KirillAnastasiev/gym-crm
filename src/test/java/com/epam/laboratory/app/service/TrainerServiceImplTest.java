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
import java.util.List;
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
    private PasswordGenerator passwordGenerator;

    @Mock
    private UsernameHelper usernameHelper;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    @DisplayName("Test of the method createTrainer - should create trainer with generated password")
    void testCreateTrainer_uniqueUsername() {
        // given
        var trainer = createTrainer();
        var password = "generatedPassword";
        var username = "FirstName.LastName";

        given(passwordGenerator.generatePassword()).willReturn(password);
        given(trainerDao.findByCondition(any(Predicate.class), any(Class.class))).willReturn(Collections.emptyList());
        given(usernameHelper.generateUsername(anyString(), anyString(), anyCollection())).willReturn(username);
        given(trainerDao.save(any(Trainer.class))).willReturn(trainer);

        // when
        var actualResult = trainerService.createTrainer(trainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(trainer);
        assertThat(actualResult.getPassword()).isEqualTo(password);
        assertThat(actualResult.getUsername()).isEqualTo(username);

        verify(passwordGenerator, times(1)).generatePassword();
        verify(trainerDao, times(1)).findByCondition(any(Predicate.class), any(Class.class));
        verify(usernameHelper, times(1)).generateUsername(anyString(), anyString(), anyCollection());
        verify(trainerDao, times(1)).save(any(Trainer.class));
        verifyNoMoreInteractions(passwordGenerator, usernameHelper, trainerDao);
    }

    @Test
    @DisplayName("Test of the method createTrainer - should create trainer with generated password and username with suffix if username is not unique")
    void testCreateTrainer_nonUniqueUsername() {
        // given
        var trainer = createTrainer();
        var password = "generatedPassword";
        var usernameWithSuffix = "FirstName.LastName.2";

        given(passwordGenerator.generatePassword()).willReturn(password);
        given(trainerDao.findByCondition(any(Predicate.class), any(Class.class))).willReturn(Collections.singletonList(trainer));
        given(usernameHelper.generateUsername(anyString(), anyString(), anyCollection())).willReturn(usernameWithSuffix);
        given(trainerDao.save(any(Trainer.class))).willReturn(trainer);

        // when
        var actualResult = trainerService.createTrainer(trainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(trainer);
        assertThat(actualResult.getPassword()).isEqualTo(password);
        assertThat(actualResult.getUsername()).isEqualTo(usernameWithSuffix);

        verify(passwordGenerator, times(1)).generatePassword();
        verify(trainerDao, times(1)).findByCondition(any(Predicate.class), any(Class.class));
        verify(usernameHelper, times(1)).generateUsername(anyString(), anyString(), anyCollection());
        verify(trainerDao, times(1)).save(any(Trainer.class));
        verifyNoMoreInteractions(passwordGenerator, usernameHelper, trainerDao);
    }

    @Test
    @DisplayName("Test of the method updateTrainer - should update trainer and return")
    void testUpdateTrainer() {
        // given
        var trainer = createTrainer();

        given(trainerDao.update(any(Trainer.class))).willReturn(trainer);

        // when
        var actualResult = trainerService.updateTrainer(trainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(trainer);

        verify(trainerDao, times(1)).update(any(Trainer.class));
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method deleteTrainer - should delete trainer")
    void testDeleteTrainer() {
        // given
        var trainer = createTrainer();

        doNothing().when(trainerDao).delete(any(Trainer.class));

        // when
        trainerService.deleteTrainer(trainer);

        // then
        verify(trainerDao, times(1)).delete(any(Trainer.class));
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    void testSelectTrainersByCondition_positive() {
        // given
        var trainer = createTrainer();
        trainer.setUsername("FirstName.LastName");

        given(trainerDao.findByCondition(any(Predicate.class), any(Class.class))).willReturn(Collections.singletonList(trainer));

        // when
        var actualResult = trainerService.selectTrainerByCondition(t -> "FirstName.LastName".equals(t.getUsername()));

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
    @DisplayName("Test of the method selectTrainersByCondition - should return empty collection if there are no trainers that match the condition")
    void testSelectTrainersByCondition_negative() {
        // given
        given(trainerDao.findByCondition(any(Predicate.class), any(Class.class))).willReturn(Collections.emptyList());

        // when
        var actualResult = trainerService.selectTrainerByCondition(t -> "FirstName.LastName".equals(t.getUsername()));

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
        trainer.setSpecialization(TrainingType.FITNESS);
        trainer.setActive(true);

        return trainer;
    }
}