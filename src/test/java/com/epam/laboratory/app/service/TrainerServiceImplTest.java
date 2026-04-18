package com.epam.laboratory.app.service;


import com.epam.laboratory.app.repository.TrainerDao;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.TrainingType;
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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
        given(usernameHelper.generateUsername(any(Trainer.class))).willReturn(username);
        given(trainerDao.existsByUsername(anyString())).willReturn(false);
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
        verify(usernameHelper, times(1)).generateUsername(any(Trainer.class));
        verify(trainerDao, times(1)).existsByUsername(anyString());
        verify(trainerDao, times(1)).save(any(Trainer.class));
        verifyNoMoreInteractions(passwordGenerator, usernameHelper, trainerDao);
    }

    @Test
    @DisplayName("Test of the method createTrainer - should create trainer with generated password and username with suffix if username is not unique")
    void testCreateTrainer_nonUniqueUsername() {
        // given
        var trainer = createTrainer();
        var password = "generatedPassword";
        var username = "FirstName.LastName";
        var usernameWithSuffix = "FirstName.LastName.2";

        given(passwordGenerator.generatePassword()).willReturn(password);
        given(usernameHelper.generateUsername(any(Trainer.class))).willReturn(username);
        given(trainerDao.existsByUsername(anyString())).willReturn(true);
        given(trainerDao.calculateTrainersWithFirstNameAndLastName(anyString(), anyString())).willReturn(1L);
        given(usernameHelper.generateUsername(any(Trainer.class), anyString())).willReturn(usernameWithSuffix);
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
        verify(usernameHelper, times(1)).generateUsername(any(Trainer.class));
        verify(trainerDao, times(1)).existsByUsername(anyString());
        verify(trainerDao, times(1)).calculateTrainersWithFirstNameAndLastName(anyString(), anyString());
        verify(usernameHelper, times(1)).generateUsername(any(Trainer.class), anyString());
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
    @DisplayName("Test of the method selectTrainer - successful execution, should return trainer by username")
    void testSelectTrainer_positive() {
        // given
        var trainer = createTrainer();
        trainer.setUsername("FirstName.LastName");

        given(trainerDao.findByUsername(anyString())).willReturn(Optional.of(trainer));

        // when
        var actualResult = trainerService.selectTrainer("FirstName.LastName");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainer);
        assertThat(actualResult.getUsername()).isEqualTo("FirstName.LastName");

        verify(trainerDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method selectTrainer - failure execution, should throw NoSuchEntityException")
    void testSelectTrainer_negative() {
        // given
        given(trainerDao.findByUsername(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> trainerService.selectTrainer("FirstName.LastName"))
                .isInstanceOf(com.epam.laboratory.app.exception.NoSuchEntityException.class)
                .hasMessageContaining("Trainer with username FirstName.LastName not found");

        verify(trainerDao, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method selectAllTrainers - successful execution, should return collection of trainers")
    void testSelectAllTrainers_positive() {
        // given
        given(trainerDao.findAll()).willReturn(List.of(new Trainer() {{ setId(1L); }}, new Trainer() {{ setId(2L); }}));

        // when
        var actualResult = trainerService.selectAllTrainees();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).hasSize(2);
        actualResult.forEach(trainer -> assertThat(trainer).isInstanceOf(Trainer.class));

        verify(trainerDao, times(1)).findAll();
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method selectAllTrainers - should return empty collection if there are no trainers")
    void testSelectAllTrainers_negative() {
        // given
        given(trainerDao.findAll()).willReturn(Collections.emptyList());

        // when
        var actualResult = trainerService.selectAllTrainees();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(trainerDao, times(1)).findAll();
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