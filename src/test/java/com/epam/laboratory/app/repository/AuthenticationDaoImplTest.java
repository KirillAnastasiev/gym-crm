package com.epam.laboratory.app.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationDaoImpl test suite")
class AuthenticationDaoImplTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private AuthenticationDaoImpl authenticationDao;


    // ==================== CHECK EXISTS BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method checkExistsByUsername - should return true when user with given username exists")
    void testCheckExistsByUsername_positive() {
        // given
        var username = "FirstName.LastName";

        TypedQuery<Boolean> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(Boolean.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.getSingleResult()).willReturn(true);

        // when
        boolean actualResult = authenticationDao.checkExistsByUsername(username);

        // then
        assertThat(actualResult).isTrue();

        verify(em, times(1)).createQuery(anyString(), eq(Boolean.class));
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).getSingleResult();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    @Test
    @DisplayName("Test of the method checkExistsByUsername - should return false when user with given username does not exist")
    void testCheckExistsByUsername_negative() {
        // given
        var username = "NonExistentUsername";

        TypedQuery<Boolean> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(Boolean.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.getSingleResult()).willReturn(false);

        // when
        var actualResult = authenticationDao.checkExistsByUsername(username);

        // then
        assertThat(actualResult).isFalse();

        verify(em, times(1)).createQuery(anyString(), eq(Boolean.class));
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).getSingleResult();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }


    // ==================== CHECK PASSWORD FOR USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method checkPasswordForUsername - should return true when password for given username is correct")
    void testCheckPasswordForUsername_positive() {
        // given
        var username = "FirstName.LastName";
        var password = "password123";

        TypedQuery<String> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(String.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.getSingleResult()).willReturn(password);

        // when
        boolean actualResult = authenticationDao.checkPasswordForUsername(username, password);

        // then
        assertThat(actualResult).isTrue();

        verify(em, times(1)).createQuery(anyString(), eq(String.class));
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).getSingleResult();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

    @Test
    @DisplayName("Test of the method checkPasswordForUsername - should return false when password for given username is incorrect")
    void testCheckPasswordForUsername_negative() {
        // given
        var username = "FirstName.LastName";
        var password = "password123";
        var incorrectPassword = "wrongPassword";

        TypedQuery<String> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString(), eq(String.class))).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.getSingleResult()).willReturn(password);

        // when
        boolean actualResult = authenticationDao.checkPasswordForUsername(username, incorrectPassword);

        // then
        assertThat(actualResult).isFalse();

        verify(em, times(1)).createQuery(anyString(), eq(String.class));
        verify(mockTypedQuery, times(1)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).getSingleResult();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }


    // ==================== CHANGE PASSWORD FOR USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method changePasswordForUsername - should change password for given username")
    void testChangePasswordForUsername() {
        // given
        var username = "FirstName.LastName";
        var newPassword = "newPassword123";

        TypedQuery<Integer> mockTypedQuery = mock(TypedQuery.class);

        given(em.createQuery(anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.setParameter(anyString(), anyString())).willReturn(mockTypedQuery);
        given(mockTypedQuery.executeUpdate()).willReturn(1);

        // when
        authenticationDao.changePasswordForUsername(username, newPassword);

        // then
        verify(em, times(1)).createQuery(anyString());
        verify(mockTypedQuery, times(2)).setParameter(anyString(), anyString());
        verify(mockTypedQuery, times(1)).executeUpdate();
        verifyNoMoreInteractions(em, mockTypedQuery);
    }

}