package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.repository.TraineeDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeServiceImpl extends AbstractService<Trainee> implements TraineeService {

    public TraineeServiceImpl(@Autowired TraineeDao traineeDao) {
        super(traineeDao);
    }

    @Override
    protected void prepareEntity(Trainee trainee) {
        trainee.setPassword(PasswordGenerator.generatePassword());
        trainee.setUsername(UsernameHelper.generateUsername(trainee, username ->
                !dao.findByCondition(u -> u.getUsername().equals(username), Trainee.class).isEmpty()));
    }
}
