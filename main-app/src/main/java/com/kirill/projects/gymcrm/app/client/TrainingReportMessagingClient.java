package com.kirill.projects.gymcrm.app.client;

import com.kirill.projects.gymcrm.app.domain.Training;

public interface TrainingReportMessagingClient {
    void sendTrainingReportAdd(Training training);
    void sendTrainingReportDelete(Training training);
}
