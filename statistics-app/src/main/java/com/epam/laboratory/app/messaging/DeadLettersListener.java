package com.epam.laboratory.app.messaging;

public interface DeadLettersListener {
    void handleDeadLetter(String deadLetterMessage);
}
