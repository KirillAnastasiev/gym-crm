package com.kirill.projects.gymcrm.app.util;

import com.kirill.projects.gymcrm.app.dto.annotation.Required;
import com.kirill.projects.gymcrm.app.exception.DtoValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("DtoValidator test suite")
class DtoValidatorTest {


    // ==================== VALIDATE TESTS ====================

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
                .isInstanceOf(IllegalArgumentException.class)
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