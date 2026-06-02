package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.repository.TrainingDao;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Validated
@Transactional(rollbackFor = Exception.class)
public class TrainingServiceImpl implements TrainingService {

    private final TrainingDao trainingDao;

    @Override
    public void addTraining(@NotNull Training training) {
        try {
            log.debug("Adding training {}", training);
            var savedTraining = trainingDao.save(training);
            log.debug("Training saved with id {}", savedTraining.getId());
        } catch (Exception e) {
            log.warn("Error adding training: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteTraining(@NotNull Training training) {
        try {
            log.debug("Deleting training {}", training);
            trainingDao.delete(training);
            log.debug("Training deleted");
        } catch (Exception e) {
            log.warn("Error deleting training: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Training> getTrainingsByTrainerUsername(@NotBlank String trainerUsername) {
        try {
            log.debug("Fetching trainings for trainer {}", trainerUsername);
            var trainings = trainingDao.findByTrainerUsername(trainerUsername);
            log.debug("Found {} trainings", trainings.size());
            return trainings;
        } catch (Exception e) {
            log.warn("Error fetching trainings for trainer {}: {}", trainerUsername, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Training> getTrainingsByTrainerUsernameBetweenDates(@NotBlank String trainerUsername,
                                                                          @NotNull LocalDate fromDate,
                                                                          @NotNull LocalDate toDate) {
        try {
            log.debug("Fetching trainings for trainer {} between {} and {}", trainerUsername, fromDate, toDate);
            if (fromDate.isAfter(toDate)) {
                throw new ValidationException("From date: " + fromDate + " to date: " + toDate);
            }
            var trainings = trainingDao.findByTrainerUsernameAndTrainingDateBetween(trainerUsername, fromDate.atStartOfDay(), toDate.plusDays(1).atStartOfDay());
            log.debug("Found {} trainings for date range", trainings.size());
            return trainings;
        } catch (Exception e) {
            log.warn("Error fetching trainings for trainer {} between dates: {}", trainerUsername, e.getMessage(), e);
            throw e;
        }
    }

}
