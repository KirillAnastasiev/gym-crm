package com.epam.laboratory.app.messaging;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.dto.TrainingRequestDto;
import com.epam.laboratory.app.dto.mapper.TrainingRequestMapper;
import com.epam.laboratory.app.repository.TrainingDao;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TrainingMessageListenerImpl implements TrainingMessageListener {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String ACTION_TYPE_HEADER = "X-Action-Type";

    private final TrainingRequestMapper trainingRequestMapper;
    private final TrainingDao trainingDao;

    @RabbitListener(queues = "${spring.rabbitmq.queue.name}")
    @Logging(Level.INFO)
    @Override
    public void receiveTraining(Message<TrainingRequestDto> trainingRequestMessage) {
        var headers = trainingRequestMessage.getHeaders();
        var actionType = getActionType(headers);
        var trainingRequestDto = trainingRequestMessage.getPayload();
        doReceiveTraining(trainingRequestDto, actionType);
    }

    private void doReceiveTraining(@Valid TrainingRequestDto trainingRequestDto, String actionType) {
        var training = trainingRequestMapper.toEntity(trainingRequestDto);
        switch (actionType) {
            case "ADD" -> trainingDao.save(training);
            case "DELETE" -> trainingDao.delete(training);
            default -> throw new IllegalArgumentException("Unknown action type: " + actionType);
        }
    }

    private static String getActionType(MessageHeaders headers) {
        return Optional.ofNullable(headers.get(ACTION_TYPE_HEADER))
                .map(Object::toString)
                .orElseThrow(() -> new IllegalArgumentException("Missing header: " + ACTION_TYPE_HEADER));
    }
}
