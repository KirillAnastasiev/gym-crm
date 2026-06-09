package com.epam.laboratory.app.messaging;

import com.epam.laboratory.app.dto.TrainingRequestDto;
import org.springframework.messaging.Message;

public interface TrainingMessageListener {
    void receiveTraining(Message<TrainingRequestDto> trainingRequestMessage);
}
