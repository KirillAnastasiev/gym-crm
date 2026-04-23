package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.repository.TrainerDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerServiceImpl extends AbstractService<Trainer> implements TrainerService {

    public TrainerServiceImpl(@Autowired TrainerDao trainerDao) {
        super(trainerDao);
    }

    @Override
    protected void prepareEntity(Trainer trainer) {
        trainer.setPassword(PasswordGenerator.generatePassword());
        trainer.setUsername(UsernameHelper.generateUsername(trainer, username ->
                !dao.findByCondition(u -> u.getUsername().equals(username), Trainer.class).isEmpty()));
    }
}
