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

    @BeforeEach
    void setUp() {
        AspectJProxyFactory factory = new AspectJProxyFactory(new TestServiceImpl());
        factory.addAspect(loggingAspect);
        testService = factory.getProxy();
    }

    @Test
    @DisplayName("Test method logMethodEntryWithoutArguments is called for method with no arguments")
    void testLogMethodEntryWithArguments() {
        // when
        testService.processData("TestInput", 42);

        // then
        verify(loggingAspect, times(1)).logMethodEntryWithArguments(any(JoinPoint.class), any(Logging.class));
        verify(loggingAspect, times(1)).logMethodEntryWithArguments(any(JoinPoint.class), any(Logging.class));
    }

    @Test
    @DisplayName("Test method logMethodEntryWithoutArguments is called for method with no arguments")
    void testLogMethodEntryWithoutArguments() {
        // when
        testService.noArgsMethod();

        // then
        verify(loggingAspect, times(1)).logMethodEntryWithoutArguments(any(JoinPoint.class), any(Logging.class));
        verify(loggingAspect, times(1)).logVoidMethodExit(any(JoinPoint.class), any(Logging.class));
    }

    @Test
    @DisplayName("Test method logMethodExitWithResult is called with correct result")
    void testLogMethodExitWithResult() {
        // when
        String result = testService.getData("TestKey");

        // then
        assertThat(result).isEqualTo("testValue");

        verify(loggingAspect, times(1)).logMethodEntryWithArguments(any(JoinPoint.class), any(Logging.class));
        verify(loggingAspect, times(1)).logMethodExitWithResult(any(JoinPoint.class), any(Logging.class), any());
    }

    @Test
    @DisplayName("Test method logMethodExitWithResult is called with complex object result")
    void testLogMethodExitWithResult_complexObject() {
        // when
        TestData result = testService.getComplexObject();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("TestName");

        verify(loggingAspect, times(1)).logMethodEntryWithoutArguments(any(JoinPoint.class), any(Logging.class));
        verify(loggingAspect, times(1)).logMethodExitWithResult(any(JoinPoint.class), any(Logging.class), any());
    }

    @Test
    @DisplayName("Test method logMethodException is called when exception is thrown")
    void testLogMethodException() {
        // when & then
        assertThatThrownBy(() -> testService.throwException())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Test exception");

        verify(loggingAspect, times(1)).logMethodEntryWithoutArguments(any(JoinPoint.class), any(Logging.class));
        verify(loggingAspect, times(1)).logMethodException(any(JoinPoint.class), any(Logging.class), any(Throwable.class));
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
        @Logging(Level.INFO)
        public String getData(String key) {
            return "testValue";
        }

        @Override
        @Logging(Level.INFO)
        public void throwException() {
            throw new RuntimeException("Test exception");
        }

        @Override
        @Logging(Level.INFO)
        public void noArgsMethod() {}

        @Override
        @Logging(Level.INFO)
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

