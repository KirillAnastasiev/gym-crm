package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrainingDao extends EntityDao<Training>, JpaRepository<Training, Long>, JpaSpecificationExecutor<Training> {
}
