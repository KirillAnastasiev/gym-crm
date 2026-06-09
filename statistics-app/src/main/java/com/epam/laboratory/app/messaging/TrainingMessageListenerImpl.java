package com.epam.laboratory.app.messaging;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.dto.TrainingRequestDto;
import com.epam.laboratory.app.dto.mapper.TrainingRequestMapper;
import com.epam.laboratory.app.repository.TrainingDao;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
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
        var actionType = headers.get(ACTION_TYPE_HEADER);
        doReceiveTraining(trainingRequestMessage, actionType.toString());
    }

    private void doReceiveTraining(Message<TrainingRequestDto> trainingRequestMessage, String actionType) {
        var training = trainingRequestMapper.toEntity(trainingRequestMessage.getPayload());
        switch (actionType) {
            case "ADD" -> trainingDao.save(training);
            case "DELETE" -> trainingDao.delete(training);
            default -> throw new IllegalArgumentException("Unknown action type: " + actionType);
        }
    }
}
