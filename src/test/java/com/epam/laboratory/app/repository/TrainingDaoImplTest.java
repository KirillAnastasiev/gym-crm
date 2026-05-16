package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Training;
import jakarta.persistence.EntityManager;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingDaoImpl test suite")
class TrainingDaoImplTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private  TrainingDaoImpl trainingDao;


    // ==================== FIND BY ID TESTS ====================

    @Test
    @DisplayName("Test of the method findById - should return training wrapped in Optional when training with given ID exists")
    void testFindById_positive() {
        // given
        var training = getTestTraining();

        given(em.find(eq(Training.class), anyLong())).willReturn(training);

        // when
        var actualResult = trainingDao.findById(training.getId(), Training.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(training);

        verify(em, times(1)).find(eq(Training.class), anyLong());
        verifyNoMoreInteractions(em);
    }

    @Test
    @DisplayName("Test of the method findById - should return empty Optional when training with given ID does not exist")
    void testFindById_negative() {
        // given
        given(em.find(eq(Training.class), anyLong())).willReturn(null);

        // when
        var actualResult = trainingDao.findById(1L, Training.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(em, times(1)).find(eq(Training.class), anyLong());
        verifyNoMoreInteractions(em);
    }


    // ==================== FIND BY CONDITION TESTS ====================

    @Test
    @DisplayName("Test of the method findByCondition - should return collection with training when training with given condition exists")
    void testFindByCondition_positive() {
        // given
        var training = getTestTraining();

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Training> mockCriteriaQuery = mock(CriteriaQuery.class);
        Root<Training> mockRoot = mock(Root.class);
        TypedQuery<Training> mockTypedQuery = mock(TypedQuery.class);

        given(em.getCriteriaBuilder()).willReturn(mockCriteriaBuilder);
        given(mockCriteriaBuilder.createQuery(Training.class)).willReturn(mockCriteriaQuery);
        given(mockCriteriaQuery.from(Training.class)).willReturn(mockRoot);
        given(mockCriteriaQuery.select(mockRoot)).willReturn(mockCriteriaQuery);
        given(em.createQuery(mockCriteriaQuery)).willReturn(mockTypedQuery);
        given(mockTypedQuery.getResultList()).willReturn(Collections.singletonList(training));

        // when
        var actualResult = trainingDao.findByCondition((cb, root) ->
                cb.equal(root.get("trainingName"), "Test Training"), Training.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).contains(training);

        verify(em, times(1)).getCriteriaBuilder();
        verify(mockCriteriaBuilder, times(1)).createQuery(Training.class);
        verify(mockCriteriaQuery, times(1)).from(Training.class);
        verify(mockCriteriaQuery, times(1)).select(mockRoot);
        verify(em, times(1)).createQuery(mockCriteriaQuery);
        verify(mockTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Test of the method findByCondition - should return empty collection when training with given condition does not exist")
    void testFindByCondition_negative_noMatchingTraining() {
        // given
        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Training> mockCriteriaQuery = mock(CriteriaQuery.class);
        Root<Training> mockRoot = mock(Root.class);
        TypedQuery<Training> mockTypedQuery = mock(TypedQuery.class);

        given(em.getCriteriaBuilder()).willReturn(mockCriteriaBuilder);
        given(mockCriteriaBuilder.createQuery(Training.class)).willReturn(mockCriteriaQuery);
        given(mockCriteriaQuery.from(Training.class)).willReturn(mockRoot);
        given(mockCriteriaQuery.select(mockRoot)).willReturn(mockCriteriaQuery);
        given(em.createQuery(mockCriteriaQuery)).willReturn(mockTypedQuery);
        given(mockTypedQuery.getResultList()).willReturn(Collections.emptyList());

        // when
        var actualResult = trainingDao.findByCondition((cb, root) -> cb.equal(root.get("trainingName"), "Non-existent Training"), Training.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(em, times(1)).getCriteriaBuilder();
        verify(mockCriteriaBuilder, times(1)).createQuery(Training.class);
        verify(mockCriteriaQuery, times(1)).from(Training.class);
        verify(mockCriteriaQuery, times(1)).select(mockRoot);
        verify(em, times(1)).createQuery(mockCriteriaQuery);
        verify(mockTypedQuery, times(1)).getResultList();
    }


    // ==================== SAVE TESTS ====================

    @Test
    @DisplayName("Test of the method save - should persist training and return it")
    void testSave_positive() {
        // given
        var training = getTestTraining();
        training.setId(null);

        doNothing().when(em).persist(any(Training.class));

        // when
        var actualResult = trainingDao.save(training);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(training);

        verify(em, times(1)).persist(any(Training.class));
        verifyNoMoreInteractions(em);
    }

    private static Training getTestTraining() {
        var training = new Training();
        training.setId(1L);
        training.setTrainingName("Test Training");
        training.setTrainingDate(LocalDateTime.now());
        training.setTrainingDuration(Duration.ofHours(1));
        return training;
    }
}

