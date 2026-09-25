package com.kirill.projects.gymcrm.app.rest;

import com.kirill.projects.gymcrm.app.config.TestSecurityConfig;
import com.kirill.projects.gymcrm.app.domain.TrainingType;
import com.kirill.projects.gymcrm.app.dto.TrainingTypeDto;
import com.kirill.projects.gymcrm.app.dto.mapper.TrainingTypeMapper;
import com.kirill.projects.gymcrm.app.dto.mapper.TrainingTypeMapperImpl;
import com.kirill.projects.gymcrm.app.exception.RestExceptionHandler;
import com.kirill.projects.gymcrm.app.security.JwtAuthenticationConverter;
import com.kirill.projects.gymcrm.app.service.TrainingTypeService;
import com.kirill.projects.gymcrm.app.service.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingTypeController.class)
@Import({
        TrainingTypeMapperImpl.class,
        RestExceptionHandler.class,
        TestSecurityConfig.class
})
@DisplayName("TrainingTypeController test suite")
class TrainingTypeControllerTest {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Autowired
    JsonMapper objectMapper;

    @Autowired
    private TrainingTypeMapper trainingTypeMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainingTypeService trainingTypeService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationConverter converter;

    @MockitoBean
    private AuthenticationManager authenticationManager;


    // ==================== GET ALL TRAINING TYPES ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method getAllTrainingTypes - should return list of all training types")
    void testGetAllTrainingTypes_positive() throws Exception {
        // given
        var trainingType = getTestTrainingType();
        var trainingTypeDto = trainingTypeMapper.toDto(trainingType);
        var expectedResponseBody = objectMapper.writeValueAsString(Collections.singletonList(trainingTypeDto));
        var requestId = "30f0e50b-7b04-4842-b0e5-0b7b046842f8";

        given(trainingTypeService.selectAll()).willReturn(Collections.singletonList(trainingType));

        // when & then
        var actualResult = mockMvc.perform(get("/api/training-types")
                        .header(REQUEST_ID_HEADER, requestId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        Collection<TrainingTypeDto> response = objectMapper.readValue(contentAsString,
                objectMapper.getTypeFactory().constructCollectionType(Collection.class, TrainingTypeDto.class));

        assertThat(response).isNotNull();
        assertThat(response).isInstanceOf(Collection.class);
        assertThat(response).isNotEmpty();
        assertThat(response).contains(trainingTypeDto);
        assertThat(contentAsString).isEqualTo(expectedResponseBody);

        verify(trainingTypeService, times(1)).selectAll();
        verifyNoMoreInteractions(trainingTypeService);
    }

    private static TrainingType getTestTrainingType() {
        var trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Fitness");
        return trainingType;
    }

}