package com.kirill.projects.gymcrm.app.metrics;

import com.kirill.projects.gymcrm.app.service.TraineeService;
import com.kirill.projects.gymcrm.app.service.TrainerService;
import com.kirill.projects.gymcrm.app.service.TrainingService;
import com.kirill.projects.gymcrm.app.service.UserService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor(onConstructor_ =  @Autowired)
public class GymCrmMetrics {

    private final MeterRegistry meterRegistry;
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;

    @PostConstruct
    void init() {
        Gauge.builder("gym.trainee.registrations", this::getTraineeRegistrationCount)
                .description("Total number of trainee registrations")
                .tag("service", "GymCRM")
                .register(meterRegistry);

        Gauge.builder("gym.trainee.active", this::getActiveTraineeCount)
                .description("Current number of active trainees")
                .tag("service", "GymCRM")
                .register(meterRegistry);

        Gauge.builder("gym.trainer.registrations", this::getTrainerRegistrationCount)
                .description("Total number of trainer registrations")
                .tag("service", "GymCRM")
                .register(meterRegistry);

        Gauge.builder("gym.trainer.active", this::getActiveTrainerCount)
                .description("Current number of active trainers")
                .tag("service", "GymCRM")
                .register(meterRegistry);

        Gauge.builder("gym.training.bookings", this::getTrainingBookingCount)
                .description("Total number of training bookings")
                .tag("service", "GymCRM")
                .register(meterRegistry);

        Gauge.builder("gym.training.completed", this::getTrainingCompletionCount)
                .description("Total number of completed training bookings")
                .tag("service", "GymCRM")
                .register(meterRegistry);

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

