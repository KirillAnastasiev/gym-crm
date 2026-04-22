package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.User;
import com.epam.laboratory.app.repository.TraineeDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class TraineeServiceImpl extends UserService<Trainee> implements TraineeService {
    private final UsernameHelper usernameHelper;

    public TraineeServiceImpl(@Autowired TraineeDao traineeDao, UsernameHelper usernameHelper) {
        super(traineeDao);
        this.usernameHelper = usernameHelper;
    }

    @Override
    protected void prepareUser(Trainee trainee) {
        trainee.setPassword(PasswordGenerator.generatePassword());
        TraineeDao traineeDao = (TraineeDao) dao;
        Predicate<Trainee> usernamePredicate = t -> t.getFirstName().equals(trainee.getFirstName())
                && t.getLastName().equals(trainee.getLastName());

        Function<User, String> usernameGeneratorStrategy = t -> {;
            long traineesCount = traineeDao.findByCondition(usernamePredicate, Trainee.class).size();
            if (traineesCount > 0) {
                return UsernameHelper.generateUsername(t.getFirstName(), t.getLastName(), String.valueOf(traineesCount + 1));
            } else {
                return UsernameHelper.generateUsername(t.getFirstName(), t.getLastName());
            }
        };

        trainee.setUsername(usernameHelper.generateUsername(trainee, usernameGeneratorStrategy));
    }
}
