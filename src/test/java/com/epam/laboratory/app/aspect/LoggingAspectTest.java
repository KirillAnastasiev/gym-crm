package com.epam.laboratory.app.aspect;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.exception.ApplicationException;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.event.Level;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoggingAspect test suite")
class LoggingAspectTest {

    private TestService testService;

    private final PrintStream originalOut = System.out;

    private ByteArrayOutputStream outContent;

    @Spy
    private LoggingAspect loggingAspect = new LoggingAspect();

    @BeforeEach
    void setUp() {
        AspectJProxyFactory factory = new AspectJProxyFactory(new TestServiceImpl());
        factory.addAspect(loggingAspect);
        testService = factory.getProxy();

        changeStandardSystemOut();
    }

    @AfterEach
    void tearDown() {
        returnStandardSystemOut();
    }

    // ==================== LOG METHOD ENTRY WITH ARGUMENTS TESTS ====================

    @Test
    @DisplayName("Test method logMethodEntryWithoutArguments - should log method entry and exit for method with arguments")
    void testLogMethodEntryWithArguments() {
        // given
        ArgumentCaptor<JoinPoint> joinPointCaptor = ArgumentCaptor.forClass(JoinPoint.class);
        ArgumentCaptor<Logging> loggingCaptor = ArgumentCaptor.forClass(Logging.class);

        // when
        testService.processData("TestInput", 42);

        // then
        String output = outContent.toString();

        assertThat(output).contains("INFO");
        assertThat(output).contains("Entering method: TestServiceImpl.processData with arguments: [TestInput, 42]");

        verify(loggingAspect, times(1)).logMethodEntryWithArguments(joinPointCaptor.capture(), loggingCaptor.capture());

        JoinPoint capturedJoinPoint = joinPointCaptor.getValue();
        Logging capturedLogging = loggingCaptor.getValue();

        assertThat(capturedJoinPoint.getSignature().getName()).isEqualTo("processData");
        assertThat(capturedLogging.value()).isEqualTo(Level.INFO);
    }


    // ==================== LOG METHOD ENTRY WITHOUT ARGUMENTS TESTS ====================

    @Test
    @DisplayName("Test method logMethodEntryWithoutArguments - should log method entry and exit for method without arguments")
    void testLogMethodEntryWithoutArguments() {
        //given
        ArgumentCaptor<JoinPoint> joinPointCaptor = ArgumentCaptor.forClass(JoinPoint.class);
        ArgumentCaptor<Logging> loggingCaptor = ArgumentCaptor.forClass(Logging.class);

        // when
        testService.noArgsMethod();

        // then
        String output = outContent.toString();

        assertThat(output).contains("Entering method: TestServiceImpl.noArgsMethod");

        verify(loggingAspect, times(1)).logMethodEntryWithoutArguments(joinPointCaptor.capture(), loggingCaptor.capture());

        JoinPoint capturedJoinPoint = joinPointCaptor.getValue();
        Logging capturedLogging = loggingCaptor.getValue();

        assertThat(capturedJoinPoint.getSignature().getName()).isEqualTo("noArgsMethod");
        assertThat(capturedLogging.value()).isEqualTo(Level.DEBUG);
    }


    // ==================== LOG METHOD EXIT WITH RESULT TESTS ====================

    @Test
    @DisplayName("Test method logMethodExitWithResult - should log method entry and exit with result for method with arguments")
    void testLogMethodExitWithResult() {
        //given
        ArgumentCaptor<JoinPoint> joinPointCaptor = ArgumentCaptor.forClass(JoinPoint.class);
        ArgumentCaptor<Logging> loggingCaptor = ArgumentCaptor.forClass(Logging.class);
        ArgumentCaptor<Object> resultCaptor = ArgumentCaptor.forClass(Object.class);

        // when
        String result = testService.getData("TestKey");

        // then
        String output = outContent.toString();

        assertThat(output).contains("Exiting method: TestServiceImpl.getData with result: testValue");
        assertThat(result).isEqualTo("testValue");

        verify(loggingAspect, times(1)).logMethodExitWithResult(joinPointCaptor.capture(), loggingCaptor.capture(), resultCaptor.capture());

        JoinPoint capturedJoinPoint = joinPointCaptor.getValue();
        Logging capturedLogging = loggingCaptor.getValue();
        Object capturedResult = resultCaptor.getValue();

        assertThat(capturedJoinPoint.getSignature().getName()).isEqualTo("getData");
        assertThat(capturedLogging.value()).isEqualTo(Level.WARN);
        assertThat(capturedResult).isEqualTo("testValue");
    }

