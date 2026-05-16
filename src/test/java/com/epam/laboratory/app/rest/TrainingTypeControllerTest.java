package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.dto.TrainingTypeDto;
import com.epam.laboratory.app.dto.mapper.TrainingTypeMapper;
import com.epam.laboratory.app.dto.mapper.TrainingTypeMapperImpl;
import com.epam.laboratory.app.exception.RestExceptionHandler;
import com.epam.laboratory.app.service.TrainingTypeService;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        JsonMapper.class,
        TrainingTypeMapperImpl.class,
        RestExceptionHandler.class
})
@DisplayName("TrainingTypeController test suite")
class TrainingTypeControllerTest {

    @Autowired
    JsonMapper objectMapper;

    @Autowired
    private TrainingTypeMapper trainingTypeMapper;

    @Autowired
    private RestExceptionHandler restExceptionHandler;

    @MockitoBean
    private TrainingTypeService trainingTypeService;

    private TrainingTypeController trainingTypeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        trainingTypeController = new TrainingTypeController(trainingTypeService, trainingTypeMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(trainingTypeController)
                .setControllerAdvice(restExceptionHandler)
                .build();
    }


    // ==================== GET ALL TRAINING TYPES ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method getAllTrainingTypes - should return list of all training types")
    void testGetAllTrainingTypes_positive() throws Exception {
        // given
        var trainingType = getTestTrainingType();
        var trainingTypeDto = trainingTypeMapper.toDto(trainingType);
        var expectedResponseBody = objectMapper.writeValueAsString(Collections.singletonList(trainingTypeDto));

        given(trainingTypeService.selectAll()).willReturn(Collections.singletonList(trainingType));

        // when & then
        var actualResult = mockMvc.perform(get("/api/training-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
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