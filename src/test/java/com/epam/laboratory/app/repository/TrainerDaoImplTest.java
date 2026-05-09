package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerDaoImplTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private TrainerDaoImpl trainerDao;

    @Test
    @DisplayName("Test of the method findById - should return trainer wrapped in Optional when trainer with given ID exists")
    void testFindById_positive() {
        // given
        var trainer = getTestTrainer();

        given(em.find(eq(Trainer.class), anyLong())).willReturn(trainer);

        // when
        var actualResult = trainerDao.findById(trainer.getId(), Trainer.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainer);

        verify(em, times(1)).find(eq(Trainer.class), anyLong());
        verifyNoMoreInteractions(em);
    }

    @Test
    @DisplayName("Test of the method findById - should return empty Optional when trainer with given ID does not exist")
    void testFindById_negative() {
        // given
        given(em.find(eq(Trainer.class), anyLong())).willReturn(null);

        // when
        var actualResult = trainerDao.findById(1L, Trainer.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();

        verify(em, times(1)).find(eq(Trainer.class), anyLong());
        verifyNoMoreInteractions(em);
    }

    @Test
    @DisplayName("Test of the method findByCondition - should return collection with trainer when trainer with given condition exists")
    void testFindByCondition_positive() {
        // given
        var trainer = getTestTrainer();

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Trainer> mockCriteriaQuery = mock(CriteriaQuery.class);
        Root<Trainer> mockRoot = mock(Root.class);
        TypedQuery<Trainer> mockTypedQuery = mock(TypedQuery.class);

        given(em.getCriteriaBuilder()).willReturn(mockCriteriaBuilder);
        given(mockCriteriaBuilder.createQuery(Trainer.class)).willReturn(mockCriteriaQuery);
        given(mockCriteriaQuery.from(Trainer.class)).willReturn(mockRoot);
        given(em.createQuery(mockCriteriaQuery)).willReturn(mockTypedQuery);
        given(mockCriteriaQuery.select(mockRoot)).willReturn(mockCriteriaQuery);
        given(mockTypedQuery.getResultList()).willReturn(Collections.singletonList(trainer));

        // when
        var actualResult = trainerDao.findByCondition((cb, root) -> cb.equal(root.get("username"), "FirstName.LastName"), Trainer.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(1);

        verify(em, times(1)).getCriteriaBuilder();
        verify(mockCriteriaBuilder, times(1)).createQuery(Trainer.class);
        verify(mockCriteriaQuery, times(1)).from(Trainer.class);
        verify(em, times(1)).createQuery(mockCriteriaQuery);
        verify(mockCriteriaQuery, times(1)).select(mockRoot);
        verify(mockTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Test of the method findByCondition - should return empty collection when trainer with given condition does not exist")
    void testFindByCondition_negative_noMatchingTrainers() {
        // given
        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Trainer> mockCriteriaQuery = mock(CriteriaQuery.class);
        Root<Trainer> mockRoot = mock(Root.class);
        TypedQuery<Trainer> mockTypedQuery = mock(TypedQuery.class);

        given(em.getCriteriaBuilder()).willReturn(mockCriteriaBuilder);
        given(mockCriteriaBuilder.createQuery(Trainer.class)).willReturn(mockCriteriaQuery);
        given(mockCriteriaQuery.from(Trainer.class)).willReturn(mockRoot);
        given(em.createQuery(mockCriteriaQuery)).willReturn(mockTypedQuery);
        given(mockCriteriaQuery.select(mockRoot)).willReturn(mockCriteriaQuery);
        given(mockTypedQuery.getResultList()).willReturn(Collections.emptyList());

        // when
        var actualResult = trainerDao.findByCondition((cb, root) ->
                cb.equal(root.get("username"), "NonExistentUsername"), Trainer.class);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();

        verify(em, times(1)).getCriteriaBuilder();
        verify(mockCriteriaBuilder, times(1)).createQuery(Trainer.class);
        verify(mockCriteriaQuery, times(1)).from(Trainer.class);
        verify(em, times(1)).createQuery(mockCriteriaQuery);
        verify(mockCriteriaQuery, times(1)).select(any());
        verify(mockTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Test of the method findByUsername - should return trainer wrapped in Optional when trainer with given username exists")
    void testFindByUsername_positive() {
        // given
        var trainer = getTestTrainer();

        TypedQuery<Trainer> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(Trainer.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mock(TypedQuery.class));
        given(mockTypedQuery.getSingleResult()).willReturn(trainer);

        // when
        var actualResult = trainerDao.findByUsername(trainer.getUsername());

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult).contains(trainer);

        verify(em, times(1)).createQuery(anyString(), eq(Trainer.class));
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).getSingleResult();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    @Test
    @DisplayName("Test of the method findByUsername - should return empty Optional when trainer with given username does not exist")
    void testFindByUsername_negative() {
        // given
        TypedQuery<Trainer> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(Trainer.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mock(TypedQuery.class));
        given(mockTypedQuery.getSingleResult()).willThrow(new NoResultException());

        // when
        var actualResult = trainerDao.findByUsername("NonExistentUsername");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
        verify(em, times(1)).createQuery(anyString(), eq(Trainer.class));
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).getSingleResult();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    @Test
    @DisplayName("Test of the method save - should persist trainer and return it")
    void testSave() {
        // given
        var trainer = getTestTrainer();
        trainer.setId(null);

        doNothing().when(em).persist(any(Trainer.class));

        // when
        var actualResult = trainerDao.save(trainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainer);

        verify(em, times(1)).persist(any(Trainer.class));
        verifyNoMoreInteractions(em);
    }

    @Test
    @DisplayName("Test of the method update - should update trainer and return updated trainer when trainer with given username exists")
    void testUpdate() {
        // given
        var trainer = getTestTrainer();

        TypedQuery<Trainer> mockTypedQuery = mock(TypedQuery.class);
        TypedQuery<Trainer> mockTypedQuery2 = mock(TypedQuery.class);

        given(em.createQuery(anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), any())).willReturn(mockTypedQuery);
        given(mockTypedQuery.executeUpdate()).willReturn(1);
        given(em.createQuery(anyString(), eq(Trainer.class))).willReturn(mockTypedQuery2);
        given(mockTypedQuery2.setParameter(anyString(), anyString())).willReturn(mockTypedQuery2);
        given(mockTypedQuery2.getSingleResult()).willReturn(trainer);

        // when
        var actualResult = trainerDao.update(trainer);

        // then
        AssertionsForClassTypes.assertThat(actualResult).isNotNull();
        AssertionsForClassTypes.assertThat(actualResult).isEqualTo(trainer);

        verify(em, times(1)).createQuery(anyString());
        verify(mockTypedQuery, times(5)).setParameter(anyString(), any());
        verify(mockTypedQuery, times(1)).executeUpdate();
        verify(em, times(1)).createQuery(anyString(), eq(Trainer.class));
        verify(mockTypedQuery2, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery2, times(1)).getSingleResult();
        verifyNoMoreInteractions(em, mockTypedQuery, mockTypedQuery2);
    }

    @Test
    @DisplayName("Test of the method changeStatus - should change status of trainer when trainer with given ID exists")
    void testChangeStatusByUsername() {
        // given
        var username = "FirstName.LastName";
        var isActive = false;

        TypedQuery<Trainer> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyBoolean())).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.executeUpdate()).willReturn(1);

        // when
        trainerDao.changeStatusByUsername(username, isActive);

        // then
        verify(em, times(1)).createQuery(anyString());
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyBoolean());
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).executeUpdate();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    @Test
    @DisplayName("Test of the method deleteByUsername - should delete trainer when trainer with given username exists")
    void testDeleteByUsername() {
        // given
        var username = "FirstName.LastName";

        TypedQuery<Trainer> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.executeUpdate()).willReturn(1);

        // when
        trainerDao.deleteByUsername(username);

        // then
        verify(em, times(1)).createQuery(anyString());
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).executeUpdate();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    private Trainer getTestTrainer() {
        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("FirstName");
        trainer.setLastName("LastName");
        trainer.setUsername("FirstName.LastName");
        trainer.setPassword("password");
        return trainer;
    }
}