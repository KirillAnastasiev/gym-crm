package com.epam.laboratory.app.util;

import com.epam.laboratory.app.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UsernameHelperTest {
    private UsernameHelper usernameHelper;

    @BeforeEach
    void setUp() {
        usernameHelper = new UsernameHelper();
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
    public void testGenerateUsername(String firstName, String lastName, String expectedUsername) {
        // given
        var user = new User() {};
        user.setFirstName(firstName);
        user.setLastName(lastName);

        // when
        var actualResult = usernameHelper.generateUsername(user);

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
    public void testGenerateUsernameWithSuffix(String firstName, String lastName, String suffix, String expectedUsername) {
        // given
        var user = new User() {};
        user.setFirstName(firstName);
        user.setLastName(lastName);

        // when
        var actualResult = usernameHelper.generateUsername(user, suffix);

        // then
        assertThat(actualResult).isEqualTo(expectedUsername);
    }
}