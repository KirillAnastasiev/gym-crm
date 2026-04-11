package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Trainee;

public interface TraineeDao {
    Trainee save(Trainee trainee);
    Trainee findById(long id);
    Trainee update(Trainee trainee);
    void delete(long id);
}
