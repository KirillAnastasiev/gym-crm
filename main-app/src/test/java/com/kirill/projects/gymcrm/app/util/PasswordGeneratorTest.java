package com.kirill.projects.gymcrm.app.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("PasswordGenerator test suite")
class PasswordGeneratorTest {

    // ==================== GENERATE PASSWORD TESTS ====================

    @RepeatedTest(
            value = 20,
            name = "{displayName}, repetition {currentRepetition} of {totalRepetitions}",
            failureThreshold = 1
    )
    @DisplayName("Test of the method generatePassword - should generate password with length 10")
    void testGeneratedPassword_length() {
        // when
        var actualResult = PasswordGenerator.generatePassword();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).hasSize(10);
    }

    @RepeatedTest(
            value = 20,
            name = "{displayName}, repetition {currentRepetition} of {totalRepetitions}",
            failureThreshold = 1
    )
    @DisplayName("Test of the method generatePassword - should generate password with only visible ASCII characters")
    void testGeneratedPassword_validCharacters() {
        // when
        var actualResult = PasswordGenerator.generatePassword();

        // then
        assertThat(actualResult).isNotNull();
        actualResult.codePoints()
                .forEach(codePoint -> assertThat((char) codePoint).isBetween('!', '~'));
    }

}