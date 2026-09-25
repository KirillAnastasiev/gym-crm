package com.kirill.projects.gymcrm.app.aspect;

import com.kirill.projects.gymcrm.app.aspect.annotation.ValidateArguments;
import com.kirill.projects.gymcrm.app.dto.annotation.Required;
import com.kirill.projects.gymcrm.app.exception.DtoValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RestRequestArgumentsValidatingAspect test suite")
class RestRequestArgumentsValidatingAspectTest {

    private TestController testController;

    @Spy
    private RestRequestArgumentsValidatingAspect validatingAspect = new RestRequestArgumentsValidatingAspect();

    @BeforeEach
    void setUp() {
        AspectJProxyFactory factory = new AspectJProxyFactory(new TestController());
        factory.addAspect(validatingAspect);
        testController = factory.getProxy();
    }


    // ==================== VALIDATE REST REQUEST ARGUMENTS TESTS ====================

    @Test
    @DisplayName("Test of the method validateRestRequestArguments - should validate method arguments and not throw exception")
    void testValidateRestRequestArguments_positive() {
        // when & then
        assertThatNoException().isThrownBy(() ->
                testController.testMethod("15", new TestDto("John", 30)));

        verify(validatingAspect, times(1)).validateRestRequestArguments(any(), any());
        verifyNoMoreInteractions(validatingAspect);
    }

    @Test
    @DisplayName("Test of the method validateRestRequestArguments - should throw IllegalArgumentException when path variable is blank string")
    void testValidateRestRequestArguments_negative_pathVariableIsBlankString() {
        // when & then
        assertThatThrownBy(() ->
                testController.testMethod(" ", new TestDto("John", 30)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Path variable 'id' must not be blank");

        verify(validatingAspect, times(1)).validateRestRequestArguments(any(), any());
        verifyNoMoreInteractions(validatingAspect);
    }

    @Test
    void testValidateRestRequestArguments_negative_requiredValueIsNull() {
        // when & then
        assertThatThrownBy(() ->
                testController.testMethod("15", new TestDto(null, 30)))
                .isInstanceOf(DtoValidationException.class)
                .hasMessage("Property 'name' is required");

        verify(validatingAspect, times(1)).validateRestRequestArguments(any(), any());
        verifyNoMoreInteractions(validatingAspect);
    }

    public static class TestController {

        @ValidateArguments
        public void testMethod(@PathVariable String id, @RequestBody TestDto dto) {}

    }

    public record TestDto(
            @Required String name,
            int age
    ) {}

}