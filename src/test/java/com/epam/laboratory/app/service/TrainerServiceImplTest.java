package com.epam.laboratory.app.service;


import com.epam.laboratory.app.dao.TrainerDao;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.util.PasswordGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceImplTest {
    @Mock
    private TrainerDao trainerDao;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    @DisplayName("Test of the method createTrainer - should create trainer with generated password")
    public void testCreateTrainer() {
        // given
        var trainer = createTrainer();
        var password = "generatedPassword";

        given(passwordGenerator.generatePassword()).willReturn(password);
        given(trainerDao.save(any(Trainer.class))).willReturn(trainer);

        // when
        var actualResult = trainerService.createTrainer(trainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(trainer);
        assertThat(actualResult.getPassword()).isEqualTo(password);

        verify(passwordGenerator, times(1)).generatePassword();
        verify(trainerDao, times(1)).save(any(Trainer.class));
        verifyNoMoreInteractions(passwordGenerator, trainerDao);
    }

    @Test
    @DisplayName("Test of the method updateTrainer - should update trainer and return")
    public void testUpdateTrainer() {
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
    @DisplayName("Test of the method selectTrainer - successful execution, should return trainer by id")
    public void testSelectTrainer_positive() {
        // given
        var trainer = createTrainer();

        given(trainerDao.findById(anyLong())).willReturn(java.util.Optional.of(trainer));

        // when
        var actualResult = trainerService.selectTrainer(1L);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult).isEqualTo(trainer);

        verify(trainerDao, times(1)).findById(anyLong());
        verifyNoMoreInteractions(trainerDao);
    }

    @Test
    @DisplayName("Test of the method selectTrainer - failure execution, should throw NoSuchEntityException")
    public void testSelectTrainer_negative() {
        // given
        given(trainerDao.findById(anyLong())).willReturn(java.util.Optional.empty());

        // when & then
        assertThatThrownBy(() -> trainerService.selectTrainer(1L))
                .isInstanceOf(com.epam.laboratory.app.exception.NoSuchEntityException.class)
                .hasMessageContaining("Trainer with id 1 not found");

        verify(trainerDao, times(1)).findById(anyLong());
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