package com.kirill.projects.gymcrm.app.service;

import com.kirill.projects.gymcrm.app.aspect.annotation.Logging;
import com.kirill.projects.gymcrm.app.client.TrainingReportMessagingClient;
import com.kirill.projects.gymcrm.app.domain.Training;
import com.kirill.projects.gymcrm.app.domain.TrainingFilter;
import com.kirill.projects.gymcrm.app.repository.TrainingDao;
import com.kirill.projects.gymcrm.app.util.InputDataValidator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import static com.kirill.projects.gymcrm.app.service.EntityService.and;
import static com.kirill.projects.gymcrm.app.service.EntityService.conditionJoiner;
import static com.kirill.projects.gymcrm.app.service.TrainingService.*;
import static org.slf4j.event.Level.INFO;

@Service
@Transactional(rollbackFor = Exception.class)
public class TrainingServiceImpl extends AbstractEntityService<Training> implements TrainingService {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingReportMessagingClient trainingReportMessagingClient;

    @Autowired
    public TrainingServiceImpl(TrainingDao trainingDao,
                               @Lazy TraineeService traineeService,
                               @Lazy TrainerService trainerService,
                               TrainingReportMessagingClient trainingReportMessagingClient) {
        super(trainingDao);
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingReportMessagingClient = trainingReportMessagingClient;
    }

    @Logging(INFO)
    @Override
    public Training registerNew(Training training) {
        InputDataValidator.validateNotNull(training, "Training");
        var traineeUsername = training.getTrainee().getUsername();
        var trainerUsername = training.getTrainer().getUsername();
        var trainee = traineeService.selectByUsername(traineeUsername);
        var trainer = trainerService.selectByUsername(trainerUsername);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        var registeredTraining = ((TrainingDao) dao).save(training);
        trainingReportMessagingClient.sendTrainingReportAdd(registeredTraining);
        return registeredTraining;
    }

    @Logging(Level.INFO)
    @Override
    public Training update(Training training) {
        InputDataValidator.validateNotNull(training, "Training");
        return ((TrainingDao) dao).save(training);
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public Collection<Training> selectForTrainee(String traineeUsername, TrainingFilter filter) {
        InputDataValidator.validateNotBlank(traineeUsername, "Trainee username");
        if (filter != null) {
            BiFunction<CriteriaBuilder, Root<Training>, Predicate>[] conditions = filterConditions(filter);
            return dao.findByCondition(and(conditionJoiner(conditions), byTraineeUsernames(traineeUsername)));
        } else {
            return dao.findByCondition(byTraineeUsernames(traineeUsername));
        }
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public Collection<Training> selectForTrainer(String trainerUsername, TrainingFilter filter) {
        InputDataValidator.validateNotBlank(trainerUsername, "Trainer username");
        if (filter != null) {
            BiFunction<CriteriaBuilder, Root<Training>, Predicate>[] filterConditions = filterConditions(filter);
            return dao.findByCondition(and(conditionJoiner(filterConditions), byTrainerUsernames(trainerUsername)));
        } else {
            return dao.findByCondition(byTrainerUsernames(trainerUsername));
        }
    }

    @SuppressWarnings("unchecked")
    private static BiFunction<CriteriaBuilder, Root<Training>, Predicate>[] filterConditions(TrainingFilter filter) {
        var traineeUsername = filter.getTraineeUsername();
        var trainerUsername = filter.getTrainerUsername();
        var trainingTypeName = filter.getTrainingTypeName();
        var periodFrom = filter.getPeriodFrom();
        var periodTo = filter.getPeriodTo();

        List<BiFunction<CriteriaBuilder, Root<Training>, Predicate>> conditions = new ArrayList<>();
        if (traineeUsername != null) {
            conditions.add(byTraineeUsernames(traineeUsername));
        }
        if (trainerUsername != null) {
            conditions.add(byTrainerUsernames(trainerUsername));
        }
        if (trainingTypeName != null) {
            conditions.add(byTrainingTypeNames(trainingTypeName));
        }
        if (periodFrom != null) {
            conditions.add(fromDate(periodFrom));
        }
        if (periodTo != null) {
            conditions.add(toDate(periodTo));
        }

        return conditions.toArray(new BiFunction[0]);
    }
}
