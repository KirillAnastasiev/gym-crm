package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.config.TestSecurityConfig;
import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.domain.UserCredentials;
import com.epam.laboratory.app.dto.CredentialsDto;
import com.epam.laboratory.app.dto.TraineeDto;
import com.epam.laboratory.app.dto.TrainerDto;
import com.epam.laboratory.app.dto.UserDto;
import com.epam.laboratory.app.dto.mapper.*;
import com.epam.laboratory.app.exception.NoSuchEntityException;
import com.epam.laboratory.app.exception.RestExceptionHandler;
import com.epam.laboratory.app.security.JwtAuthenticationConverter;
import com.epam.laboratory.app.service.TrainerService;
import com.epam.laboratory.app.service.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
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

@WebMvcTest(TrainerController.class)
@Import({
        TestSecurityConfig.class,
        TrainerController.class,
        TrainerMapperImpl.class,
        TrainingTypeMapperImpl.class,
        CredentialsMapperImpl.class,
        TraineeWithoutTrainersMapperImpl.class,
        RestExceptionHandler.class
})

@DisplayName("TrainerController test suite")
class TrainerControllerTest {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private TrainerMapper trainerMapper;

    @Autowired
    private CredentialsMapper credentialsMapper;

    @Autowired
    private TraineeWithoutTrainersMapper traineeWithoutTrainersMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationConverter converter;

    @MockitoBean
    private AuthenticationManager authenticationManager;


    // ==================== GET PROFILE ENDPOINT TESTS ====================

