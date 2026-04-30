package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.repository.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.slf4j.event.Level.INFO;

@Service
@Transactional(rollbackFor = Exception.class)
public class TrainerServiceImpl extends AbstractUserService<Trainer> implements TrainerService {

    public TrainerServiceImpl(@Autowired TrainerDao trainerDao) {
        super(trainerDao);
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public Optional<Trainer> selectByUsername(String username) {
        return ((TrainerDao) dao).findByUsername(username);
    }

    @Logging(INFO)
    @Transactional(readOnly = true)
    @Override
    public boolean checkPasswordForUsername(String userName, String password) {
        var trainerOptional = ((TrainerDao) dao).findByUsername(userName);
        return trainerOptional.map(Trainer::getPassword)
                .filter(password::equals)
                .isPresent();
    }

    @Override
    public void deleteByUsername(String username) {
        ((TrainerDao) dao).deleteByUsername(username);
    }

}
