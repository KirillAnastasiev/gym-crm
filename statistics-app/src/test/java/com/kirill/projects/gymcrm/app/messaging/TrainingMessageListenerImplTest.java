package com.kirill.projects.gymcrm.app.messaging;

import com.kirill.projects.gymcrm.app.domain.TrainerStatus;
import com.kirill.projects.gymcrm.app.domain.Training;
import com.kirill.projects.gymcrm.app.dto.TrainingRequestDto;
import com.kirill.projects.gymcrm.app.dto.mapper.TrainingRequestMapper;
import com.kirill.projects.gymcrm.app.dto.mapper.TrainingRequestMapperImpl;
import com.kirill.projects.gymcrm.app.service.TrainingStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingMessageListenerImpl test suite")
class TrainingMessageListenerImplTest {

    @Mock
    private TrainingStatisticsService trainingStatisticsService;

    private TrainingRequestMapper trainingRequestMapper = new TrainingRequestMapperImpl();

    private TrainingMessageListenerImpl trainingMessageListenerImpl;

    @BeforeEach
    void setUp() {
        trainingMessageListenerImpl = new TrainingMessageListenerImpl(trainingRequestMapper, trainingStatisticsService);
    }


    // ==================== RECEIVE TRAINING TESTS ====================

    @Test
    @DisplayName("Test of the method receiveTraining - should save training when action type is ADD")
    void testReceiveTraining_positive_addTraining() {
        // given
        var trainingRequestDto = createTestTrainingRequestDto();
        var training = createTestTraining();
        var headers = new MessageHeaders(Map.of(
                "X-Action-Type", "ADD",
                "X-Request-ID", "a533787d-7ac7-4dc5-b378-7d7ac79dc51f"
        ));
        var message = new GenericMessage<>(trainingRequestDto, headers);

        doNothing().when(trainingStatisticsService).saveTrainingStatisticsForTraining(any(Training.class));

        // when
        trainingMessageListenerImpl.receiveTraining(message);

        // then
        verify(trainingStatisticsService, times(1)).saveTrainingStatisticsForTraining(any(Training.class));
        verifyNoMoreInteractions(trainingStatisticsService);
    }

    @Test
    @DisplayName("Test of the method receiveTraining - should delete training when action type is DELETE")
    void testReceiveTraining_positive_deleteTraining() {
        // given
        var trainingRequestDto = createTestTrainingRequestDto();
        var headers = new MessageHeaders(Map.of(
                "X-Action-Type", "DELETE",
                "X-Request-ID", "a533787d-7ac7-4dc5-b378-7d7ac79dc51f"
        ));
        var message = new GenericMessage<>(trainingRequestDto, headers);

        doNothing().when(trainingStatisticsService).deleteTrainingStatisticsForTraining(any(Training.class));

        // when
        trainingMessageListenerImpl.receiveTraining(message);

        // then
        verify(trainingStatisticsService, times(1)).deleteTrainingStatisticsForTraining(any(Training.class));
        verifyNoMoreInteractions(trainingStatisticsService);
    }

    @Test
    @DisplayName("Test of the method receiveTraining - should throw exception when action type is unknown")
    void testReceiveTraining_negative_unknownActionType() {
        // given
        var trainingRequestDto = createTestTrainingRequestDto();
        var headers = new MessageHeaders(Map.of(
                "X-Action-Type", "UNKNOWN",
                "X-Request-ID", "a533787d-7ac7-4dc5-b378-7d7ac79dc51f"
        ));
        var message = new GenericMessage<>(trainingRequestDto, headers);

        // when & then
        assertThatThrownBy(() -> trainingMessageListenerImpl.receiveTraining(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown action type: UNKNOWN");
        verifyNoInteractions(trainingStatisticsService);

        verifyNoInteractions(trainingStatisticsService);
    }

    private static TrainingRequestDto createTestTrainingRequestDto() {
        return new TrainingRequestDto(
                "Sarah.Davis",
                "Sarah",
                "Davis",
                true,
                LocalDateTime.of(2025, 12, 15, 12, 45),
                Duration.ofMinutes(30)
        );
    }

    private static Training createTestTraining() {
        var training = new Training();
        training.setTrainerFirstName("Sarah");
        training.setTrainerLastName("Davis");
        training.setTrainerUsername("Sarah.Davis");
        training.setTrainerStatus(TrainerStatus.ACTIVE);
        training.setTrainingDate(LocalDateTime.of(2025, 12, 15, 12, 45));
        training.setTrainingDuration(Duration.ofMinutes(30));
        return training;
    }

}