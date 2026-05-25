package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.dto.CredentialsDto;
import com.epam.laboratory.app.dto.TraineeDto;
import com.epam.laboratory.app.dto.TrainerDto;
import com.epam.laboratory.app.dto.UserDto;
import com.epam.laboratory.app.dto.mapper.*;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.exception.RestExceptionHandler;
import com.epam.laboratory.app.service.TraineeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {
        TraineeController.class,
        TraineeMapperImpl.class,
        TraineeCredentialsMapperImpl.class,
        TrainerWithoutTraineesMapperImpl.class,
        TrainingTypeMapperImpl.class,
        RestExceptionHandler.class,
})
@DisplayName("TraineeController test suite")
class TraineeControllerTest {

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private TraineeMapper traineeMapper;

    @Autowired
    private TraineeCredentialsMapper credentialsMapper;

    @Autowired
    private TrainerWithoutTraineesMapper trainerWithoutTraineesMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TraineeService traineeService;


    // ==================== GET PROFILE ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method getProfile - should return trainee profile when trainee exists")
    void testGetProfile_positive() throws Exception {
        // given
        var trainee = getTestTrainee();
        var trainer = getTestTrainer();
        trainee.addTrainer(trainer);
        var expectedResponse = traineeMapper.toDto(trainee);

        given(traineeService.selectByUsername(anyString())).willReturn(trainee);

        // when & then
        var actualResult = mockMvc.perform(get("/api/trainees/{username}", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        var response = objectMapper.readValue(contentAsString, TraineeDto.class);

        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(traineeService, times(1)).selectByUsername(anyString());
        verifyNoMoreInteractions(traineeService);
    }

    @Test
    @DisplayName("Test of the method getProfile - should return 404 Not Found when trainee with given username does not exist")
    void testGetProfile_negative_traineeNotFound() throws Exception {
        // given
        given(traineeService.selectByUsername(anyString())).willThrow(new NoSuchEntityException("Trainee with username NonExistentUsername not found"));

        // when & then
        mockMvc.perform(get("/api/trainees/{username}", "NonExistentUsername")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(content().json("{\"detail\":\"Trainee with username NonExistentUsername not found\"}"));

        verify(traineeService, times(1)).selectByUsername(anyString());
        verifyNoMoreInteractions(traineeService);
    }


    // ==================== REGISTER TRAINEE ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method registerTrainee - should register new trainee and return credentials when request body is valid")
    void testRegisterTrainee_positive() throws Exception {
        // given
        var trainee = getTestTrainee();
        var requestBody = objectMapper.writeValueAsString(traineeMapper.toDto(trainee));
        var expectedResponse = credentialsMapper.toDto(trainee);

        given(traineeService.registerNew(any(Trainee.class))).willReturn(trainee);

        // when & then
        var actualResult = mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        var response = objectMapper.readValue(contentAsString, CredentialsDto.class);

        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(traineeService, times(1)).registerNew(any(Trainee.class));
        verifyNoMoreInteractions(traineeService);
    }


    // ==================== UPDATE TRAINEE ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method updateTrainee - should update trainee and return updated trainee when request body is valid and trainee with given username exists")
    void testUpdateTrainee_positive() throws Exception {
        // given
        var trainee = getTestTrainee();
        var trainer = getTestTrainer();

        trainee.addTrainer(trainer);
        trainee.setFirstName("UpdatedFirstName");
        var requestBody = objectMapper.writeValueAsString(traineeMapper.toDto(trainee));
        var expectedResponse = traineeMapper.toDto(trainee);

        given(traineeService.updateByUsername(anyString(), any(Trainee.class))).willReturn(trainee);

        // when & then
        var actualResult = mockMvc.perform(put("/api/trainees/{username}", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        var response = objectMapper.readValue(contentAsString, TraineeDto.class);

        assertThat(response).isNotNull();
        assertThat(response.firstName()).isEqualTo("UpdatedFirstName");
        assertThat(response).isEqualTo(expectedResponse);

        verify(traineeService, times(1)).updateByUsername(anyString(), any(Trainee.class));
        verifyNoMoreInteractions(traineeService);
    }


    // ==================== DELETE TRAINEE ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method deleteTrainee - should delete trainee and return confirmation message when trainee with given username exists")
    void testDeleteTrainee_positive() throws Exception {
        // given
        doNothing().when(traineeService).deleteByUsername(anyString());

        // when & then
        mockMvc.perform(delete("/api/trainees/{username}", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Trainee with username John.Doe was deleted"));

        verify(traineeService, times(1)).deleteByUsername(anyString());
        verifyNoMoreInteractions(traineeService);
    }

    @Test
    @DisplayName("Test of the method deleteTrainee - should return 404 Not Found when trainee with given username does not exist")
    void testDeleteTrainee_negative_traineeNotFound() throws Exception {
        // given
        doThrow(new NoSuchEntityException("Trainee with username NonExistentUsername not found"))
                .when(traineeService).deleteByUsername(anyString());

        // when & then
        mockMvc.perform(delete("/api/trainees/{username}", "NonExistentUsername")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(content().json("{\"detail\":\"Trainee with username NonExistentUsername not found\"}"));

        verify(traineeService, times(1)).deleteByUsername(anyString());
        verifyNoMoreInteractions(traineeService);
    }


    // ==================== CHANGE TRAINEE STATUS ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method changeTraineeStatus - should change trainee status and return confirmation message when trainee with given username exists")
    void testChangeTraineeStatus_positive() throws Exception {
        // given
        doNothing().when(traineeService).changeStatus(anyString(), anyBoolean());

        // when & then
        mockMvc.perform(patch("/api/trainees/{username}", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Trainee with username John.Doe was blocked"));

        verify(traineeService, times(1)).changeStatus(anyString(), eq(false));
        verifyNoMoreInteractions(traineeService);
    }

    @Test
    @DisplayName("Test of the method changeTraineeStatus - should return 404 Not Found when trainee with given username does not exist")
    void testChangeTraineeStatus_negative_traineeNotFound() throws Exception {
        // given
        doThrow(new NoSuchEntityException("Trainee with username NonExistentUsername not found"))
                .when(traineeService).changeStatus(anyString(), anyBoolean());

        // when & then
        mockMvc.perform(patch("/api/trainees/{username}", "NonExistentUsername")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(content().json("{\"detail\":\"Trainee with username NonExistentUsername not found\"}"));

        verify(traineeService, times(1)).changeStatus(anyString(), anyBoolean());
        verifyNoMoreInteractions(traineeService);
    }


    // ==================== UPDATE TRAINEE TRAINERS ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method updateTraineeTrainers - should update trainee trainers and return updated trainers when request body is valid and trainee with given username exists")
    void testUpdateTraineeTrainers_positive() throws Exception {
        // given
        var trainerDto = new UserDto("Jane.Smith");
        var trainer = getTestTrainer();
        var requestBody = objectMapper.writeValueAsString(Collections.singletonList(trainerDto));

        given(traineeService.updateTrainers(anyString(), anyCollection())).willReturn(Collections.singletonList(trainer));

        // when & then
        var actualResult = mockMvc.perform(put("/api/trainees/{username}/trainers", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        Collection<TrainerDto> response = objectMapper.readValue(contentAsString,
                objectMapper.getTypeFactory().constructCollectionType(Collection.class, TrainerDto.class));

        assertThat(response).isNotNull();
        assertThat(response).isInstanceOf(Collection.class);
        assertThat(response).isNotEmpty();
        assertThat(response).contains(trainerWithoutTraineesMapper.toDto(trainer));

        verify(traineeService, times(1)).updateTrainers(anyString(), anyCollection());
        verifyNoMoreInteractions(traineeService);
    }

    @Test
    @DisplayName("Test of the method updateTraineeTrainers - should return 404 Not Found when trainee with given username does not exist")
    void testUpdateTraineeTrainers_negative_traineeNotFound() throws Exception {
        // given
        var trainerDto = new UserDto("Jane.Smith");
        var requestBody = objectMapper.writeValueAsString(Collections.singletonList(trainerDto));

        doThrow(new NoSuchEntityException("Trainee with username NonExistentUsername not found"))
                .when(traineeService).updateTrainers(anyString(), anyCollection());

        // when & then
        mockMvc.perform(put("/api/trainees/{username}/trainers", "NonExistentUsername")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(content().json("{\"detail\":\"Trainee with username NonExistentUsername not found\"}"));

        verify(traineeService, times(1)).updateTrainers(anyString(), anyCollection());
        verifyNoMoreInteractions(traineeService);
    }

    @Test
    @DisplayName("Test of the method updateTraineeTrainers - should return 404 Not Found when trainer with given username does not exist")
    void testUpdateTraineeTrainers_negative_trainerNotFound() throws Exception {
        // given
        var trainerDto = new UserDto("NonExistentTrainer");
        var requestBody = objectMapper.writeValueAsString(Collections.singletonList(trainerDto));

        doThrow(new NoSuchEntityException("Trainer with username NonExistentTrainer not found"))
                .when(traineeService).updateTrainers(anyString(), anyCollection());

        // when & then
        mockMvc.perform(put("/api/trainees/{username}/trainers", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(content().json("{\"detail\":\"Trainer with username NonExistentTrainer not found\"}"));

        verify(traineeService, times(1)).updateTrainers(anyString(), anyCollection());
        verifyNoMoreInteractions(traineeService);
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

    private static Trainer getTestTrainer() {
        var trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Fitness");

        var trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");
        trainer.setUsername("Jane.Smith");
        trainer.setPassword("password456");
        trainer.setSpecialization(trainingType);
        trainer.setActive(true);
        return trainer;
    }
}