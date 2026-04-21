package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.repository.TrainerDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TrainerServiceImpl extends UserService<Trainer> implements TrainerService {
    private final UsernameHelper usernameHelper;

    public TrainerServiceImpl(@Autowired TrainerDao trainerDao, UsernameHelper usernameHelper) {
        super(trainerDao);
        this.usernameHelper = usernameHelper;
    }

    @Override
    protected void prepareUser(Trainer trainer) {
        trainer.setPassword(PasswordGenerator.generatePassword());
        trainer.setUsername(getUsername(trainer));
    }

    private String getUsername(Trainer trainer) {
        Collection<Trainer> trainersWithSameFirstNameAndLastName = dao.findByCondition(
                t -> t.getFirstName().equals(trainer.getFirstName())
                        && t.getLastName().equals(trainer.getLastName()), Trainer.class);
        return usernameHelper.generateUsername(trainer.getFirstName(), trainer.getLastName(), trainersWithSameFirstNameAndLastName);
    }
}
