package com.epam.laboratory.app.aspect;

import com.epam.laboratory.app.aspect.annotation.RestCallLogging;
import com.epam.laboratory.app.dto.annotation.Sensitive;
import com.epam.laboratory.app.exception.ApplicationException;
import com.epam.laboratory.app.util.SensitiveDataMasker;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.slf4j.event.Level.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        JsonMapper.class,
        SensitiveDataMasker.class,
        RestCallLoggingAspectTest.TestRestController.class
})
@DisplayName("RestCallLoggingAspect test suite")
class RestCallLoggingAspectTest {

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private SensitiveDataMasker sensitiveDataMasker;

    private final PrintStream standardOut = System.out;

    private ByteArrayOutputStream outContent;

    private TestRestController testRestController;

    private MockMvc mockMvc;

    private RestCallLoggingAspect loggingAspect;

    @BeforeEach
    void setUp() {
        AspectJProxyFactory factory = new AspectJProxyFactory(new TestRestController());
        loggingAspect = spy(new RestCallLoggingAspect(objectMapper, sensitiveDataMasker));
        factory.addAspect(loggingAspect);
        testRestController = factory.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(testRestController)
                .build();

        changeStandardSystemOut();
    }

    @AfterEach
    void tearDown() {
        returnStandardSystemOut();
    }


    // ==================== LOG REST CALL REQUEST TESTS ====================

    @Test
    @DisplayName("Test of the method logRestCallRequest - should log correct HTTP method and endpoint for GET request")
    void testLogRestCallRequest_getMethod() throws Exception {
        // given
        ArgumentCaptor<JoinPoint> joinPointCaptor = ArgumentCaptor.forClass(JoinPoint.class);
        ArgumentCaptor<RestCallLogging> annotationCaptor = ArgumentCaptor.forClass(RestCallLogging.class);

        // when
        mockMvc.perform(get("/test/get"));

        // then
        String output = outContent.toString();

        assertThat(output).contains("REST Call - Endpoint: /test/get, HTTP Method: GET");

        verify(loggingAspect, times(1)).logRestCallRequest(joinPointCaptor.capture(), annotationCaptor.capture());

        JoinPoint capturedJoinPoint = joinPointCaptor.getValue();
        RestCallLogging capturedAnnotation = annotationCaptor.getValue();

        assertThat(capturedJoinPoint.getSignature().getName()).isEqualTo("testGetMethod");
        assertThat(capturedAnnotation.value()).isEqualTo(DEBUG);
    }

    @Test
    @DisplayName("Test of the method logRestCallRequest - should log correct HTTP method and endpoint for POST request")
    void testLogRestCallRequest_postMethod() throws Exception {
        // given
        ArgumentCaptor<JoinPoint> joinPointCaptor = ArgumentCaptor.forClass(JoinPoint.class);
        ArgumentCaptor<RestCallLogging> annotationCaptor = ArgumentCaptor.forClass(RestCallLogging.class);

        var requestDto = new TestRequestDto("testUser", "testPassword");
        var requestBody = objectMapper.writeValueAsString(requestDto);

        // when
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/test/post")
                .contentType("application/json")
                .content(requestBody));

        // then
        String output = outContent.toString();

        assertThat(output).contains("REST Call - Endpoint: /test/post, HTTP Method: POST, Request Body: {\"username\":\"testUser\",\"password\":\"************\"}");

        verify(loggingAspect, times(1)).logRestCallRequest(joinPointCaptor.capture(), annotationCaptor.capture());

        JoinPoint capturedJoinPoint = joinPointCaptor.getValue();
        RestCallLogging capturedAnnotation = annotationCaptor.getValue();