    @Test
    @WithMockUser
    @DisplayName("Test of the method getProfile - should return trainer profile when trainer with given username exists")
    void testGetProfile_positive() throws Exception {
        // given
        var trainer = getTestTrainer();
        var trainee = getTestTrainee();
        trainer.addTrainee(trainee);
        var expectedResponse = trainerMapper.toDto(trainer);
        var requestId = "30f0e50b-7b04-4842-b0e5-0b7b046842f8";

        given(trainerService.selectByUsername(anyString())).willReturn(trainer);

        // when & then
        var actualResult = mockMvc.perform(get("/api/trainers/{username}", "Jane.Smith")
                        .header(REQUEST_ID_HEADER, requestId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        var response = objectMapper.readValue(contentAsString, TrainerDto.class);

        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(trainerService, times(1)).selectByUsername(anyString());
        verifyNoMoreInteractions(trainerService);
    }

    @Test
    @WithMockUser
    @DisplayName("Test of the method getProfile - should return error message when trainer with given username does not exist")
    void testGetProfile_negative_trainerNotFound() throws Exception {
        // given
        var requestId = "30f0e50b-7b04-4842-b0e5-0b7b046842f8";

        given(trainerService.selectByUsername(anyString())).willThrow(new NoSuchEntityException("Trainer with username NonExistentTrainer not found"));

        // when & then
        mockMvc.perform(get("/api/trainers/{username}", "NonExistentTrainer")
                        .header(REQUEST_ID_HEADER, requestId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(content().json("{\"detail\":\"Trainer with username NonExistentTrainer not found\"}"));

        verify(trainerService, times(1)).selectByUsername(anyString());
        verifyNoMoreInteractions(trainerService);
    }


    // ==================== REGISTER TRAINER ENDPOINT TESTS ====================

    @Test
    @WithMockUser
    @DisplayName("Test of the method registerTrainer - should return credentials of registered trainer when request body is valid")
    void testRegisterTrainer_positive() throws Exception {
        // given
        var trainer = getTestTrainer();
        var credentials = getTestCredentials();
        var requestBody = objectMapper.writeValueAsString(trainerMapper.toDto(trainer));
        var expectedResponse = credentialsMapper.toDto(credentials);
        var requestId = "30f0e50b-7b04-4842-b0e5-0b7b046842f8";

        given(trainerService.registerNew(any(Trainer.class))).willReturn(credentials);

        // when & then
        var actualResult = mockMvc.perform(post("/api/trainers")
                        .header(REQUEST_ID_HEADER, requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        var response = objectMapper.readValue(contentAsString, CredentialsDto.class);

        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(trainerService, times(1)).registerNew(any(Trainer.class));
        verifyNoMoreInteractions(trainerService);
    }


    // ==================== UPDATE TRAINER ENDPOINT TESTS ====================

    @Test
    @WithMockUser
    @DisplayName("Test of the method updateTrainer - should return updated trainer profile when request body is valid and trainer with given username exists")
    void testUpdateTrainer_positive() throws Exception {
        // given
        var trainer = getTestTrainer();
        var trainee = getTestTrainee();

        trainer.addTrainee(trainee);
        trainer.setFirstName("UpdatedFirstName");
        var requestBody = objectMapper.writeValueAsString(trainerMapper.toDto(trainer));
        var expectedResponse = trainerMapper.toDto(trainer);
        var requestId = "30f0e50b-7b04-4842-b0e5-0b7b046842f8";

        given(trainerService.updateByUsername(anyString(), any(Trainer.class))).willReturn(trainer);

        // when & then
        var actualResult = mockMvc.perform(put("/api/trainers/{username}", "Jane.Smith")
                        .header(REQUEST_ID_HEADER, requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        var response = objectMapper.readValue(contentAsString, TrainerDto.class);

        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(trainerService, times(1)).updateByUsername(anyString(), any(Trainer.class));
        verifyNoMoreInteractions(trainerService);
    }


    // ==================== DELETE TRAINER ENDPOINT TESTS ====================

    @Test
    @WithMockUser
    @DisplayName("Test of the method deleteTrainer - should return success message when trainer with given username was successfully deleted")
    void testDeleteTrainer_positive() throws Exception {
        // given
        var requestId = "30f0e50b-7b04-4842-b0e5-0b7b046842f8";

        doNothing().when(trainerService).deleteByUsername(anyString());

        // when & then
        mockMvc.perform(delete("/api/trainers/{username}", "Jane.Smith")
                        .header(REQUEST_ID_HEADER, requestId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\":\"Trainer with username Jane.Smith was deleted\"}"));

        verify(trainerService, times(1)).deleteByUsername(anyString());
        verifyNoMoreInteractions(trainerService);
    }

    @Test
    @WithMockUser
    @DisplayName("Test of the method deleteTrainer - should return error message when trainer with given username does not exist")
    void testDeleteTrainer_negative_trainerNotFound() throws Exception {
        // given
        var requestId = "30f0e50b-7b04-4842-b0e5-0b7b046842f8";

        doThrow(new NoSuchEntityException("Trainer with username NonExistentTrainer not found"))
                .when(trainerService).deleteByUsername(anyString());

        // when & then
        mockMvc.perform(delete("/api/trainers/{username}", "NonExistentTrainer")
                        .header(REQUEST_ID_HEADER, requestId)
                        .header("Authorization", "Bearer valid.jwt.token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(content().json("{\"detail\":\"Trainer with username NonExistentTrainer not found\"}"));

        verify(trainerService, times(1)).deleteByUsername(anyString());
        verifyNoMoreInteractions(trainerService);
    }


    // ==================== CHANGE TRAINER STATUS ENDPOINT TESTS ====================

    @Test
    @WithMockUser
    @DisplayName("Test of the method changeTrainerStatus - should return success message when trainer with given username was successfully blocked")
    void testChangeTrainerStatus_positive() throws Exception {
        // given
        var requestId = "30f0e50b-7b04-4842-b0e5-0b7b046842f8";

        doNothing().when(trainerService).changeStatus(anyString(), anyBoolean());

        // when & then
        mockMvc.perform(patch("/api/trainers/{username}", "Jane.Smith")
                        .header(REQUEST_ID_HEADER, requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\":\"Trainer with username Jane.Smith was blocked\"}"));

        verify(trainerService, times(1)).changeStatus(anyString(), anyBoolean());
        verifyNoMoreInteractions(trainerService);
    }

    @Test
    @WithMockUser
    @DisplayName("Test of the method changeTrainerStatus - should return error message when trainer with given username does not exist")
    void testChangeTrainerStatus_negative_trainerNotFound() throws Exception {
        // given
        var requestId = "30f0e50b-7b04-4842-b0e5-0b7b046842f8";

        doThrow(new NoSuchEntityException("Trainer with username NonExistentTrainer not found"))
                .when(trainerService).changeStatus(anyString(), anyBoolean());

        // when & then
        mockMvc.perform(patch("/api/trainers/{username}", "NonExistentTrainer")
                        .header(REQUEST_ID_HEADER, requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(content().json("{\"detail\":\"Trainer with username NonExistentTrainer not found\"}"));

        verify(trainerService, times(1)).changeStatus(anyString(), anyBoolean());
        verifyNoMoreInteractions(trainerService);
    }


    // ==================== UPDATE TRAINER TRAINEES ENDPOINT TESTS ====================

    @Test
    @WithMockUser
    @DisplayName("Test of the method updateTrainerTrainees - should return updated list of trainer's trainees when request body is valid and trainer with given username exists")
    void testUpdateTrainerTrainees_positive() throws Exception {
        // given
        var traineeDto = new UserDto("John.Doe");
        var trainee = getTestTrainee();
        var requestBody = objectMapper.writeValueAsString(Collections.singletonList(traineeDto));
        var requestId = "30f0e50b-7b04-4842-b0e5-0b7b046842f8";

        given(trainerService.updateTrainees(anyString(), anyCollection())).willReturn(Collections.singletonList(trainee));

        // when & then
        var actualResult = mockMvc.perform(put("/api/trainers/{username}/trainees", "Jane.Smith")
                        .header(REQUEST_ID_HEADER, requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var contentAsString = actualResult.getResponse().getContentAsString();
        Collection<TraineeDto> response = objectMapper.readValue(contentAsString,
                objectMapper.getTypeFactory().constructCollectionType(Collection.class, TraineeDto.class));

        assertThat(response).isNotNull();
        assertThat(response).isInstanceOf(Collection.class);
        assertThat(response).isNotEmpty();
        assertThat(response).contains(traineeWithoutTrainersMapper.toDto(trainee));

        verify(trainerService, times(1)).updateTrainees(anyString(), anyCollection());
        verifyNoMoreInteractions(trainerService);
    }

    @Test
    @WithMockUser
    @DisplayName("Test of the method updateTrainerTrainees - should return error message when trainer with given username does not exist")
    void testUpdateTrainerTrainees_negative_trainerNotFound() throws Exception {
        // given
        var traineeDto = new UserDto("John.Doe");
        var requestBody = objectMapper.writeValueAsString(Collections.singletonList(traineeDto));
        var requestId = "30f0e50b-7b04-4842-b0e5-0b7b046842f8";

        doThrow(new NoSuchEntityException("Trainer with username NonExistentTrainer not found"))
                .when(trainerService).updateTrainees(anyString(), anyCollection());

        // when & then
        mockMvc.perform(put("/api/trainers/{username}/trainees", "NonExistentTrainer")
                        .header(REQUEST_ID_HEADER, requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(content().json("{\"detail\":\"Trainer with username NonExistentTrainer not found\"}"));

        verify(trainerService, times(1)).updateTrainees(anyString(), anyCollection());
        verifyNoMoreInteractions(trainerService);
    }

    @Test
    @WithMockUser
    @DisplayName("Test of the method updateTrainerTrainees - should return error message when trainee with given username does not exist")
    void testUpdateTrainerTrainees_negative_traineeNotFound() throws Exception {
        // given
        var traineeDto = new UserDto("NonExistentTrainee");
        var requestBody = objectMapper.writeValueAsString(Collections.singletonList(traineeDto));
        var requestId = "30f0e50b-7b04-4842-b0e5-0b7b046842f8";

        doThrow(new NoSuchEntityException("Trainee with username NonExistentTrainee not found"))
                .when(trainerService).updateTrainees(anyString(), anyCollection());

        // when & then
        mockMvc.perform(put("/api/trainers/{username}/trainees", "Jane.Smith")
                        .header(REQUEST_ID_HEADER, requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(content().json("{\"detail\":\"Trainee with username NonExistentTrainee not found\"}"));

        verify(trainerService, times(1)).updateTrainees(anyString(), anyCollection());
        verifyNoMoreInteractions(trainerService);
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

    private static UserCredentials getTestCredentials() {
        var userCredentials = new UserCredentials();
        userCredentials.setUsername("John.Doe");
        userCredentials.setPassword("password123");
        return userCredentials;
    }

}