package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.exception.AuthenticationException;
import com.epam.laboratory.app.exception.RestExceptionHandler;
import com.epam.laboratory.app.service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {
        AuthenticationController.class,
        RestExceptionHandler.class
})
@DisplayName("AuthenticationController test suite")
class AuthenticationControllerTest {
    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJKb2huLkRvZSIsImlhdCI6MTc3ODQzNTAwMCwiZXhwIjoxNzc4NDM4NjAwfQ.newAccessTokenSignature";
    private static final String REFRESH_TOKEN = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJKb2huLkRvZSIsImlhdCI6MTc3ODQzNDQzMywiZXhwIjoxNzc5NzMwNDMzfQ.CATJEnKWL0Oze6-lcRiU2Ba-Gxl3jDQ80qFSbiOwmWYTgPTU9G8Foa31iKlJgqMX";

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;


    // ==================== LOGIN ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method login - should return access and refresh tokens when credentials are correct")
    void testLogin_positive() throws Exception {
        // given
        var tokensMap = Map.of(
                "accessToken", ACCESS_TOKEN,
                "refreshToken", REFRESH_TOKEN
        );

        doNothing().when(authenticationService).validateUser(anyString(), anyString());
        given(authenticationService.getUserTokens(anyString())).willReturn(tokensMap);

        // when & then
        var actualResult = mockMvc.perform(get("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"John.Doe\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        var response = objectMapper.readValue(contentAsString, Map.class);

        assertThat(response).isNotNull();
        assertThat(response).isInstanceOf(Map.class);
        assertThat(response).containsKey("accessToken");
        assertThat(response).containsKey("refreshToken");
        assertThat(response).containsAllEntriesOf(tokensMap);

        verify(authenticationService, times(1)).validateUser(anyString(), anyString());
        verify(authenticationService, times(1)).getUserTokens(anyString());
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    @DisplayName("Test of the method login - should return 401 when user is not found")
    void testLogin_negative_userNotFound() throws Exception {
        // given
        doThrow(new AuthenticationException("User with username Unknown.User does not exist"))
                .when(authenticationService).validateUser(anyString(), anyString());

        // when & then
        mockMvc.perform(get("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Unknown.User\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("User with username Unknown.User does not exist"));

        verify(authenticationService, times(1)).validateUser(anyString(), anyString());
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    @DisplayName("Test of the method login - should return 401 when password is incorrect")
    void testLogin_negative_incorrectPassword() throws Exception {
        // given
        doThrow(new AuthenticationException("Incorrect password for username John.Doe"))
                .when(authenticationService).validateUser(anyString(), anyString());

        // when & then
        mockMvc.perform(get("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"John.Doe\",\"password\":\"wrongPassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Incorrect password for username John.Doe"));

        verify(authenticationService, times(1)).validateUser(anyString(), anyString());
        verifyNoMoreInteractions(authenticationService);
    }


    // ==================== REFRESH TOKEN ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method refreshAccessToken - should return new access token when refresh token is valid")
    void testRefreshAccessToken_positive() throws Exception {
        // given
        var newTokenMap = Map.of("accessToken", ACCESS_TOKEN);

        given(authenticationService.refreshAccessToken(anyString())).willReturn(newTokenMap);

        // when & then
        var actualResult = mockMvc.perform(get("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("\"" + REFRESH_TOKEN + "\""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        var response = objectMapper.readValue(contentAsString, Map.class);

        assertThat(response).isNotNull();
        assertThat(response).containsKey("accessToken");
        assertThat(response).containsAllEntriesOf(newTokenMap);

        verify(authenticationService, times(1)).refreshAccessToken(anyString());
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    @DisplayName("Test of the method refreshAccessToken - should return 401 when refresh token is invalid")
    void testRefreshAccessToken_negative_invalidRefreshToken() throws Exception {
        // given
        var invalidRefreshToken = "invalidRefreshToken";

        doThrow(new AuthenticationException("Invalid refresh token"))
                .when(authenticationService).refreshAccessToken(anyString());

        // when & then
       mockMvc.perform(get("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("\"" + invalidRefreshToken + "\""))
                .andExpect(status().isUnauthorized())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(content().string("Invalid refresh token"));

        verify(authenticationService, times(1)).refreshAccessToken(anyString());
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    @DisplayName("Test of the method refreshAccessToken - should return 401 when username is not found in refresh token")
    void testRefreshAccessToken_negative_noUsernameInRefreshToken() throws Exception {
        // given
        doThrow(new AuthenticationException("Username not found in refresh token"))
                .when(authenticationService).refreshAccessToken(anyString());

        // when & then
       mockMvc.perform(get("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("\"" + REFRESH_TOKEN + "\""))
                .andExpect(status().isUnauthorized())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(content().string("Username not found in refresh token"));

        verify(authenticationService, times(1)).refreshAccessToken(anyString());
        verifyNoMoreInteractions(authenticationService);
    }


    // ==================== CHANGE PASSWORD ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method changePassword - should return success message when password is changed successfully")
    void testChangePassword_positive() throws Exception {
        // given
        doNothing().when(authenticationService).changePassword(anyString(), anyString(), anyString());

        // when & then
        mockMvc.perform(put("/api/auth/{username}/change-password", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"oldPass123\",\"newPassword\":\"newPass456\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Password changed successfully"));

        verify(authenticationService, times(1)).changePassword(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    @DisplayName("Test of the method changePassword - should return 401 when old password is incorrect")
    void testChangePassword_negative_incorrectOldPassword() throws Exception {
        // given
        doThrow(new AuthenticationException("Incorrect password for username John.Doe"))
                .when(authenticationService).changePassword(anyString(), anyString(), anyString());

        // when & then
        mockMvc.perform(put("/api/auth/{username}/change-password", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"wrongOldPass\",\"newPassword\":\"newPass456\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Incorrect password for username John.Doe"));

        verify(authenticationService, times(1)).changePassword(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    @DisplayName("Change Password - Negative: Should return 401 when user does not exist")
    void testChangePassword_negative_userNotFound() throws Exception {
        // given
        doThrow(new AuthenticationException("User with username NonExistent.User does not exist"))
                .when(authenticationService).changePassword(anyString(), anyString(), anyString());

        // when & then
        mockMvc.perform(put("/api/auth/{username}/change-password", "NonExistent.User")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"oldPass123\",\"newPassword\":\"newPass456\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("User with username NonExistent.User does not exist"));

        verify(authenticationService, times(1)).changePassword(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(authenticationService);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, newPass456, Old password must not be null",
            "'', newPass456, Old password must not be blank",
            "'   ', newPass456, Old password must not be blank",
            "oldPass123, '', New password must not be blank",
            "oldPass123, '  ', New password must not be blank",
            "oldPass123, NULL, New password must not be null"
    }, nullValues = "NULL")
    @DisplayName("Test of the method changePassword - should return 400 when new password or old password is null or blank")
    void testChangePassword_negative_invalidInput(String oldPassword, String newPassword, String expectedErrorMessage) throws Exception {
        // when & then
        doThrow(new IllegalArgumentException(expectedErrorMessage)).when(authenticationService).changePassword(anyString(), anyString(), anyString());

        mockMvc.perform(put("/api/auth/{username}/change-password", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"%s\",\"newPassword\":\"%s\"}".formatted(oldPassword, newPassword)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedErrorMessage));

        verify(authenticationService, times(1)).changePassword(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(authenticationService);
    }
}
