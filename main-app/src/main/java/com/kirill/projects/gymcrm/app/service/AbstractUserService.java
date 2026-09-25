package com.kirill.projects.gymcrm.app.service;

import com.kirill.projects.gymcrm.app.domain.User;
import com.kirill.projects.gymcrm.app.repository.UserDao;
import com.kirill.projects.gymcrm.app.service.security.AuthenticationService;
import com.kirill.projects.gymcrm.app.util.PasswordGenerator;
import com.kirill.projects.gymcrm.app.util.UsernameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public abstract class AbstractUserService<T extends User> extends AbstractEntityService<T> implements UserService<T> {

    protected final AuthenticationService authenticationService;

    @Autowired
    protected AbstractUserService(UserDao<T> userDao,
                                  AuthenticationService authenticationService)  {
        super(userDao);
        this.authenticationService = authenticationService;
    }

    protected void prepareUser(T user) {
        user.setPassword(PasswordGenerator.generatePassword());
        user.setUsername(UsernameHelper.generateUsername(user, authenticationService::checkExistsByUsername));
        user.setActive(true);
    }

}
