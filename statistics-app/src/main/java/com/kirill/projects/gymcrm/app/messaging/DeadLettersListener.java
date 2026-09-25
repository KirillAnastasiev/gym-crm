package com.kirill.projects.gymcrm.app.messaging;

public interface DeadLettersListener {
    void handleDeadLetter(String deadLetterMessage);
}
