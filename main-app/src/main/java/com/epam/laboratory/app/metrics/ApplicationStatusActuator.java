package com.epam.laboratory.app.metrics;

import com.epam.laboratory.app.service.TraineeService;
import com.epam.laboratory.app.service.TrainerService;
import com.epam.laboratory.app.service.TrainingService;
import com.epam.laboratory.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.Access;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@Endpoint(id = "status", defaultAccess = Access.READ_ONLY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ApplicationStatusActuator {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @ReadOperation(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> getApplicationStatus() {
        return Map.of(
                "registered trainees", getTraineeRegistrationCount(),
                "active trainees", getActiveTraineeCount(),
                "registered trainers", getTrainerRegistrationCount(),
                "active trainers", getActiveTrainerCount(),
                "booked trainings", getTrainingBookingCount(),
                "completed trainings", getTrainingCompletionCount()
        );
    }

    private long getTraineeRegistrationCount() {
        return traineeService.count();
    }

    private long getActiveTraineeCount() {
        return traineeService.countByCondition(UserService.byStatus(true));
    }

    private long getTrainerRegistrationCount() {
        return trainerService.count();
    }

    private long getActiveTrainerCount() {
        return trainerService.countByCondition(UserService.byStatus(true));
    }

    private long getTrainingBookingCount() {
        return trainingService.count();
    }

    private long getTrainingCompletionCount() {
        return trainingService.countByCondition(TrainingService.toDate(LocalDateTime.now()));
    }
}
