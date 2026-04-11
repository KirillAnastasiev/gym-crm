package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Trainer;

public interface TrainerDao {
    Trainer save(Trainer trainee);
    Trainer findById(long id);
    Trainer update(Trainer trainee);
    void delete(Trainer trainee);
}
