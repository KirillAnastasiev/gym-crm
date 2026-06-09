package com.epam.laboratory.app.messaging;

import com.epam.laboratory.app.aspect.annotation.Logging;
import org.slf4j.event.Level;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeadLettersListenerImpl implements DeadLettersListener {

    @RabbitListener(queues = "${spring.rabbitmq.dlqueue.name}")
    @Logging(Level.WARN)
    @Override
    public void handleDeadLetter(String deadLetterMessage) {
    }
}
