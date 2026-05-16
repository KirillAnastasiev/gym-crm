package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingTypeDaoImpl test suite")
class TrainingTypeDaoImplTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private TrainingTypeDaoImpl dao;


    // ==================== FIND BY ID TESTS ====================

    @Test
    @DisplayName("Test of the method findById - should return Optional with TrainingType when entity exists")
    void testFindById_positive() {
        // given
        var trainingType = getTestTrainingType();

        given(em.find(eq(TrainingType.class), anyLong())).willReturn(trainingType);

        // when
        var actualResult = dao.findById(1L, TrainingType.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainingType);

        verify(em, times(1)).find(eq(TrainingType.class), anyLong());
        verifyNoMoreInteractions(em);
    }

    @Test
    @DisplayName("Test of the method findById - should return empty Optional when entity does not exist")
    void testFindById_negative() {
        // given
        given(em.find(eq(TrainingType.class), anyLong())).willReturn(null);

        // when
        var actualResult = dao.findById(1L, TrainingType.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(em, times(1)).find(eq(TrainingType.class), anyLong());
        verifyNoMoreInteractions(em);
    }


    // ==================== FIND BY CONDITION TESTS ====================

    @Test
    @DisplayName("Test of the method findByCondition - should return collection with TrainingType when entity exists")
    void testFindByCondition_positive() {
        // given
        var trainingType = getTestTrainingType();

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<TrainingType> mockCriteriaQuery = mock(CriteriaQuery.class);
        Root<TrainingType> mockRoot = mock(Root.class);
        TypedQuery<TrainingType> mockTypedQuery = mock(TypedQuery.class);

        given(em.getCriteriaBuilder()).willReturn(mockCriteriaBuilder);
        given(mockCriteriaBuilder.createQuery(TrainingType.class)).willReturn(mockCriteriaQuery);
        given(mockCriteriaQuery.from(TrainingType.class)).willReturn(mockRoot);
        given(mockCriteriaQuery.select(mockRoot)).willReturn(mockCriteriaQuery);
        given(em.createQuery(mockCriteriaQuery)).willReturn(mockTypedQuery);
        given(mockTypedQuery.getResultList()).willReturn(Collections.singletonList(trainingType));

        // when
        var actualResult = dao.findByCondition((cb, root) -> cb.equal(root.get("trainingTypeName"), "Test Training Type"), TrainingType.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).contains(trainingType);

        verify(em, times(1)).getCriteriaBuilder();
        verify(mockCriteriaBuilder, times(1)).createQuery(TrainingType.class);
        verify(mockCriteriaQuery, times(1)).from(TrainingType.class);
        verify(mockCriteriaQuery, times(1)).select(mockRoot);
        verify(em, times(1)).createQuery(mockCriteriaQuery);
        verify(mockTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Test of the method findByCondition - should return empty collection when entity does not exist")
    void testFindByCondition_negative_notMatchingTrainingType() {
        // given
        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<TrainingType> mockCriteriaQuery = mock(CriteriaQuery.class);
        Root<TrainingType> mockRoot = mock(Root.class);
        TypedQuery<TrainingType> mockTypedQuery = mock(TypedQuery.class);

        given(em.getCriteriaBuilder()).willReturn(mockCriteriaBuilder);
        given(mockCriteriaBuilder.createQuery(TrainingType.class)).willReturn(mockCriteriaQuery);
        given(mockCriteriaQuery.from(TrainingType.class)).willReturn(mockRoot);
        given(mockCriteriaQuery.select(mockRoot)).willReturn(mockCriteriaQuery);
        given(em.createQuery(mockCriteriaQuery)).willReturn(mockTypedQuery);
        given(mockTypedQuery.getResultList()).willReturn(Collections.emptyList());

        // when
        var actualResult = dao.findByCondition((cb, root) ->
                cb.equal(root.get("trainingTypeName"), "Nonexistent Training Type"), TrainingType.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(em, times(1)).getCriteriaBuilder();
        verify(mockCriteriaBuilder, times(1)).createQuery(TrainingType.class);
        verify(mockCriteriaQuery, times(1)).from(TrainingType.class);
        verify(mockCriteriaQuery, times(1)).select(mockRoot);
        verify(em, times(1)).createQuery(mockCriteriaQuery);
        verify(mockTypedQuery, times(1)).getResultList();
    }


    // ==================== FIND ALL TESTS ====================

    @Test
    @DisplayName("Test of the method findAll - should return collection with TrainingType when entities exist")
    void testFindAll_positive() {
        // given
        var trainingType = getTestTrainingType();

        TypedQuery<TrainingType> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(TrainingType.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.getResultList()).willReturn(Collections.singletonList(trainingType));

        // when
        var actualResult = dao.findAll();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).contains(trainingType);

        verify(em, times(1)).createQuery(anyString(), eq(TrainingType.class));
        verify(mockTypedQuery, times(1)).getResultList();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    @Test
    @DisplayName("Test of the method findAll - should return empty collection when no entities exist")
    void testFindAll_negative() {
        // given
        TypedQuery<TrainingType> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(TrainingType.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.getResultList()).willReturn(Collections.emptyList());

        // when
        var actualResult = dao.findAll();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(em, times(1)).createQuery(anyString(), eq(TrainingType.class));
        verify(mockTypedQuery, times(1)).getResultList();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }


    // ==================== FIND BY TRAINING TYPE NAME TESTS ====================

    @Test
    @DisplayName("Test of the method findByTrainingTypeName - should return Optional with TrainingType when entity exists")
    void testFindByTrainingTypeName_positive() {
        // given
        var trainingType = getTestTrainingType();

        TypedQuery<TrainingType> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(TrainingType.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.getSingleResult()).willReturn(trainingType);

        // when
        var actualResult = dao.findByTrainingTypeName("Test Training Type");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainingType);

        verify(em, times(1)).createQuery(anyString(), eq(TrainingType.class));
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).getSingleResult();
    }

    @Test
    @DisplayName("Test of the method findByTrainingTypeName - should return empty Optional when entity does not exist")
    void testFindByTrainingTypeName_negative_notExistingTrainingType() {
        // given
        TypedQuery<TrainingType> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(TrainingType.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.getSingleResult()).willThrow(new NoResultException());

        // when
        var actualResult = dao.findByTrainingTypeName("Nonexistent Training Type");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(em, times(1)).createQuery(anyString(), eq(TrainingType.class));
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).getSingleResult();
    }


    // ==================== SAVE TESTS ====================

    @Test
    @DisplayName("Test of the method save - should throw UnsupportedOperationException when trying to save a TrainingType")
    void testSave_negative_notSupportedOperation() {
        // given
        var trainingType = getTestTrainingType();

        // when & then
        assertThatThrownBy(() -> dao.save(trainingType))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not supported operation.");

        verifyNoInteractions(em);
    }


    // ==================== UPDATE TESTS ====================

    @Test
    @DisplayName("Test of the method update - should throw UnsupportedOperationException when trying to update a TrainingType")
    void testUpdate_negative_notSupportedOperation() {
        // given
        var trainingType = getTestTrainingType();

        // when & then
        assertThatThrownBy(() -> dao.update(trainingType))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not supported operation.");

        verifyNoInteractions(em);
    }

    private static TrainingType getTestTrainingType() {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Test Training Type");
        return trainingType;
    }

}