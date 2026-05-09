package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingFilter;
import com.epam.laboratory.app.repository.TrainingDao;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import static com.epam.laboratory.app.service.EntityService.and;
import static com.epam.laboratory.app.service.EntityService.conditionJoiner;
import static com.epam.laboratory.app.service.TrainingService.*;
import static org.slf4j.event.Level.INFO;

@Service
@Transactional(rollbackFor = Exception.class)
public class TrainingServiceImpl extends AbstractEntityService<Training> implements TrainingService {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingTypeService trainingTypeService;

    public TrainingServiceImpl(@Autowired TrainingDao trainingDao,
                               @Autowired TraineeService traineeService,
                               @Autowired TrainerService trainerService,
                               @Autowired TrainingTypeService trainingTypeService) {
        super(trainingDao);
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingTypeService = trainingTypeService;
    }

    @Override
    protected void prepareEntity(Training entity) {
        // dumb method
    }

    @Logging(INFO)
    @Override
    public Training registerNew(Training entity) {
        var traineeUsername = entity.getTrainee().getUsername();
        var trainerUsername = entity.getTrainer().getUsername();
        var trainingTypeName = entity.getTrainingType().getTrainingTypeName();

        var trainee = traineeService.selectByUsername(traineeUsername);
        var trainer = trainerService.selectByUsername(trainerUsername);
        var trainingType = trainingTypeService.selectByTrainingTypeName(trainingTypeName);

        entity.setTrainee(trainee);
        entity.setTrainer(trainer);
        entity.setTrainingType(trainingType);

        return dao.save(entity);
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public Collection<Training> selectForTrainee(String traineeUsername, TrainingFilter filter) {
        if (traineeUsername == null) {
            throw new IllegalArgumentException("Trainee username must not be null");
        }
        if (traineeUsername.isEmpty()) {
            throw new IllegalArgumentException("Trainee username must not be blank");
        }

        if (filter != null) {
            BiFunction<CriteriaBuilder, Root<Training>, Predicate>[] conditions = filterConditions(filter);
            return dao.findByCondition(and(conditionJoiner(conditions), byTraineeUsernames(traineeUsername)), Training.class);
        } else {
            return dao.findByCondition(byTraineeUsernames(traineeUsername), Training.class);
        }
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public Collection<Training> selectForTrainer(String trainerUsername, TrainingFilter filter) {
        if (trainerUsername == null) {
            throw new IllegalArgumentException("Trainer username must not be null");
        }
        if (trainerUsername.isEmpty()) {
            throw new IllegalArgumentException("Trainer username must not be blank");
        }

        if (filter != null) {
            BiFunction<CriteriaBuilder, Root<Training>, Predicate>[] filterConditions = filterConditions(filter);
            return dao.findByCondition(and(conditionJoiner(filterConditions), byTrainerUsernames(trainerUsername)), Training.class);
        } else {
            return dao.findByCondition(byTrainerUsernames(trainerUsername), Training.class);
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
