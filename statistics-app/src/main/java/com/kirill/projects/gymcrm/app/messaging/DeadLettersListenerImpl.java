package com.kirill.projects.gymcrm.app.messaging;

import com.kirill.projects.gymcrm.app.aspect.annotation.Logging;
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
