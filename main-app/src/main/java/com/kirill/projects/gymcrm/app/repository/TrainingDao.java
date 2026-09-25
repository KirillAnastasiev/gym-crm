package com.kirill.projects.gymcrm.app.repository;

import com.kirill.projects.gymcrm.app.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrainingDao extends EntityDao<Training>, JpaRepository<Training, Long>, JpaSpecificationExecutor<Training> {
}
