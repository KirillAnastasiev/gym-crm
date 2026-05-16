package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.domain.*;
import com.epam.laboratory.app.dto.TrainingDto;
import com.epam.laboratory.app.dto.mapper.*;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.exception.RestExceptionHandler;
import com.epam.laboratory.app.service.TrainingService;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        JavaTimeModule.class,
        JsonMapper.class,
        TrainingMapperImpl.class,
        TrainingTypeMapperImpl.class,
        TrainingFilterMapperImpl.class,
        RestExceptionHandler.class
})
@DisplayName("TrainingController test suite")
class TrainingControllerTest {

    @Autowired
    private JavaTimeModule javaTimeModule;

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private TrainingMapper traineeMapper;

    @Autowired
    private TrainingFilterMapper traineeFilterMapper;

    @Autowired
    private RestExceptionHandler restExceptionHandler;

    @MockitoBean
    private TrainingService trainingService;

    private TrainingController trainingController;

    private MockMvc mockMvc;
    @Autowired
    private TrainingMapper trainingMapper;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(javaTimeModule);
        trainingController = new TrainingController(trainingService, traineeMapper, traineeFilterMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(trainingController)
                .setControllerAdvice(restExceptionHandler)
                .build();
    }


    // ==================== GET TRAINEE TRAININGS ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method getTraineeTrainings - should return list of trainings for trainee when valid filter is provided")
    void testGetTraineeTrainings_positive() throws Exception {
        // given
        var trainingType = getTestTrainingType();
        var trainee = getTestTrainee();
        var trainer = getTestTrainer(trainingType);
        var training = getTestTraining(trainee, trainer, trainingType);
        var trainingFilter = getTestTrainingFilter();

        var requestBody = objectMapper.writeValueAsString(traineeFilterMapper.toDto(trainingFilter));
        var expectedResponse = objectMapper.writeValueAsString(Collections.singletonList(trainingMapper.toDto(training)));

        given(trainingService.selectForTrainee(anyString(), any(TrainingFilter.class))).willReturn(Collections.singletonList(training));

        // when & then
        var actualResult = mockMvc.perform(get("/api/trainings/trainee/{username}", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        Collection<TrainingDto> response = objectMapper.readValue(contentAsString,
                objectMapper.getTypeFactory().constructCollectionType(Collection.class, TrainingDto.class));

        assertThat(response).isNotNull();
        assertThat(response).isInstanceOf(Collection.class);
        assertThat(response).isNotEmpty();
        assertThat(response).contains(trainingMapper.toDto(training));
        assertThat(contentAsString).isEqualTo(expectedResponse);

        verify(trainingService, times(1)).selectForTrainee(anyString(), any(TrainingFilter.class));
        verifyNoMoreInteractions(trainingService);
    }


    // ==================== GET TRAINER TRAININGS ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method getTrainerTrainings - should return list of trainings for trainer when valid filter is provided")
    void testGetTrainerTrainings_positive() throws Exception {
        // given
        var trainingType = getTestTrainingType();
        var trainee = getTestTrainee();
        var trainer = getTestTrainer(trainingType);
        var training = getTestTraining(trainee, trainer, trainingType);
        var trainingFilter = getTestTrainingFilter();

        var requestBody = objectMapper.writeValueAsString(traineeFilterMapper.toDto(trainingFilter));
        var expectedResponse = objectMapper.writeValueAsString(Collections.singletonList(trainingMapper.toDto(training)));

        given(trainingService.selectForTrainer(anyString(), any(TrainingFilter.class))).willReturn(Collections.singletonList(training));

        // when & then
        var actualResult = mockMvc.perform(get("/api/trainings/trainer/{username}", "Jane.Smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        Collection<TrainingDto> response = objectMapper.readValue(contentAsString,
                objectMapper.getTypeFactory().constructCollectionType(Collection.class, TrainingDto.class));

        assertThat(response).isNotNull();
        assertThat(response).isInstanceOf(Collection.class);
        assertThat(response).isNotEmpty();
        assertThat(response).contains(trainingMapper.toDto(training));
        assertThat(contentAsString).isEqualTo(expectedResponse);

        verify(trainingService, times(1)).selectForTrainer(anyString(), any(TrainingFilter.class));
        verifyNoMoreInteractions(trainingService);
    }


    // ==================== REGISTER TRAINING ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method registerTraining - should register training when valid training is provided")
    void testRegisterTraining_positive() throws Exception {
        // given
        var trainingType = getTestTrainingType();
        var trainee = getTestTrainee();
        var trainer = getTestTrainer(trainingType);
        var training = getTestTraining(trainee, trainer, trainingType);

        var requestBody = objectMapper.writeValueAsString(trainingMapper.toDto(training));

        given(trainingService.registerNew(any(Training.class))).willReturn(training);

        // when & then
        var actualResult = mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        assertThat(contentAsString).isNotNull();
        assertThat(contentAsString).isEqualTo("Training was registered");

        verify(trainingService, times(1)).registerNew(any(Training.class));
        verifyNoMoreInteractions(trainingService);
    }

    @Test
    @DisplayName("Test of the method registerTraining - should return bad request when trainee with provided username does not exist")
    void testRegisterTraining_negative_notExistedTrainee() throws Exception {
        // given
        var trainingType = getTestTrainingType();
        var trainee = getTestTrainee();
        var trainer = getTestTrainer(trainingType);
        var training = getTestTraining(trainee, trainer, trainingType);

        var requestBody = objectMapper.writeValueAsString(trainingMapper.toDto(training));

        given(trainingService.registerNew(any(Training.class))).willThrow(new NoSuchEntityException("Trainee with username John.Doe not found"));

        // when & then
        var actualResult = mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        assertThat(contentAsString).isNotNull();
        assertThat(contentAsString).contains("Trainee with username John.Doe not found");

        verify(trainingService, times(1)).registerNew(any(Training.class));
        verifyNoMoreInteractions(trainingService);
    }

    @Test
    @DisplayName("Test of the method registerTraining - should return bad request when trainer with provided username does not exist")
    void testRegisterTraining_negative_notExistedTrainer() throws Exception {
        // given
        var trainingType = getTestTrainingType();
        var trainee = getTestTrainee();
        var trainer = getTestTrainer(trainingType);
        var training = getTestTraining(trainee, trainer, trainingType);

        var requestBody = objectMapper.writeValueAsString(trainingMapper.toDto(training));

        given(trainingService.registerNew(any(Training.class))).willThrow(new NoSuchEntityException("Trainer with username Jane.Smith not found"));

        // when & then
        var actualResult = mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        assertThat(contentAsString).isNotNull();
        assertThat(contentAsString).contains("Trainer with username Jane.Smith not found");

        verify(trainingService, times(1)).registerNew(any(Training.class));
        verifyNoMoreInteractions(trainingService);
    }

    private static Training getTestTraining(Trainee trainee, Trainer trainer, TrainingType trainingType) {
        var training = new Training();
        training.setId(1L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        training.setTrainingDuration(Duration.ofMinutes(60));
        return training;
    }

    private static Trainee getTestTrainee() {
        var trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("John.Doe");
        trainee.setPassword("password123");
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setActive(true);
        return trainee;
    }

    private static Trainer getTestTrainer(TrainingType trainingType) {
        var trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");
        trainer.setUsername("Jane.Smith");
        trainer.setPassword("password456");
        trainer.setSpecialization(trainingType);
        trainer.setActive(true);
        return trainer;
    }

    private static TrainingType getTestTrainingType() {
        var trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Fitness");
        return trainingType;
    }

    private static TrainingFilter getTestTrainingFilter() {
        var trainingFilter = new TrainingFilter();
        trainingFilter.setTraineeUsername("John.Doe");
        trainingFilter.setTrainerUsername("Jane.Smith");
        trainingFilter.setTrainingTypeName("Fitness");
        trainingFilter.setPeriodFrom(LocalDate.of(2000, 1, 1).atStartOfDay());
        trainingFilter.setPeriodTo(LocalDate.of(2025, 12, 31).atStartOfDay());
        return trainingFilter;
    }

}