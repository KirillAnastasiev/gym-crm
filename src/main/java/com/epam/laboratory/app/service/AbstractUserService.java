package com.epam.laboratory.app.service;

import com.epam.laboratory.app.aspect.Logging;
import com.epam.laboratory.app.domain.User;
import com.epam.laboratory.app.repository.UserDao;
import com.epam.laboratory.app.util.PasswordGenerator;
import com.epam.laboratory.app.util.UsernameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.slf4j.event.Level.INFO;

@Service
@Transactional(rollbackFor = Exception.class)
public abstract class AbstractUserService<T extends User> extends AbstractService<T> implements UserService<T> {
    protected AbstractUserService(@Autowired UserDao<T> userDao)  {
        super(userDao);
    }

    @Logging(INFO)
    @Override
    public void changePassword(T user, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            newPassword = PasswordGenerator.generatePassword();
        }
        user.setPassword(newPassword);
        dao.update(user);
    }

    @Logging(INFO)
    @Override
    public void changeStatus(T user, boolean isActive) {
        user.setActive(isActive);
        dao.update(user);
    }

    @Override
    protected void prepareEntity(T user) {
        user.setPassword(PasswordGenerator.generatePassword());
        user.setUsername(UsernameHelper.generateUsername(user, username ->
                ((UserDao<T>) this.dao).existsByUsername(username)));
    }
}
