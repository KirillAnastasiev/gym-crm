package com.epam.laboratory.app.client;

import com.epam.laboratory.app.domain.Training;

public interface TrainingReportMessagingClient {
    void sendTrainingReportAdd(Training training);
    void sendTrainingReportDelete(Training training);
}
