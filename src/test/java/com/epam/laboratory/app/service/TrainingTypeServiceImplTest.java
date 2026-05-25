package com.epam.laboratory.app.service;

import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.repository.TrainingTypeDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingTypeServiceImpl test suite")
class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;


    // ==================== REGISTER NEW TESTS ====================

    @Test
    @DisplayName("Test of the method registerNew - should throw UnsupportedOperationException")
    void testRegisterNew() {
        // when & then
        assertThatThrownBy(() -> trainingTypeService.registerNew(new TrainingType()))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Unavailable operation for training type");

        verifyNoInteractions(trainingTypeDao);
    }


    // ==================== UPDATE TESTS ====================

    @Test
    @DisplayName("Test of the method update - should throw UnsupportedOperationException")
    void testUpdate() {
        // when & then
        assertThatThrownBy(() -> trainingTypeService.update(new TrainingType()))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Unavailable operation for training type");

        verifyNoInteractions(trainingTypeDao);
    }


    // ==================== SELECT ALL TESTS ====================

    @Test
    @DisplayName("Test of the method selectAll - should return collection of training types")
    void testSelectAll_positive() {
        // given
        var trainingType = getTestTrainingType();

        given(trainingTypeDao.findAll()).willReturn(Collections.singletonList(trainingType));

        // when
        var actualResult = trainingTypeService.selectAll();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        actualResult.forEach(tt -> {
            assertThat(tt).isInstanceOf(TrainingType.class);
            assertThat(tt).isEqualTo(trainingType);
        });

        verify(trainingTypeDao, times(1)).findAll();
        verifyNoMoreInteractions(trainingTypeDao);
    }

    @Test
    @DisplayName("Test of the method selectAll - should return empty collection if there are no training types")
    void testSelectAll_negative() {
        // given
        given(trainingTypeDao.findAll()).willReturn(Collections.emptyList());

        // when
        var actualResult = trainingTypeService.selectAll();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(trainingTypeDao, times(1)).findAll();
        verifyNoMoreInteractions(trainingTypeDao);
    }


    // ==================== SELECT BY TRAINING TYPE NAME TESTS ====================

    @Test
    @DisplayName("Test of the method selectByTrainingTypeName - should return training type if it exists")
    void testSelectByTrainingTypeName_positive() {
        // given
        var trainingType = getTestTrainingType();
        var trainingTypeName = "Test Training Type";

        given(trainingTypeDao.findByTrainingTypeName(trainingTypeName)).willReturn(Optional.of(trainingType));

        // when
        var actualResult = trainingTypeService.selectByTrainingTypeName(trainingTypeName);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(TrainingType.class);
        assertThat(actualResult).isEqualTo(trainingType);

        verify(trainingTypeDao, times(1)).findByTrainingTypeName(trainingTypeName);
        verifyNoMoreInteractions(trainingTypeDao);
    }

    @Test
    @DisplayName("Test of the method selectByTrainingTypeName - should return null if training type with given name does not exist")
    void testSelectByTrainingTypeName_negative_notExistedTrainingType() {
        // given
        var trainingTypeName = "Non-existent Training Type";

        given(trainingTypeDao.findByTrainingTypeName(trainingTypeName)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> trainingTypeService.selectByTrainingTypeName(trainingTypeName))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessage("Training type with name " + trainingTypeName + " not found");

        verify(trainingTypeDao, times(1)).findByTrainingTypeName(trainingTypeName);
        verifyNoMoreInteractions(trainingTypeDao);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, Training type name must not be null",
            "'', Training type name must not be blank",
            "'   ', Training type name must not be blank"
    }, nullValues = "NULL")
    @DisplayName("Test of the method selectByTrainingTypeName - should throw IllegalArgumentException if training type name is invalid")
    void testSelectByTrainingTypeName_negative_invalidInput(String trainingTypeName, String errorMessage) {
        // when & then
        assertThatThrownBy(() -> trainingTypeService.selectByTrainingTypeName(trainingTypeName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);

        verifyNoInteractions(trainingTypeDao);
    }

    private static TrainingType getTestTrainingType() {
        var trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Test Training Type");
        return trainingType;
    }

}