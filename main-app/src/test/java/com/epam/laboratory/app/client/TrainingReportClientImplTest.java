package com.epam.laboratory.app.client;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.dto.TrainingReportRequestDto;
import com.epam.laboratory.app.dto.mapper.TrainingReportMapper;
import com.epam.laboratory.app.dto.mapper.TrainingReportMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.time.Duration.ofHours;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(TrainingReportMapperImpl.class)
@DisplayName("TrainingReportClientImpl test suite")
class TrainingReportClientImplTest {
    private static final String STATISTICS_SERVICE_URL = "http://localhost:8081";

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private TrainingReportMapper reportMapper;

    private TrainingReportClientImpl trainingReportClient;

    @BeforeEach
    void setUp() {
        trainingReportClient = new TrainingReportClientImpl(restTemplate, reportMapper);
        ReflectionTestUtils.setField(trainingReportClient, "statisticsServiceUrl", STATISTICS_SERVICE_URL);
    }


    // ==================== SEND TRAINING REPORT ADD TESTS ====================

    @Test
    @DisplayName("Test of the method sendTrainingReportAdd - should send training report with action type ADD to the statistics service")
    void testSendTrainingReportAdd_positive() {
        // given
        var training = createTestTraining();

        given(restTemplate.getInterceptors()).willReturn(new ArrayList<>());
        given(restTemplate.postForEntity(any(URI.class), any(TrainingReportRequestDto.class), eq(Void.class)))
                .willReturn(ResponseEntity.ok(null));

        // when
        trainingReportClient.sendTrainingReportAdd(training);

        // then
        verify(restTemplate, times(1)).getInterceptors();
        verify(restTemplate, times(1)).postForEntity(any(URI.class), any(TrainingReportRequestDto.class), eq(Void.class));
        verifyNoMoreInteractions(restTemplate);
    }


    // ==================== SEND TRAINING REPORT DELETE TESTS ====================

    @Test
    @DisplayName("Test of the method sendTrainingReportDelete - should send training report with action type DELETE to the statistics service")
    void testSendTrainingReportDelete_positive() {
        // given
        var training = createTestTraining();

        given(restTemplate.getInterceptors()).willReturn(new ArrayList<>());
        given(restTemplate.postForEntity(any(URI.class), any(TrainingReportRequestDto.class), eq(Void.class)))
                .willReturn(ResponseEntity.ok(null));

        // when
        trainingReportClient.sendTrainingReportDelete(training);

        // then
        verify(restTemplate, times(1)).getInterceptors();
        verify(restTemplate, times(1)).postForEntity(any(URI.class), any(TrainingReportRequestDto.class), eq(Void.class));
        verifyNoMoreInteractions(restTemplate);
    }

    private static Training createTestTraining() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");
        trainee.setUsername("FirstName.LastName");
        trainee.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setId(2L);
        trainer.setFirstName("FirstName");
        trainer.setLastName("LastName");
        trainer.setUsername("FirstName.LastName1");
        trainer.setActive(true);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Test Training Type");

        var training = new Training();
        training.setId(1L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Test Training");
        training.setTrainingType(trainingType);
        training.setTrainingDate(LocalDateTime.now());
        training.setTrainingDuration(ofHours(1));

        return training;
    }
}