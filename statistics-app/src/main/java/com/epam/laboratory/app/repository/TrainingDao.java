package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(rollbackFor = Exception.class)
public interface TrainingDao extends JpaRepository<Training, Long> {

    @Transactional(readOnly = true)
    List<Training> findByTrainerUsername(String trainerUsername);

    @Transactional(readOnly = true)
    List<Training> findByTrainerUsernameAndTrainingDateBetween(String trainerUsername, LocalDateTime from, LocalDateTime to);
}
