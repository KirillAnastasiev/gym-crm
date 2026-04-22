package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.User;
import com.epam.laboratory.app.repository.TrainerDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.function.Predicate;

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
        TrainerDao trainerDao = (TrainerDao) dao;
        Predicate<Trainer> usernamePredicate = t -> t.getFirstName().equals(trainer.getFirstName())
                && t.getLastName().equals(trainer.getLastName());

        Function<User, String> usernameGeneratorStrategy = t -> {
            long trainersCount = trainerDao.findByCondition(usernamePredicate, Trainer.class).size();
            if (trainersCount > 0) {
                return UsernameHelper.generateUsername(t.getFirstName(), t.getLastName(), String.valueOf(trainersCount + 1));
            } else {
                return UsernameHelper.generateUsername(t.getFirstName(), t.getLastName());
            }
        };

        trainer.setUsername(usernameHelper.generateUsername(trainer, usernameGeneratorStrategy));
    }
}
