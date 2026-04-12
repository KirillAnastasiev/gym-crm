package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Training;

import java.util.Optional;

public interface TrainingDao {
    Training save(Training training);
    Optional<Training> findById(long id);
}