        assertThat(capturedJoinPoint.getSignature().getName()).isEqualTo("testPostMethod");
        assertThat(capturedAnnotation.value()).isEqualTo(INFO);
    }


    // ==================== LOG REST CALL RESPONSE TESTS ====================

    @Test
    @DisplayName("Test of the method logRestCallRequest - should log correct HTTP status code and response for GET request")
    void testLogRestCallResponse_getMethod() throws Exception {
        // given
        ArgumentCaptor<JoinPoint> joinPointCaptor = ArgumentCaptor.forClass(JoinPoint.class);
        ArgumentCaptor<RestCallLogging> annotationCaptor = ArgumentCaptor.forClass(RestCallLogging.class);

        // when
        mockMvc.perform(get("/test/get"));

        // then
        String output = outContent.toString();

        assertThat(output).contains("REST Call Response - Status Code: 200, Response Body: \"GET method - Success\"");

        verify(loggingAspect, times(1)).logRestCallResponse(joinPointCaptor.capture(), annotationCaptor.capture(), any());

        JoinPoint capturedJoinPoint = joinPointCaptor.getValue();
        RestCallLogging capturedAnnotation = annotationCaptor.getValue();

        assertThat(capturedJoinPoint.getSignature().getName()).isEqualTo("testGetMethod");
        assertThat(capturedAnnotation.value()).isEqualTo(DEBUG);
    }

    @Test
    @DisplayName("Test of the method logRestCallRequest - should log correct HTTP status code and response for POST request")
    void testLogRestCallResponse_postMethod() throws Exception {
        // given
        ArgumentCaptor<JoinPoint> joinPointCaptor = ArgumentCaptor.forClass(JoinPoint.class);
        ArgumentCaptor<RestCallLogging> annotationCaptor = ArgumentCaptor.forClass(RestCallLogging.class);

        var requestDto = new TestRequestDto("testUser", "testPassword");
        var requestBody = objectMapper.writeValueAsString(requestDto);

        // when
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/test/post")
                .contentType("application/json")
                .content(requestBody));

        // then
        String output = outContent.toString();

        assertThat(output).contains("REST Call Response - Status Code: 200, Response Body: \"POST method - Success\"");

        verify(loggingAspect, times(1)).logRestCallResponse(joinPointCaptor.capture(), annotationCaptor.capture(), any());

        JoinPoint capturedJoinPoint = joinPointCaptor.getValue();
        RestCallLogging capturedAnnotation = annotationCaptor.getValue();

        assertThat(capturedJoinPoint.getSignature().getName()).isEqualTo("testPostMethod");
        assertThat(capturedAnnotation.value()).isEqualTo(INFO);
    }


    // ==================== LOG REST CALL EXCEPTION TESTS ====================

    @Test
    @DisplayName("Test of the method logRestCallException - should log correct exception message and type when REST call results in exception")
    void testLogRestCallException() throws Exception {
        // given
        ArgumentCaptor<JoinPoint> joinPointCaptor = ArgumentCaptor.forClass(JoinPoint.class);
        ArgumentCaptor<RestCallLogging> annotationCaptor = ArgumentCaptor.forClass(RestCallLogging.class);
        ArgumentCaptor<Throwable> exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);

        // when
        mockMvc.perform(get("/test/exception"));

        // then
        String output = outContent.toString();

        assertThat(output).contains("REST call resulted in exception: Test exception");

        verify(loggingAspect, times(1)).logRestCallException(joinPointCaptor.capture(), annotationCaptor.capture(), exceptionCaptor.capture());

        JoinPoint capturedJoinPoint = joinPointCaptor.getValue();
        RestCallLogging capturedAnnotation = annotationCaptor.getValue();
        Throwable capturedException = exceptionCaptor.getValue();

        assertThat(capturedJoinPoint.getSignature().getName()).isEqualTo("testMethodThatThrowsException");
        assertThat(capturedAnnotation.value()).isEqualTo(TRACE);
        assertThat(capturedException).isInstanceOf(ApplicationException.class);
        assertThat(capturedException.getMessage()).isEqualTo("Test exception");
    }

    private void changeStandardSystemOut() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    private void returnStandardSystemOut() {
        System.setOut(standardOut);
    }

    @RestController
    @RequestMapping("/test")
    static class TestRestController {

        @RestCallLogging(DEBUG)
        @GetMapping("/get")
        ResponseEntity<?> testGetMethod() {
            return ResponseEntity.ok("GET method - Success");
        }

        @RestCallLogging(INFO)
        @PostMapping("/post")
        ResponseEntity<?> testPostMethod(@RequestBody TestRequestDto request) {
            return ResponseEntity.ok("POST method - Success");
        }

        @RestCallLogging(TRACE)
        @GetMapping("/exception")
        ResponseEntity<?> testMethodThatThrowsException() {
            throw new ApplicationException("Test exception");
        }

        @ExceptionHandler(ApplicationException.class)
        ResponseEntity<?> handleException(ApplicationException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    private record TestRequestDto(
            String username,
            @Sensitive String password
    ) {}

}