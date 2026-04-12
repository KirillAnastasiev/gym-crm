package com.epam.laboratory.app.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class PasswordGeneratorTest {
    private PasswordGenerator passwordGenerator;

    @BeforeEach
    public void init() {
        passwordGenerator = new PasswordGenerator();
    }

    @RepeatedTest(value = 20,
                  name = "{displayName}, repetition {currentRepetition} of {totalRepetitions}",
                  failureThreshold = 1)
    @DisplayName("Test of the method generatePassword - should generate password with length 10")
    void testGeneratedPasswordLength() {
        // when
        var actualResult = passwordGenerator.generatePassword();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.length()).isEqualTo(10);
    }

    @RepeatedTest(value = 20,
                  name = "{displayName}, repetition {currentRepetition} of {totalRepetitions}",
                  failureThreshold = 1)
    @DisplayName("Test of the method generatePassword - should generate password with only visible ASCII characters")
    public void testGeneratedPasswordValidCharacters() {
        // when
        var actualResult = passwordGenerator.generatePassword();

        // then
        assertThat(actualResult).isNotNull();
        actualResult.codePoints()
                .forEach(codePoint -> assertThat((char) codePoint).isBetween('!', '~'));
    }
}