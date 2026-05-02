package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainer;
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
        given(mockCriteriaQuery.select(eq(mockRoot))).willReturn(mockCriteriaQuery);
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
        verify(mockCriteriaQuery, times(1)).select(eq(mockRoot));
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
        given(mockCriteriaQuery.select(eq(mockRoot))).willReturn(mockCriteriaQuery);
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
    @DisplayName("Test of the method save - should persist trainer and return it when trainer with given ID does not exist")
    void testSave_positive_notExistedTrainer() {
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
    @DisplayName("Test of the method save - should merge trainer and return it when trainer with given ID exists")
    void testSave_positive_existedTrainer() {
        // given
        var trainer = getTestTrainer();

        given(em.find(eq(Trainer.class), anyLong())).willReturn(trainer);
        given(em.merge(any(Trainer.class))).willReturn(trainer);

        // when
        var actualResult = trainerDao.save(trainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainer);

        verify(em, times(1)).find(eq(Trainer.class), anyLong());
        verify(em, times(1)).merge(any(Trainer.class));
        verifyNoMoreInteractions(em);
    }

    @Test
    @DisplayName("Test of the method save - should throw IllegalArgumentException when trainer is null")
    void testSave_negative_nullTrainer() {
        // when & then
        assertThatThrownBy(() -> trainerDao.save(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entity must not be null");

        verifyNoInteractions(em);
    }

    @Test
    @DisplayName("Test of the method update - should merge trainer and return it when trainer with given ID exists")
    void testUpdate_positive() {
        // given
        var trainer = getTestTrainer();

        given(em.find(eq(Trainer.class), anyLong())).willReturn(trainer);
        given(em.merge(any(Trainer.class))).willReturn(trainer);

        // when
        var actualResult = trainerDao.update(trainer);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(trainer);

        verify(em, times(1)).find(eq(Trainer.class), anyLong());
        verify(em, times(1)).merge(any(Trainer.class));
        verifyNoMoreInteractions(em);
    }

    @Test
    @DisplayName("Test of the method update - should throw IllegalArgumentException when trainer is null")
    void testUpdate_negative_nullTrainer() {
        // when & then
        assertThatThrownBy(() -> trainerDao.update(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entity must not be null and must have an ID");

        verifyNoInteractions(em);
    }

    @Test
    @DisplayName("Test of the method update - should throw IllegalArgumentException when trainer without ID is passed")
    void testUpdate_negative_trainerWithoutId() {
        // given
        var trainer = getTestTrainer();
        trainer.setId(null);

        // when & then
        assertThatThrownBy(() -> trainerDao.update(trainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entity must not be null and must have an ID");

        verifyNoInteractions(em);
    }

    @Test
    @DisplayName("Test of the method update - should throw IllegalArgumentException when trainer with given ID does not exist")
    void testUpdate_negative_notExistedTrainer() {
        // given
        var trainer = getTestTrainer();

        given(em.find(eq(Trainer.class), anyLong())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> trainerDao.update(trainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entity with ID " + trainer.getId() + " does not exist");

        verify(em, times(1)).find(eq(Trainer.class), anyLong());
        verifyNoMoreInteractions(em);
    }

    @Test
    @DisplayName("Test of the method changePassword - should change password of trainer when trainer with given ID exists")
    void testChangePassword() {
        // given
        var trainer = getTestTrainer();
        var newPassword = "newPassword";

        TypedQuery<Trainer> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyLong())).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.executeUpdate()).willReturn(1);

        // when
        trainerDao.changePassword(trainer.getId(), newPassword);

        // then
        verify(em, times(1)).createQuery(anyString());
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyLong());
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).executeUpdate();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    @Test
    @DisplayName("Test of the method changeStatus - should change status of trainer when trainer with given ID exists")
    void testChangeStatus() {
        // given
        var trainer = getTestTrainer();
        var isActive = false;

        TypedQuery<Trainer> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(Trainer.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyBoolean())).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyLong())).willReturn(mockTypedQuery);
        given(mockTypedQuery.executeUpdate()).willReturn(1);

        // when
        trainerDao.changeStatus(trainer.getId(), isActive);

        // then
        verify(em, times(1)).createQuery(anyString(), eq(Trainer.class));
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyBoolean());
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyLong());
        verify(mockTypedQuery, times(1)).executeUpdate();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    @Test
    @DisplayName("Test of the method delete - should remove trainer when trainer with given ID exists")
    void testDelete_positive() {
        // given
        var trainer = getTestTrainer();

        given(em.find(eq(Trainer.class), anyLong())).willReturn(trainer);
        doNothing().when(em).remove(any(Trainer.class));
        doNothing().when(em).flush();

        // when
        trainerDao.delete(trainer);

        // then
        verify(em, times(1)).find(eq(Trainer.class), anyLong());
        verify(em, times(1)).remove(any(Trainer.class));
        verify(em, times(1)).flush();
        verifyNoMoreInteractions(em);
    }

    @Test
    @DisplayName("Test of the method delete - should throw IllegalArgumentException when trainer is null")
    void testDelete_negative_nullTrainer() {
        // when & then
        assertThatThrownBy(() -> trainerDao.delete(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entity must not be null and must have an ID");

        verifyNoInteractions(em);
    }

    @Test
    @DisplayName("Test of the method delete - should throw IllegalArgumentException when trainer without ID is passed")
    void testDelete_negative_trainerWithoutId() {
        // given
        var trainer = getTestTrainer();
        trainer.setId(null);

        // when & then
        assertThatThrownBy(() -> trainerDao.delete(trainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entity must not be null and must have an ID");

        verifyNoInteractions(em);
    }

    @Test
    @DisplayName("Test of the method deleteByUsername - should delete trainer when trainer with given username exists")
    void testDeleteByUsername() {
        // given
        var username = "FirstName.LastName";

        TypedQuery<Trainer> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(Trainer.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.executeUpdate()).willReturn(1);

        // when
        trainerDao.deleteByUsername(username);

        // then
        verify(em, times(1)).createQuery(anyString(), eq(Trainer.class));
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).executeUpdate();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    @Test
    @DisplayName("Test of the method existsByUsername - should return true when trainer with given username exists")
    void testIsExistsByUsername_positive() {
        // given
        var username = "FirstName.LastName";

        TypedQuery<Boolean> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(Boolean.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mock(TypedQuery.class));
        given(mockTypedQuery.getSingleResult()).willReturn(true);

        // when
        var actualResult = trainerDao.existsByUsername(username);

        // then
        assertThat(actualResult).isTrue();

        verify(em, times(1)).createQuery(anyString(), eq(Boolean.class));
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).getSingleResult();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    @Test
    @DisplayName("Test of the method existsByUsername - should return false when trainer with given username does not exist")
    void testIsExistsByUsername_negative_notExistedTrainer() {
        // given
        var username = "NonExistentUsername";

        TypedQuery<Boolean> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(Boolean.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mock(TypedQuery.class));
        given(mockTypedQuery.getSingleResult()).willReturn(false);

        // when
        var actualResult = trainerDao.existsByUsername(username);

        // then
        assertThat(actualResult).isFalse();

        verify(em, times(1)).createQuery(anyString(), eq(Boolean.class));
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).getSingleResult();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    @Test
    @DisplayName("Test of the method existsByUsername - should throw IllegalArgumentException when username is null")
    void testIsExistsByUsername_negative_nullUsername() {
        // when & then
        assertThatThrownBy(() -> trainerDao.existsByUsername(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username must not be null");

        verifyNoInteractions(em);
    }

    @Test
    @DisplayName("Test of the method existsByUsername - should return false when username is blank")
    void testIsExistsByUsername_negative_blankUsername() {
        // given
        var username = "   ";

        // when
        var actualResult = trainerDao.existsByUsername(username);

        // then
        assertThat(actualResult).isFalse();

        verifyNoInteractions(em);
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