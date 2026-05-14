package com.epam.laboratory.app.util;

import com.epam.laboratory.app.dto.annotation.Required;
import com.epam.laboratory.app.exception.DtoValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class DtoValidatorTest {

    @Test
    @DisplayName("Test of the method validate - should not throw an exception when all required fields are present")
    void testValidate_positive() {
        // given
        var testDto = new TestDto("required value", "optional value");

        // when
        assertThatCode(() -> DtoValidator.validate(testDto)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Test of the method validate - should throw an exception when the DTO is null")
    void testValidate_negative_dtoIsNull() {
        // when & then
        assertThatThrownBy(() -> DtoValidator.validate(null))
                .isInstanceOf(DtoValidationException.class)
                .hasMessage("DTO must not be null");
    }

    @Test
    @DisplayName("Test of the method validate - should throw an exception when a required field is null")
    void testValidate_negative_requiredFieldIsNull() {
        // given
        var testDto = new TestDto(null, "optional value");

        // when & then
        assertThatThrownBy(() -> DtoValidator.validate(testDto))
                .isInstanceOf(DtoValidationException.class)
                .hasMessage("Property 'requiredField' is required");
    }

    private record TestDto(
            @Required String requiredField,
            String optionalField
    ) {}

}