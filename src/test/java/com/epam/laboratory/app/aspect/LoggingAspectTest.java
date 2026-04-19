package com.epam.laboratory.app.aspect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.event.Level;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoggingAspect Test Suite")
class LoggingAspectTest {
    @Spy
    private LoggingAspect loggingAspect;

    private TestService testService;

    private final PrintStream originalOut = System.out;

    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        AspectJProxyFactory factory = new AspectJProxyFactory(new TestServiceImpl());
        factory.addAspect(loggingAspect);
        testService = factory.getProxy();
        outContent = new ByteArrayOutputStream();
    }

    @Test
    @DisplayName("Test method logMethodEntryWithoutArguments is called for method with no arguments")
    void testLogMethodEntryWithArguments() {
        // given
        changeStandardSystemOut();

        // when
        testService.processData("TestInput", 42);

        // then
        returnStandardSystemOut();
        String output = outContent.toString();

        assertThat(output).contains("INFO");
        assertThat(output).contains("Entering method: TestServiceImpl.processData with arguments: [TestInput, 42]");
        assertThat(output).contains("Exiting method: TestServiceImpl.processData");

        verify(loggingAspect, times(1)).logMethodEntryWithArguments(any(JoinPoint.class), any(Logging.class));
        verify(loggingAspect, times(1)).logMethodEntryWithArguments(any(JoinPoint.class), any(Logging.class));
    }

    @Test
    @DisplayName("Test method logMethodEntryWithoutArguments is called for method with no arguments")
    void testLogMethodEntryWithoutArguments() {
        //given
        changeStandardSystemOut();

        // when
        testService.noArgsMethod();

        // then
        returnStandardSystemOut();
        String output = outContent.toString();

        assertThat(output).contains("DEBUG");
        assertThat(output).contains("Entering method: TestServiceImpl.noArgsMethod");
        assertThat(output).contains("Exiting method: TestServiceImpl.noArgsMethod");

        verify(loggingAspect, times(1)).logMethodEntryWithoutArguments(any(JoinPoint.class), any(Logging.class));
        verify(loggingAspect, times(1)).logVoidMethodExit(any(JoinPoint.class), any(Logging.class));
    }

    @Test
    @DisplayName("Test method logMethodExitWithResult is called with correct result")
    void testLogMethodExitWithResult() {
        //given
        changeStandardSystemOut();

        // when
        String result = testService.getData("TestKey");

        // then
        returnStandardSystemOut();
        String output = outContent.toString();

        assertThat(output).contains("WARN");
        assertThat(output).contains("Entering method: TestServiceImpl.getData with arguments: [TestKey]");
        assertThat(output).contains("Exiting method: TestServiceImpl.getData with result: testValue");
        assertThat(result).isEqualTo("testValue");

        verify(loggingAspect, times(1)).logMethodEntryWithArguments(any(JoinPoint.class), any(Logging.class));
        verify(loggingAspect, times(1)).logMethodExitWithResult(any(JoinPoint.class), any(Logging.class), any());
    }

    @Test
    @DisplayName("Test method logMethodExitWithResult is called with complex object result")
    void testLogMethodExitWithResult_complexObject() {
        //given
        changeStandardSystemOut();

        // when
        TestData result = testService.getComplexObject();

        // then
        returnStandardSystemOut();
        String output = outContent.toString();

        assertThat(output).contains("TRACE");
        assertThat(output).contains("Entering method: TestServiceImpl.getComplexObject");
        assertThat(output).contains("Exiting method: TestServiceImpl.getComplexObject with result: LoggingAspectTest.TestData(name=TestName, value=123)");
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("TestName");

        verify(loggingAspect, times(1)).logMethodEntryWithoutArguments(any(JoinPoint.class), any(Logging.class));
        verify(loggingAspect, times(1)).logMethodExitWithResult(any(JoinPoint.class), any(Logging.class), any());
    }

    @Test
    @DisplayName("Test method logMethodException is called when exception is thrown")
    void testLogMethodException() {
        //given
        changeStandardSystemOut();

        // when & then
        assertThatThrownBy(() -> testService.throwException())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Test exception");

        returnStandardSystemOut();
        String output = outContent.toString();

        assertThat(output).contains("ERROR");
        assertThat(output).contains("Exception in method: TestServiceImpl.throwException with message: Test exception");

        verify(loggingAspect, times(1)).logMethodEntryWithoutArguments(any(JoinPoint.class), any(Logging.class));
        verify(loggingAspect, times(1)).logMethodException(any(JoinPoint.class), any(Logging.class), any(Throwable.class));
    }

    private void changeStandardSystemOut() {
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
            throw new RuntimeException("Test exception");
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

    @RequiredArgsConstructor
    @Getter
    @ToString
    static class TestData {
        private final String name;
        private final int value;
    }
}

