package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.dto.mapper.TrainingRequestMapperImpl;
import com.epam.laboratory.app.exception.RestExceptionHandler;
import com.epam.laboratory.app.service.TrainingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingController.class)
@Import({
        TrainingRequestMapperImpl.class,
        RestExceptionHandler.class,
})
@DisplayName("TrainingController test suite")
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainingService trainingService;


    // ==================== NEW TRAINING REQUEST ENDPOINT TESTS ====================

    @Test
    @DisplayName("Test of the method newTrainingRequest - should return 201 Created when valid request is sent")
    void testNewTrainingRequest_positive_addRequest() throws Exception {
        // given
        var requestId = "53f9405d-eaa2-43cb-b940-5deaa263cb33";

        // when & then
        mockMvc.perform(post("/training")
                            .header("X-Request-ID", requestId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "trainerUsername": "Sarah.Davis",
                                        "trainerFirstName": "Sarah",
                                        "trainerLastName": "Davis",
                                        "trainerActive": true,
                                        "trainingDate": "2025-12-15T12:45:00",
                                        "trainingDuration": "PT30M",
                                        "actionType": "ADD"
                                    }
                                    """))
                .andExpect(status().isCreated());

        verify(trainingService, times(1)).addTraining(ArgumentMatchers.any(Training.class));
        verifyNoMoreInteractions(trainingService);
    }

    @Test
    @DisplayName("Test of the method newTrainingRequest - should return 201 Created when valid delete request is sent")
    void testNewTrainingRequest_positive_deleteRequest() throws Exception {
        // given
        var requestId = "53f9405d-eaa2-43cb-b940-5deaa263cb33";

        // when & then
        mockMvc.perform(post("/training")
                            .header("X-Request-ID", requestId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "trainerUsername": "Sarah.Davis",
                                        "trainerFirstName": "Sarah",
                                        "trainerLastName": "Davis",
                                        "trainerActive": true,
                                        "trainingDate": "2025-12-15T12:45:00",
                                        "trainingDuration": "PT30M",
                                        "actionType": "DELETE"
                                    }
                                    """))
                .andExpect(status().isCreated());

        verify(trainingService, times(1)).deleteTraining(ArgumentMatchers.any(Training.class));
        verifyNoMoreInteractions(trainingService);
    }

    @Test
    @DisplayName("Test of the method newTrainingRequest - should return 400 Bad Request when request with validation error is sent")
    void testNewTrainingRequest_negative_validationError() throws Exception {
        // given
        var requestId = "53f9405d-eaa2-43cb-b940-5deaa263cb33";

        // when & then
        mockMvc.perform(post("/training")
                            .header("X-Request-ID", requestId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "trainerUsername": "",
                                        "trainerFirstName": "Sarah",
                                        "trainerLastName": "Davis",
                                        "trainerActive": true,
                                        "trainingDate": "2025-12-15T12:45:00",
                                        "trainingDuration": "PT30M",
                                        "actionType": "ADD"
                                    }
                                    """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect(content().json("{\"detail\":\"Invalid request content.\"}"));
    }

    @Test
    @DisplayName("Test of the method newTrainingRequest - should return 400 Bad Request when request with unknown action type is sent")
    void testNewTrainingRequest_negative_unknownActionType() throws Exception {
        // given
        var requestId = "53f9405d-eaa2-43cb-b940-5deaa263cb33";

        // when & then
        mockMvc.perform(post("/training")
                        .header("X-Request-ID", requestId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "trainerUsername": "Sarah.Davis",
                                        "trainerFirstName": "Sarah",
                                        "trainerLastName": "Davis",
                                        "trainerActive": true,
                                        "trainingDate": "2025-12-15T12:45:00",
                                        "trainingDuration": "PT30M",
                                        "actionType": "UNKNOWN"
                                    }
                                    """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect(content().json("{\"detail\":\"Unsupported training report action type: UNKNOWN\"}"));

        verifyNoInteractions(trainingService);
    }

}