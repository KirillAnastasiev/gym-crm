package com.epam.laboratory.app.util;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.User;
import com.epam.laboratory.app.repository.TraineeDao;
import com.epam.laboratory.app.repository.TrainerDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class UsernameHelper {
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    public String generateUsername(User user) {
        Class<? extends User> userClass = user.getClass();
        Collection<? extends User> usersWithSuchFirstNameAndLastName = switch (userClass.getSimpleName()) {
            case "Trainer" -> trainerDao.findByCondition(
                    t -> t.getFirstName().equals(user.getFirstName())
                            && t.getLastName().equals(user.getLastName()), Trainer.class);
            case "Trainee" -> traineeDao.findByCondition(
                    t -> t.getFirstName().equals(user.getFirstName())
                            && t.getLastName().equals(user.getLastName()), Trainee.class);
            default -> throw new IllegalArgumentException("Unsupported user class: " + userClass);
        };

        boolean isAlreadyExists = !usersWithSuchFirstNameAndLastName.isEmpty();
        if (isAlreadyExists) {
            long traineesCount = usersWithSuchFirstNameAndLastName.size();
            return generateUsername(user.getFirstName(), user.getLastName(), String.valueOf(traineesCount + 1));
        } else {
            return generateUsername(user.getFirstName(), user.getLastName());
        }
    }

    public static String generateUsername(String firstName, String lastName) {
        return firstName + "." + lastName;
    }

    public static String generateUsername(String firstName, String lastName, String suffix) {
        return generateUsername(firstName, lastName) + suffix;
    }
}
