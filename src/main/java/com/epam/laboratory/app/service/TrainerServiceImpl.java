package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.repository.TrainerDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerServiceImpl extends AbstractService<Trainer> implements TrainerService {
    private final UsernameHelper usernameHelper;

    public TrainerServiceImpl(@Autowired TrainerDao trainerDao, UsernameHelper usernameHelper) {
        super(trainerDao);
        this.usernameHelper = usernameHelper;
    }

    @Override
    protected void prepareEntity(Trainer trainer) {
        trainer.setPassword(PasswordGenerator.generatePassword());
        trainer.setUsername(usernameHelper.generateUsername(trainer, dao));
    }
}
