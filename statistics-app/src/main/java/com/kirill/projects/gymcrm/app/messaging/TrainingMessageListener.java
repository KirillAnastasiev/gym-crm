package com.kirill.projects.gymcrm.app.messaging;

import com.kirill.projects.gymcrm.app.dto.TrainingRequestDto;
import org.springframework.messaging.Message;

public interface TrainingMessageListener {
    void receiveTraining(Message<TrainingRequestDto> trainingRequestMessage);
}
