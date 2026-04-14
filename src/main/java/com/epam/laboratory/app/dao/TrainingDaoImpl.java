package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainingDaoImpl implements TrainingDao {
    private static final String KEY_PREFIX = "training:";

    private final Map<String, Training> trainingMap;

    @Override
    public Training save(Training training) {
        long id = calculateNextId();
        training.setId(id);
        trainingMap.put(KEY_PREFIX + id, training);
        return training;
    }

    @Override
    public Optional<Training> findById(String id) {
        return Optional.ofNullable(trainingMap.get(id));
    }

    private long calculateNextId() {
        return trainingMap.values()
                .stream()
                .map(Training::getId)
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1;
    }
}
