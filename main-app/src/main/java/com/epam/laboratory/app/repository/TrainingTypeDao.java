package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.TrainingType;
import org.slf4j.event.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeDao extends EntityDao<TrainingType>, JpaRepository<TrainingType, Long>, JpaSpecificationExecutor<TrainingType> {

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    List<TrainingType> findAll();

    @Logging(Level.INFO)
    @Transactional(readOnly = true)
    Optional<TrainingType> findByTrainingTypeName(String trainingTypeName);

}
