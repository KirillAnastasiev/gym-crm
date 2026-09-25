package com.kirill.projects.gymcrm.app.security;

import jakarta.servlet.ServletOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ObjectMapper.class)
class CustomAuthenticationEntryPointTest {
    private static final String BEARER_AUTHENTICATION_REQUIRED_MESSAGE = "Bearer realm=\"Access to the protected resource\", charset=\"UTF-8\"";

    @Autowired
    private ObjectMapper objectMapper;

    private CustomAuthenticationEntryPoint entryPoint;

    @BeforeEach
    void setUp() {
        objectMapper = spy(objectMapper);
        entryPoint = new CustomAuthenticationEntryPoint(objectMapper, BEARER_AUTHENTICATION_REQUIRED_MESSAGE);
    }


    // ==================== COMMENCE TESTS ====================

    @Test
    @DisplayName("Test of the method commence - should return 401 status with correct message and content type when exception is thrown")
    void testCommence_positive() throws IOException {
        // given
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var exception = new BadCredentialsException("Bad Credentials");

        // when
        entryPoint.commence(request, response, exception);
        var actualResult = response.getContentAsString();

        assertThat(actualResult).contains("Missing or invalid Authorization header");
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/problem+json");

        verify(objectMapper, times(1)).writeValue(any(OutputStream.class), any(ProblemDetail.class));
    }

    @Test
    @DisplayName("Test of the method commence - should throw IOException when response output stream cannot be obtained")
    void testCommence_negative_thrownException() {
        // given
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse() {
            @Override
            public ServletOutputStream getOutputStream() {
                throw new RuntimeException("Unable to get OutputStream");
            }
        };
        var exception = new BadCredentialsException("Bad Credentials");

        // when & then
        assertThatThrownBy(() -> entryPoint.commence(request, response, exception))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unable to get OutputStream");

        verifyNoInteractions(objectMapper);
    }

}