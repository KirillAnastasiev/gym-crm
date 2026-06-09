package com.epam.laboratory.app.client;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingReport;
import com.epam.laboratory.app.dto.mapper.TrainingReportMapper;
import com.epam.laboratory.app.util.InputDataValidator;
import com.epam.laboratory.app.util.RequestIdHolder;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TrainingReportRabbitMQMessagingClient implements TrainingReportMessagingClient {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String ACTION_TYPE_HEADER = "X-Action-Type";

    private final RabbitTemplate rabbitTemplate;
    private final TrainingReportMapper trainingReportMapper;

    @Logging(Level.INFO)
    @Override
    public void sendTrainingReportAdd(Training training) {
        InputDataValidator.validateNotNull(training, "Training");
        sendTrainingReport(training, TrainingReport.ActionType.ADD);
    }

    @Logging(Level.INFO)
    @Override
    public void sendTrainingReportDelete(Training training) {
        InputDataValidator.validateNotNull(training, "Training");
        sendTrainingReport(training, TrainingReport.ActionType.DELETE);
    }

    private void sendTrainingReport(Training training, TrainingReport.ActionType actionType) {
        var requestId = RequestIdHolder.getRequestId();
        var trainingReport = createTrainingReport(training);
        rabbitTemplate.convertAndSend(trainingReportMapper.toDto(trainingReport), message -> {
            var properties = message.getMessageProperties();
            properties.setHeader(REQUEST_ID_HEADER, requestId);
            properties.setHeader(ACTION_TYPE_HEADER, actionType);
            return message;
        });
    }

    private static TrainingReport createTrainingReport(Training training) {
        var report = new TrainingReport();
        report.setTrainerUsername(training.getTrainer().getUsername());
        report.setTrainerFirsName(training.getTrainer().getFirstName());
        report.setTrainerLastName(training.getTrainer().getLastName());
        report.setIsActive(training.getTrainer().getActive());
        report.setTrainingDate(training.getTrainingDate());
        report.setTrainingDuration(training.getTrainingDuration());
        return report;
    }
}
