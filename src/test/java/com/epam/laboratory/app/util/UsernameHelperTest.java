package com.epam.laboratory.app.util;

import com.epam.laboratory.app.repository.TraineeDao;
import com.epam.laboratory.app.repository.TrainerDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class UsernameHelperTest {
    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private UsernameHelper usernameHelper;

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