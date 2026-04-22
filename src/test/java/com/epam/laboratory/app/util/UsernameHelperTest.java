package com.epam.laboratory.app.util;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.repository.TraineeDao;
import com.epam.laboratory.app.repository.TrainerDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsernameHelperTest {
    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private UsernameHelper usernameHelper;

    @Test
    @DisplayName("Test of the method generateUsername - should generate username in format 'firstName.lastName' if there are no users with such first name and last name")
    void testGenerateUsername_noUsersWithSuchFirstNameAndLastName() {
        // given
        var trainee = new Trainee();
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");

        var trainer = new Trainer();
        trainer.setFirstName("FirstName");
        trainer.setLastName("LastName");
        String expectedUsername = "FirstName.LastName";

        given(traineeDao.findByCondition(any(Predicate.class), eq(Trainee.class))).willReturn(java.util.Collections.emptyList());
        given(trainerDao.findByCondition(any(Predicate.class), eq(Trainer.class))).willReturn(java.util.Collections.emptyList());

        // when
        var actualResult = usernameHelper.generateUsername(trainee);

        // then
        assertThat(actualResult).isEqualTo(expectedUsername);

        verify(traineeDao, times(1)).findByCondition(any(Predicate.class), eq(Trainee.class));
        verifyNoMoreInteractions(traineeDao);
        verifyNoInteractions(trainerDao);

        // when
        actualResult = usernameHelper.generateUsername(trainer);

        // then
        assertThat(actualResult).isEqualTo(expectedUsername);

        verify(trainerDao, times(1)).findByCondition(any(Predicate.class), eq(Trainer.class));
        verifyNoMoreInteractions(trainerDao);
        verifyNoMoreInteractions(traineeDao);
    }

    @Test
    @DisplayName("Test of the method generateUsername - should generate username in format 'firstName.lastNameN' if there are N users with such first name and last name")
    void testGenerateUsername_existedUsersWithSuchFirstNameAndLastName() {
        // given
        var trainee1 = new Trainee();
        trainee1.setFirstName("FirstName");
        trainee1.setLastName("LastName");

        var trainee2 = new Trainee();
        trainee2.setFirstName("FirstName");
        trainee2.setLastName("LastName");

        var newTrainee = new Trainee();
        newTrainee.setFirstName("FirstName");
        newTrainee.setLastName("LastName");

        String expectedUsername = "FirstName.LastName3";

        given(traineeDao.findByCondition(any(Predicate.class), eq(Trainee.class))).willReturn(List.of(trainee1, trainee2));

        // when
        var actualResult = usernameHelper.generateUsername(newTrainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(expectedUsername);

        verify(traineeDao, times(1)).findByCondition(any(Predicate.class), eq(Trainee.class));
        verifyNoMoreInteractions(traineeDao);
        verifyNoInteractions(trainerDao);
    }

    @ParameterizedTest
    @CsvSource({
            "John, Doe, John.Doe",
            "Jane, Smith, Jane.Smith",
            "Alice, Johnson, Alice.Johnson",
            "Bob, Brown, Bob.Brown",
            "Charlie, Davis, Charlie.Davis",
            "David, Wilson, David.Wilson",
            "Eve, Miller, Eve.Miller",
            "Frank, Garcia, Frank.Garcia",
            "Grace, Martinez, Grace.Martinez",
            "Hank, Rodriguez, Hank.Rodriguez"
    })
    @DisplayName("Test of the method generateUsername - should generate username in format 'firstName.lastName'")
    void testGenerateUsername(String firstName, String lastName, String expectedUsername) {
        // when
        var actualResult = UsernameHelper.generateUsername(firstName, lastName);

        // then
        assertThat(actualResult).isEqualTo(expectedUsername);
    }

    @ParameterizedTest
    @CsvSource({
            "John, Doe, 123, John.Doe123",
            "Jane, Smith, 456, Jane.Smith456",
            "Alice, Johnson, 789, Alice.Johnson789",
            "Bob, Brown, 101, Bob.Brown101",
            "Charlie, Davis, 202, Charlie.Davis202",
            "David, Wilson, 303, David.Wilson303",
            "Eve, Miller, 404, Eve.Miller404",
            "Frank, Garcia, 505, Frank.Garcia505",
            "Grace, Martinez, 606, Grace.Martinez606",
            "Hank, Rodriguez, 707, Hank.Rodriguez707"
    })
    @DisplayName("Test of the method generateUsername with suffix - should generate username in format 'firstName.lastNameSuffix'")
    void testGenerateUsernameWithSuffix(String firstName, String lastName, String suffix, String expectedUsername) {
        // when
        var actualResult = UsernameHelper.generateUsername(firstName, lastName, suffix);

        // then
        assertThat(actualResult).isEqualTo(expectedUsername);
    }
}