package com.epam.laboratory.app.repository;


import com.epam.laboratory.app.domain.Trainee;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeDaoImplTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private TraineeDaoImpl traineeDao;

    @Test
    @DisplayName("Test of the method findById - should return trainee wrapped in Optional when trainee with given ID exists")
    void testFindById_positive() {
        // given
        var trainee = getTestTrainee();

        given(em.find(eq(Trainee.class), anyLong())).willReturn(trainee);

        // when
        var actualResult = traineeDao.findById(trainee.getId(), Trainee.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainee);

        verify(em, times(1)).find(eq(Trainee.class), anyLong());
        verifyNoMoreInteractions(em);
    }

    @Test
    @DisplayName("Test of the method findById - should return empty Optional when trainee with given ID does not exist")
    void testFindById_negative() {
        // given
        given(em.find(eq(Trainee.class), anyLong())).willReturn(null);

        // when
        var actualResult = traineeDao.findById(1L, Trainee.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(em, times(1)).find(eq(Trainee.class), anyLong());
        verifyNoMoreInteractions(em);
    }

    @Test
    @DisplayName("Test of the method findByCondition - should return list of trainees matching condition")
    void testFindByCondition_positive() {
        // given
        var trainee = getTestTrainee();

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Trainee> mockCriteriaQuery = mock(CriteriaQuery.class);
        Root<Trainee> mockRoot = mock(Root.class);
        TypedQuery<Trainee> mockTypedQuery = mock(TypedQuery.class);

        given(em.getCriteriaBuilder()).willReturn(mockCriteriaBuilder);
        given(mockCriteriaBuilder.createQuery(Trainee.class)).willReturn(mockCriteriaQuery);
        given(mockCriteriaQuery.from(Trainee.class)).willReturn(mockRoot);
        given(em.createQuery(any(CriteriaQuery.class))).willReturn(mockTypedQuery);
        given(mockCriteriaQuery.select(mockRoot)).willReturn(mockCriteriaQuery);
        given(mockTypedQuery.getResultList()).willReturn(Collections.singletonList(trainee));

        // when
        var actualResult = traineeDao.findByCondition((cb, root) ->
                cb.equal(root.get("firstName"), "FirstName"), Trainee.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).contains(trainee);

        verify(em, times(1)).getCriteriaBuilder();
        verify(em, times(1)).createQuery(any(CriteriaQuery.class));
        verify(mockCriteriaBuilder, times(1)).createQuery(Trainee.class);
        verify(mockCriteriaQuery, times(1)).from(Trainee.class);
        verify(mockCriteriaQuery, times(1)).select(mockRoot);
        verify(mockTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Test of the method findByCondition - should return empty list when no trainees match the condition")
    void testFindByCondition_negative_noMatchingTrainees() {
        // given
        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Trainee> mockCriteriaQuery = mock(CriteriaQuery.class);
        Root<Trainee> mockRoot = mock(Root.class);
        TypedQuery<Trainee> mockTypedQuery = mock(TypedQuery.class);

        given(em.getCriteriaBuilder()).willReturn(mockCriteriaBuilder);
        given(mockCriteriaBuilder.createQuery(Trainee.class)).willReturn(mockCriteriaQuery);
        given(mockCriteriaQuery.from(Trainee.class)).willReturn(mockRoot);
        given(em.createQuery(any(CriteriaQuery.class))).willReturn(mockTypedQuery);
        given(mockCriteriaQuery.select(mockRoot)).willReturn(mockCriteriaQuery);
        given(mockTypedQuery.getResultList()).willReturn(Collections.emptyList());

        // when
        var actualResult = traineeDao.findByCondition((cb, root) -> cb.equal(root.get("firstName"), "NonExistentFirstName"), Trainee.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(em, times(1)).getCriteriaBuilder();
        verify(em, times(1)).createQuery(any(CriteriaQuery.class));
        verify(mockCriteriaBuilder, times(1)).createQuery(Trainee.class);
        verify(mockCriteriaQuery, times(1)).from(Trainee.class);
        verify(mockCriteriaQuery, times(1)).select(mockRoot);
        verify(mockTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Test of the method findByUsername - should return trainee wrapped in Optional when trainee with given username exists")
    void testFindByUsername_positive() {
        // given
        var trainee = getTestTrainee();

        TypedQuery<Trainee> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(Trainee.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(eq("username"), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.getSingleResult()).willReturn(trainee);

        // when
        var actualResult = traineeDao.findByUsername(trainee.getUsername());

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainee);

        verify(em, times(1)).createQuery(anyString(), eq(Trainee.class));
        verify(mockTypedQuery, times(1)).setParameter(eq("username"), anyString());
        verify(mockTypedQuery, times(1)).getSingleResult();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    @Test
    @DisplayName("Test of the method findByUsername - should return empty Optional when trainee with given username does not exist")
    void testFindByUsername_negative() {
        // given
        TypedQuery<Trainee> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(Trainee.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(eq("username"), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.getSingleResult()).willThrow(new NoResultException());

        // when
        var actualResult = traineeDao.findByUsername("NonExistentUsername");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(em, times(1)).createQuery(anyString(), eq(Trainee.class));
        verify(mockTypedQuery, times(1)).setParameter(eq("username"), anyString());
        verify(mockTypedQuery, times(1)).getSingleResult();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    @Test
    @DisplayName("Test of the method save - should persist trainee and return it")
    void testSave() {
        // given
        var trainee = getTestTrainee();
        trainee.setId(null);

        doNothing().when(em).persist(any(Trainee.class));

        // when
        var actualResult = traineeDao.save(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);

        verify(em, times(1)).persist(any(Trainee.class));
        verifyNoMoreInteractions(em);
    }

    @Test
    @DisplayName("Test of the method update - should execute update query and return updated trainee when trainee with given username exists")
    void testUpdate() {
        // given
        var trainee = getTestTrainee();

        TypedQuery<Trainee> mockTypedQuery = mock(TypedQuery.class);
        TypedQuery<Trainee> mockTypedQuery2 = mock(TypedQuery.class);

        given(em.createQuery(anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), any())).willReturn(mockTypedQuery);
        given(mockTypedQuery.executeUpdate()).willReturn(1);
        given(em.createQuery(anyString(), eq(Trainee.class))).willReturn(mockTypedQuery2);
        given(mockTypedQuery2.setParameter(anyString(), anyString())).willReturn(mockTypedQuery2);
        given(mockTypedQuery2.getSingleResult()).willReturn(trainee);

        // when
        var actualResult = traineeDao.update(trainee);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainee);

        verify(em, times(1)).createQuery(anyString());
        verify(mockTypedQuery, times(6)).setParameter(anyString(), any());
        verify(mockTypedQuery, times(1)).executeUpdate();
        verify(em, times(1)).createQuery(anyString(), eq(Trainee.class));
        verify(mockTypedQuery2, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery2, times(1)).getSingleResult();
        verifyNoMoreInteractions(em, mockTypedQuery, mockTypedQuery2);
    }

    @Test
    @DisplayName("Test of the method changeStatus - should execute update query to change status of trainee with given ID")
    void testChangeStatusByUsername() {
        // given
        var username = "FirstName.LastName";
        var newStatus = false;

        TypedQuery<Trainee> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyBoolean())).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.executeUpdate()).willReturn(1);

        // when
        traineeDao.changeStatusByUsername(username, newStatus);

        // then
        verify(em, times(1)).createQuery(anyString());
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyBoolean());
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).executeUpdate();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    @Test
    @DisplayName("Test of the method deleteByUsername - should execute delete query to delete trainee with given username")
    void testDeleteByUsername() {
        // given
        var username = "FirstName.LastName";

        TypedQuery<Trainee> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.executeUpdate()).willReturn(1);

        // when
        traineeDao.deleteByUsername(username);

        // then
        verify(em, times(1)).createQuery(anyString());
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).executeUpdate();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    private Trainee getTestTrainee() {
        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");
        trainee.setUsername("FirstName.LastName");
        trainee.setPassword("password");
        trainee.setAddress("Test Address");
        trainee.setDateOfBirth(java.time.LocalDate.now());
        trainee.setActive(true);
        return trainee;
    }

}