    @Test
    @DisplayName("Test method logMethodExitWithResult - should log method entry and exit with complex object result for method without arguments")
    void testLogMethodExitWithResult_complexObject() {
        //given
        ArgumentCaptor<JoinPoint> joinPointCaptor = ArgumentCaptor.forClass(JoinPoint.class);
        ArgumentCaptor<Logging> loggingCaptor = ArgumentCaptor.forClass(Logging.class);

        // when
        TestData result = testService.getComplexObject();

        // then
        String output = outContent.toString();

        assertThat(output).contains("""
                              Exiting method: TestServiceImpl.getComplexObject with result: TestData[name=TestName, value=123]""");
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("TestName");

        verify(loggingAspect, times(1)).logMethodExitWithResult(joinPointCaptor.capture(), loggingCaptor.capture(), any());

        JoinPoint capturedJoinPoint = joinPointCaptor.getValue();
        Logging capturedLogging = loggingCaptor.getValue();

        assertThat(capturedJoinPoint.getSignature().getName()).isEqualTo("getComplexObject");
        assertThat(capturedLogging.value()).isEqualTo(Level.TRACE);
    }


    // ==================== LOG METHOD EXIT WITHOUT RESULT TESTS ====================

    @Test
    @DisplayName("Test method logMethodExitWithoutResult - should log method entry and exit for void method without arguments")
    void testLogMethodExitWithoutResult() {
        //given
        ArgumentCaptor<JoinPoint> joinPointCaptor = ArgumentCaptor.forClass(JoinPoint.class);
        ArgumentCaptor<Logging> loggingCaptor = ArgumentCaptor.forClass(Logging.class);

        // when
        testService.processData("TestInput", 42);

        // then
        String output = outContent.toString();

        assertThat(output).contains("Exiting method: TestServiceImpl.processData");

        verify(loggingAspect, times(1)).logVoidMethodExit(joinPointCaptor.capture(), loggingCaptor.capture());

        JoinPoint capturedJoinPoint = joinPointCaptor.getValue();
        Logging capturedLogging = loggingCaptor.getValue();

        assertThat(capturedJoinPoint.getSignature().getName()).isEqualTo("processData");
        assertThat(capturedLogging.value()).isEqualTo(Level.INFO);
    }


    // ==================== LOG METHOD EXCEPTION TESTS ====================

    @Test
    @DisplayName("Test method logMethodException - should log method entry and exception for method that throws exception")
    void testLogMethodException() {
        //given
        ArgumentCaptor<JoinPoint> joinPointCaptor = ArgumentCaptor.forClass(JoinPoint.class);
        ArgumentCaptor<Logging> loggingCaptor = ArgumentCaptor.forClass(Logging.class);
        ArgumentCaptor<Throwable> exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);

        // when & then
        assertThatThrownBy(() -> testService.throwException())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Test exception");

        String output = outContent.toString();

        assertThat(output).contains("WARN");
        assertThat(output).contains("Exception in method: TestServiceImpl.throwException with message: Test exception");

        verify(loggingAspect, times(1)).logMethodException(joinPointCaptor.capture(), loggingCaptor.capture(), exceptionCaptor.capture());

        JoinPoint capturedJoinPoint = joinPointCaptor.getValue();
        Logging capturedLogging = loggingCaptor.getValue();
        Throwable capturedException = exceptionCaptor.getValue();

        assertThat(capturedJoinPoint.getSignature().getName()).isEqualTo("throwException");
        assertThat(capturedLogging.value()).isEqualTo(Level.INFO);
        assertThat(capturedException.getMessage()).isEqualTo("Test exception");
    }

    private void changeStandardSystemOut() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    private void returnStandardSystemOut() {
        System.setOut(originalOut);
    }

    private interface TestService {
        void noArgsMethod();
        void processData(String input, int value);
        TestData getComplexObject();
        String getData(String key);
        void throwException();
    }

    private static class TestServiceImpl implements TestService {
        @Override
        @Logging(Level.INFO)
        public void processData(String input, int value) {}

        @Override
        @Logging(Level.WARN)
        public String getData(String key) {
            return "testValue";
        }

        @Override
        @Logging(Level.INFO)
        public void throwException() {
            throw new ApplicationException("Test exception");
        }

        @Override
        @Logging(Level.DEBUG)
        public void noArgsMethod() {}

        @Override
        @Logging
        public TestData getComplexObject() {
            return new TestData("TestName", 123);
        }
    }

    record TestData(
        String name,
        int value
    ) {}

}

