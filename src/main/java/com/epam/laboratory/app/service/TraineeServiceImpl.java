package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.repository.TraineeDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        trainee.setUsername(usernameHelper.generateUsername(trainee));
    }
}
