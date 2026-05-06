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
public abstract class AbstractUserService<T extends User> extends AbstractEntityService<T> implements UserService<T> {

    protected final AuthenticationService authenticationService;

    protected AbstractUserService(@Autowired UserDao<T> userDao,
                                  @Autowired AuthenticationService authenticationService)  {
        super(userDao);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void prepareEntity(T user) {
        user.setPassword(PasswordGenerator.generatePassword());
        user.setUsername(UsernameHelper.generateUsername(user, authenticationService::checkExistsByUsername));
        user.setActive(true);
    }

}
