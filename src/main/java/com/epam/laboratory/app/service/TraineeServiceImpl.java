package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.repository.TraineeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.slf4j.event.Level.INFO;

@Service
@Transactional(rollbackFor = Exception.class)
public class TraineeServiceImpl extends AbstractUserService<Trainee> implements TraineeService {

    public TraineeServiceImpl(@Autowired TraineeDao traineeDao) {
        super(traineeDao);
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public Optional<Trainee> selectByUsername(String username) {
        return ((TraineeDao) dao).findByUsername(username);
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public boolean checkPasswordForUsername(String userName, String password) {
        var traineeOptional = ((TraineeDao) dao).findByUsername(userName);
        return traineeOptional.map(Trainee::getPassword)
                .filter(password::equals)
                .isPresent();
    }

    @Logging(INFO)
    @Override
    public void deleteByUsername(String username) {
        ((TraineeDao) dao).deleteByUsername(username);
    }

}
