package com.epam.laboratory.app.service.security;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.User;
import com.epam.laboratory.app.domain.UserSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsernamePasswordAuthenticationUserDetailsService test suite")
class UsernamePasswordAuthenticationUserDetailsServiceTest {

    @Mock
    private UserSecurityService userSecurityService;

    @InjectMocks
    private UsernamePasswordAuthenticationUserDetailsService userDetailsService;


    // ==================== LOAD USER BY USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method loadUserByUsername - should return UserDetails when user is found and valid")
    void testLoadUserByUsername_positive() {
        // given
        var username = "John.Doe";
        var user = createTestUser();

        given(userSecurityService.getUserWithUserSecurityByUsername(anyString())).willReturn(Optional.of(user));
        doNothing().when(userSecurityService).checkUserSecurity(any(User.class));

        // when
        var actualResult = userDetailsService.loadUserByUsername(username);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(UserDetails.class);
        assertThat(actualResult.getUsername()).isEqualTo(username);
        assertThat(actualResult.isEnabled()).isTrue();
        assertThat(actualResult.isAccountNonExpired()).isTrue();
        assertThat(actualResult.isAccountNonLocked()).isTrue();
        actualResult.getAuthorities().forEach(authority ->
                assertThat(authority.getAuthority()).isEqualTo("USER"));

        verify(userSecurityService, times(1)).getUserWithUserSecurityByUsername(anyString());
        verify(userSecurityService, times(1)).checkUserSecurity(any(User.class));
        verifyNoMoreInteractions(userSecurityService);
    }

    @Test
    @DisplayName("Test of the method loadUserByUsername - should throw UsernameNotFoundException when user is not found")
    void testLoadUserByUsername_negative_notFoudUser() {
        // given
        given(userSecurityService.getUserWithUserSecurityByUsername(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("John.Doe"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Invalid username or password");

        verify(userSecurityService, times(1)).getUserWithUserSecurityByUsername(anyString());
        verifyNoMoreInteractions(userSecurityService);
    }

    @Test
    @DisplayName("Test of the method loadUserByUsername - should throw RuntimeException when database error occurs")
    void testLoadUserByUsername_negative_databaseException() {
        // given
        given(userSecurityService.getUserWithUserSecurityByUsername(anyString())).willThrow(new RuntimeException("Database error"));

        // when
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("John.Doe"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(userSecurityService, times(1)).getUserWithUserSecurityByUsername(anyString());
        verifyNoMoreInteractions(userSecurityService);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "'', 'Username must not be blank'",
        "'   ', 'Username must not be blank'",
        "NULL, 'Username must not be null'"
    }, nullValues = "NULL")
    void testLoadUserByUsername_negative_invalidInput(String username, String message) {
        // when & then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);

        verifyNoInteractions(userSecurityService);
    }

    private static User createTestUser() {
        var user = new Trainee();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setActive(true);

        var userSecurity = new UserSecurity();
        userSecurity.setUser(user);
        userSecurity.setId(user.getId());
        userSecurity.setAccountLocked(false);
        userSecurity.setFailedAttempts(0);
        user.setSecurity(userSecurity);

        return user;
    }

}