package com.epam.laboratory.app.util;

import com.epam.laboratory.app.dto.annotation.Sensitive;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        JavaTimeModule.class,
        ObjectMapper.class,
        SensitiveDataMasker.class
})
@DisplayName("SensitiveDataMasker test suite")
class SensitiveDataMaskerTest {

    @Autowired
    private SensitiveDataMasker sensitiveDataMasker;


    // ==================== MASK SENSITIVE DATA TESTS ====================

    @Test
    @DisplayName("Test maskSensitiveData - should mask sensitive data in object")
    void testMaskSensitiveData_positive_object() {
        // given
        var testData = new TestData("John Doe", "johndoe@test.com", "password123");
        String expectedResult = """
                     {"name":"John Doe","email":"johndoe@test.com","password":"***********"}""";

        // when
        var maskedData = sensitiveDataMasker.maskSensitiveData(testData);
        var actualResult = maskedData.toString();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Test maskSensitiveData - should mask sensitive data in array of objects")
    void testMaskSensitiveData_positive_arrayOfObjects() {
        // given
        var testDataArray = new TestData[]{
                new TestData("John Doe", "johndoe@test.com", "password123"),
                new TestData("Jane Doe", "janedoe@test.com", "password456")
        };
        var expectedResult = """
                     [TestData[name=John Doe, email=johndoe@test.com, password=***********], TestData[name=Jane Doe, email=janedoe@test.com, password=***********]]""";

        // when
        var maskedData = sensitiveDataMasker.maskSensitiveData(testDataArray);
        var actualResult = Arrays.toString((TestData[]) maskedData);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Test maskSensitiveData - should mask sensitive data in collection of objects")
    void testMaskSensitiveData_positive_collectionOfObjects() {
        // given
        var testDataList = List.of(
                new TestData("John Doe", "johndoe@test.com", "password123"),
                new TestData("Jane Doe", "janedoe@test.com", "password456")
        );
        var expectedResult = """
                     [{"name":"John Doe","email":"johndoe@test.com","password":"***********"}, {"name":"Jane Doe","email":"janedoe@test.com","password":"***********"}]""";

        // when
        var maskedData = sensitiveDataMasker.maskSensitiveData(testDataList);
        var actualResult = maskedData.toString();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Test maskSensitiveData - should return null when input object is null")
    void testMaskSensitiveData_negative_nullObject() {
        // when
        var maskedData = sensitiveDataMasker.maskSensitiveData(null);

        // then
        assertThat(maskedData).isNull();
    }

    @Test
    @DisplayName("Test maskSensitiveData - should handle null sensitive field in object")
    void testMaskSensitiveData_negative_objectWithNullSensitiveField() {
        // given
        var testData = new TestData("John Doe", "johndoe@test.com", null);
        String expectedResult = """
                     {"name":"John Doe","email":"johndoe@test.com","password":null}""";

        // when
        var maskedData = sensitiveDataMasker.maskSensitiveData(testData);
        var actualResult = maskedData.toString();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Test maskSensitiveData - should return the same string when input is a string")
    void testMaskSensitiveData_negative_nonObject() {
        // given
        String input = "This is a string";

        // when
        var maskedData = sensitiveDataMasker.maskSensitiveData(input);

        // then
        assertThat(maskedData).isNotNull();
        assertThat(maskedData).isEqualTo(input);
    }

    @Test
    @DisplayName("Test maskSensitiveData - should return the same array when input is a non-object array")
    void testMaskSensitiveData_negative_nonObjectArray() {
        // given
        String[] input = {"This is a string", "Another string"};

        // when
        var maskedData = sensitiveDataMasker.maskSensitiveData(input);

        // then
        assertThat(maskedData).isNotNull();
        assertThat(maskedData).isInstanceOf(String[].class);
        assertThat(maskedData).isEqualTo(input);
    }

    @Test
    @DisplayName("Test maskSensitiveData - should return the same collection when input is a non-object collection")
    void testMaskSensitiveData_negative_nonObjectCollection() {
        // given
        var input = List.of("This is a string", "Another string");

        // when
        var maskedData = sensitiveDataMasker.maskSensitiveData(input);

        // then
        assertThat(maskedData).isNotNull();
        assertThat(maskedData).isInstanceOf(Collection.class);
        assertThat(maskedData).isEqualTo(input);
    }

    @Test
    @DisplayName("Test maskSensitiveData - should return the same map when input is a non-object map")
    void testMaskSensitiveData_negative_nonObjectMap() {
        // given
        var input = Map.of("key1", "value1", "key2", "value2");

        // when
        var maskedData = sensitiveDataMasker.maskSensitiveData(input);

        // then
        assertThat(maskedData).isNotNull();
        assertThat(maskedData).isInstanceOf(Map.class);
        assertThat(maskedData).isEqualTo(input);
    }

    @Test
    @DisplayName("Test maskSensitiveData - should return the same primitive when input is a non-object primitive")
    void testMaskSensitiveData_negative_primitiveValue() {
        // given
        int input = 42;

        // when
        var maskedData = sensitiveDataMasker.maskSensitiveData(input);

        // then
        assertThat(maskedData).isNotNull();
        assertThat(maskedData).isInstanceOf(Integer.class);
        assertThat(maskedData).isEqualTo(input);
    }

    @Test
    @DisplayName("Test maskSensitiveData - should return the same primitive wrapper when input is a non-object primitive wrapper")
    void testMaskSensitiveData_negative_primitiveWrapperValue() {
        // given
        Integer input = 42;

        // when
        var maskedData = sensitiveDataMasker.maskSensitiveData(input);

        // then
        assertThat(maskedData).isNotNull();
        assertThat(maskedData).isInstanceOf(Integer.class);
        assertThat(maskedData).isEqualTo(input);
    }

    @Test
    @DisplayName("Test maskSensitiveData - should return the same string when input is a non-object string")
    void testMaskSensitiveDate_negative_stringValue() {
        // given
        String input = "2024-06-01";

        // when
        var maskedData = sensitiveDataMasker.maskSensitiveData(input);

        // then
        assertThat(maskedData).isNotNull();
        assertThat(maskedData).isInstanceOf(String.class);
        assertThat(maskedData).isEqualTo(input);
    }

    record TestData(
            String name,
            String email,
            @Sensitive String password) {
    }
}