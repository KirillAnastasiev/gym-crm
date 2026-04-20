package com.epam.laboratory.app.util;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UsernameHelperTest {
    private UsernameHelper usernameHelper;

    @BeforeEach
    void setUp() {
        usernameHelper = new UsernameHelper();
    }

    @Test
    @DisplayName("Test of the method generateUsername with suffix - should generate username in format 'firstName.lastName' if there are no users with such first name and last name")
    void testGenerateUsernameWithSuffix_noUsersWithSuchFirstNameAndLastName() {
        // given
        String firstName = "FirstName";
        String lastName = "LastName";
        String expectedUsername = "FirstName.LastName";

        // when
        var actualResult = usernameHelper.generateUsername(firstName, lastName, java.util.Collections.emptyList());

        // then
        assertThat(actualResult).isEqualTo(expectedUsername);
    }

    @Test
    @DisplayName("Test of the method generateUsername with suffix - should generate username in format 'firstName.lastNameN' where N is the number of users with such first name and last name + 1")
    void testGenerateUsername_withUsersWithSuchFirstNameAndLastName() {
        // given
        String firstName = "FirstName";
        String lastName = "LastName";
        String expectedUsername = "FirstName.LastName3";
        var usersWithSuchFirstNameAndLastName = java.util.List.of(new Trainee(), new Trainee());

        // when
        var actualResult = usernameHelper.generateUsername(firstName, lastName, usersWithSuchFirstNameAndLastName);

        // then
        assertThat(actualResult).isEqualTo(expectedUsername);
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
        var actualResult = usernameHelper.generateUsername(firstName, lastName);

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
        var actualResult = usernameHelper.generateUsername(firstName, lastName, suffix);

        // then
        assertThat(actualResult).isEqualTo(expectedUsername);
    }
}