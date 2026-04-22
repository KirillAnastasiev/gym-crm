package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.repository.TraineeDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

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
        trainee.setUsername(getUsername(trainee));
    }

    private String getUsername(Trainee trainee) {
        Collection<Trainee> traineesWithSameFirstNameAndLastName = dao.findByCondition(
                t -> t.getFirstName().equals(trainee.getFirstName())
                        && t.getLastName().equals(trainee.getLastName()), Trainee.class);
        return usernameHelper.generateUsername(trainee.getFirstName(), trainee.getLastName(), traineesWithSameFirstNameAndLastName);
    }
}
