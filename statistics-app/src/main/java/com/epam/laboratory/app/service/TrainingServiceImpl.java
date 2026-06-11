package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.repository.TrainingDao;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@Transactional(rollbackFor = Exception.class)
public class TrainingServiceImpl implements TrainingService {

    private final TrainingDao trainingDao;

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    @Override
    public Collection<Training> getTrainingsByTrainerUsername(@NotBlank String trainerUsername) {
        return trainingDao.findByTrainerUsername(trainerUsername);
    }

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    @Override
    public Collection<Training> getTrainingsByTrainerUsernameBetweenDates(@NotBlank String trainerUsername,
                                                                          @NotNull LocalDate fromDate,
                                                                          @NotNull LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new ValidationException("From date: " + fromDate + " to date: " + toDate);
        }
        return trainingDao.findByTrainerUsernameAndTrainingDateBetween(trainerUsername, fromDate.atStartOfDay(), toDate.plusDays(1).atStartOfDay());
    }

}
