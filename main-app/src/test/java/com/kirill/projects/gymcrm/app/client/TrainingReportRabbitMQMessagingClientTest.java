package com.kirill.projects.gymcrm.app.client;

import com.kirill.projects.gymcrm.app.domain.*;
import com.kirill.projects.gymcrm.app.dto.mapper.TrainingReportMapper;
import com.kirill.projects.gymcrm.app.dto.mapper.TrainingReportMapperImpl;
import com.kirill.projects.gymcrm.app.util.RequestIdHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingReportRabbitMQMessagingClient test suite")
class TrainingReportRabbitMQMessagingClientTest {

    private static final String REQUEST_ID = "88478acf-0d45-4c5b-878a-cf0d45cc5b09";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String ACTION_TYPE_HEADER = "X-Action-Type";

    @Mock
    private RabbitTemplate rabbitTemplate;

    private TrainingReportMapper trainingReportMapper = new TrainingReportMapperImpl();

    @InjectMocks
    private TrainingReportRabbitMQMessagingClient trainingReportMessagingClient;

    @BeforeEach
    void setUp() {
        RequestIdHolder.setRequestId(REQUEST_ID);
        trainingReportMessagingClient = new TrainingReportRabbitMQMessagingClient(rabbitTemplate, trainingReportMapper);
    }

    @AfterEach
    void tearDown() {
        RequestIdHolder.clearRequestId();
    }

    // ==================== SEND TRAINING REPORT ADD TESTS ====================

    @Test
    @DisplayName("Test of the method sendTrainingReportAdd - should send training report with ADD action type and correct request ID")
    void testSendTrainingReportAdd_positive() {
        // given
        var training = createTestTraining();
        var trainingReport = createTestTrainingReport();
        var trainingReportDto = trainingReportMapper.toDto(trainingReport);
        var message = new Message(new byte[0], new MessageProperties());

        doAnswer(invocation -> {
            var messagePostProcessor = (MessagePostProcessor) invocation.getArgument(1);
            messagePostProcessor.postProcessMessage(message);
            return null;
        }).when(rabbitTemplate).convertAndSend(eq(trainingReportDto), any());

        // when
        trainingReportMessagingClient.sendTrainingReportAdd(training);

        // then
        assertThat(message.getMessageProperties().getHeaders()).containsKey(REQUEST_ID_HEADER);
        assertThat(message.getMessageProperties().getHeaders()).containsKey(ACTION_TYPE_HEADER);
        assertThat(message.getMessageProperties().getHeaders().get(REQUEST_ID_HEADER)).isEqualTo(REQUEST_ID);
        assertThat(message.getMessageProperties().getHeaders().get(ACTION_TYPE_HEADER)).isEqualTo(TrainingReport.ActionType.ADD);

        verify(rabbitTemplate, times(1)).convertAndSend(eq(trainingReportDto), any());
        verifyNoMoreInteractions(rabbitTemplate);
    }

    @Test
    @DisplayName("Test of the method sendTrainingReportAdd - should throw IllegalArgumentException when training is null")
    void testSendTrainingReportAdd_negative_nullValue() {
        // when & then
        assertThatThrownBy(() -> trainingReportMessagingClient.sendTrainingReportAdd(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training must not be null");

        verifyNoInteractions(rabbitTemplate);
    }


    // ==================== SEND TRAINING REPORT DELETE TESTS ====================

    @Test
    @DisplayName("Test of the method sendTrainingReportDelete - should send training report with DELETE action type")
    void testSendTrainingReportDelete_positive() {
        // given
        var training = createTestTraining();
        var trainingReport = createTestTrainingReport();
        var trainingReportDto = trainingReportMapper.toDto(trainingReport);
        var message = new Message(new byte[0], new MessageProperties());

        doAnswer(invocation -> {
            var messagePostProcessor = (MessagePostProcessor) invocation.getArgument(1);
            messagePostProcessor.postProcessMessage(message);
            return null;
        }).when(rabbitTemplate).convertAndSend(eq(trainingReportDto), any());

        // when
        trainingReportMessagingClient.sendTrainingReportDelete(training);

        // then
        assertThat(message.getMessageProperties().getHeaders()).containsKey(REQUEST_ID_HEADER);
        assertThat(message.getMessageProperties().getHeaders()).containsKey(ACTION_TYPE_HEADER);
        assertThat(message.getMessageProperties().getHeaders().get(REQUEST_ID_HEADER)).isEqualTo(REQUEST_ID);
        assertThat(message.getMessageProperties().getHeaders().get(ACTION_TYPE_HEADER)).isEqualTo(TrainingReport.ActionType.DELETE);

        verify(rabbitTemplate, times(1)).convertAndSend(eq(trainingReportDto), any());
        verifyNoMoreInteractions(rabbitTemplate);
    }

    @Test
    @DisplayName("Test of the method sendTrainingReportDelete - should throw IllegalArgumentException when training is null")
    void testSendTrainingReportDelete_negative_nullValue() {
        // when & then
        assertThatThrownBy(() -> trainingReportMessagingClient.sendTrainingReportDelete(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training must not be null");

        verifyNoInteractions(rabbitTemplate);
    }


    private static Training createTestTraining() {
        var trainingType = new TrainingType();
        trainingType.setId(2L);
        trainingType.setTrainingTypeName("Yoga");

        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("John.Doe");
        trainee.setActive(true);

        var trainer = new Trainer();
        trainer.setId(5L);
        trainer.setFirstName("Sarah");
        trainer.setLastName("Davis");
        trainer.setUsername("Sarah.Davis");
        trainer.setActive(true);

        var training = new Training();
        training.setId(1L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingName("Soft yoga");
        training.setTrainingDate(LocalDateTime.of(2025, 12, 15, 12, 45, 0));
        training.setTrainingDuration(Duration.ofMinutes(30));

        return training;
    }

    private static TrainingReport createTestTrainingReport() {
        var trainingReport = new TrainingReport();
        trainingReport.setTrainerFirsName("Sarah");
        trainingReport.setTrainerLastName("Davis");
        trainingReport.setTrainerUsername("Sarah.Davis");
        trainingReport.setIsActive(true);
        trainingReport.setTrainingDate(LocalDateTime.of(2025, 12, 15, 12, 45, 0));
        trainingReport.setTrainingDuration(Duration.ofMinutes(30));
        return trainingReport;
    }

}