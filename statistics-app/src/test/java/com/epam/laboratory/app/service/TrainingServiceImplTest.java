package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.TrainerStatus;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.repository.TrainingDao;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingServiceImpl test suite")
class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingServiceImpl trainingService;


    // ==================== GET TRAININGS BY TRAINER USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method getTrainingsByTrainerUsername - should return list of trainings for given trainer username")
    void testGetTrainingsByTrainerUsername_positive() {
        // given
        var username = "FirstName.LastName";
        var trainings = List.of(createTestTraining());

        given(trainingDao.findByTrainerUsername(anyString())).willReturn(trainings);

        // when
        var actualResult = trainingService.getTrainingsByTrainerUsername(username);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).isEqualTo(trainings);

        verify(trainingDao, times(1)).findByTrainerUsername(anyString());
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method getTrainingsByTrainerUsername - should return empty list when no trainings found for given trainer username")
    void testGetTrainingsByTrainerUsername_negative_unknownUsername() {
        // given
        var username = "Unknown.Username";

        given(trainingDao.findByTrainerUsername(anyString())).willReturn(Collections.emptyList());

        // when
        var actualResult = trainingService.getTrainingsByTrainerUsername(username);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(trainingDao, times(1)).findByTrainerUsername(anyString());
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method getTrainingsByTrainerUsername - should throw RuntimeException when dao throws exception")
    void testGetTrainingsByTrainerUsername_negative_daoException() {
        // given
        var username = "FirstName.LastName";

        doThrow(new RuntimeException("Database error")).when(trainingDao).findByTrainerUsername(anyString());

        // when & then
        assertThatThrownBy(() ->  trainingService.getTrainingsByTrainerUsername(username))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(trainingDao, times(1)).findByTrainerUsername(anyString());
        verifyNoMoreInteractions(trainingDao);
    }


    // ==================== GET TRAININGS BY TRAINER USERNAME BETWEEN DATES TESTS ====================

    @Test
    @DisplayName("Test of the method getTrainingsByTrainerUsernameBetweenDates - should return list of trainings for given trainer username and date range")
    void testGetTrainingsByTrainerUsernameBetweenDates_positive() {
        // given
        var username = "FirstName.LastName";
        var fromDate = LocalDate.of(2024, 7, 1);
        var toDate = LocalDate.of(2024, 7, 31);
        var trainings = List.of(createTestTraining());

        given(trainingDao.findByTrainerUsernameAndTrainingDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(trainings);

        // when
        var actualResult = trainingService.getTrainingsByTrainerUsernameBetweenDates(username, fromDate, toDate);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).isEqualTo(trainings);

        verify(trainingDao, times(1)).findByTrainerUsernameAndTrainingDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method getTrainingsByTrainerUsernameBetweenDates - should return empty list when no trainings found for given trainer username")
    void testGetTrainingsByTrainerUsernameBetweenDates_negative_unknownUsername() {
        // given
        var username = "Unknown.Username";
        var fromDate = LocalDate.of(2023, 7, 1);
        var toDate = LocalDate.of(2023, 7, 31);

        given(trainingDao.findByTrainerUsernameAndTrainingDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(Collections.emptyList());

        // when
        var actualResult = trainingService.getTrainingsByTrainerUsernameBetweenDates(username, fromDate, toDate);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(trainingDao, times(1)).findByTrainerUsernameAndTrainingDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method getTrainingsByTrainerUsernameBetweenDates - should return empty list when no trainings found for given date range")
    void testGetTrainingsByTrainerUsernameBetweenDates_negative_notSatisfiedDates() {
        // given
        var username = "FirstName.LastName";
        var fromDate = LocalDate.of(2024, 7, 1);
        var toDate = LocalDate.of(2024, 7, 31);

        given(trainingDao.findByTrainerUsernameAndTrainingDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(Collections.emptyList());

        // when
        var actualResult = trainingService.getTrainingsByTrainerUsernameBetweenDates(username, fromDate, toDate);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(trainingDao, times(1)).findByTrainerUsernameAndTrainingDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method getTrainingsByTrainerUsernameBetweenDates - should throw RuntimeException when dao throws exception")
    void testGetTrainingsByTrainerUsernameBetweenDates_negative_daoException() {
        // given
        var username = "FirstName.LastName";
        var fromDate = LocalDate.of(2024, 7, 1);
        var toDate = LocalDate.of(2024, 7, 31);

        doThrow(new RuntimeException("Database error"))
                .when(trainingDao).findByTrainerUsernameAndTrainingDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));

        // when & then
        assertThatThrownBy(() ->  trainingService.getTrainingsByTrainerUsernameBetweenDates(username, fromDate, toDate))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(trainingDao, times(1)).findByTrainerUsernameAndTrainingDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        verifyNoMoreInteractions(trainingDao);
    }

    @Test
    @DisplayName("Test of the method getTrainingsByTrainerUsernameBetweenDates - should throw ValidationException when from date is after to date")
    void testGetTrainingsByTrainerUsernameBetweenDates_negative_invalidDateRange() {
        // given
        var username = "FirstName.LastName";
        var fromDate = LocalDate.of(2024, 7, 31);
        var toDate = LocalDate.of(2024, 7, 1);

        // when & then
        assertThatThrownBy(() ->  trainingService.getTrainingsByTrainerUsernameBetweenDates(username, fromDate, toDate))
                .isInstanceOf(ValidationException.class)
                .hasMessage("From date: " + fromDate + " to date: " + toDate);

        verifyNoInteractions(trainingDao);
    }

    private static Training createTestTraining() {
        var training = new Training();
        training.setId(1L);
        training.setTrainerUsername("FirstName.LastName");
        training.setTrainerFirstName("FirstName");
        training.setTrainerLastName("LastName");
        training.setTrainerStatus(TrainerStatus.ACTIVE);
        training.setTrainingDate(LocalDateTime.of(2024, 7, 1, 8, 0));
        training.setTrainingDuration(java.time.Duration.ofHours(1));
        return training;
    }

}