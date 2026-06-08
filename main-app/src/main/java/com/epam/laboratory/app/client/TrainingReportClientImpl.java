package com.epam.laboratory.app.client;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingReport;
import com.epam.laboratory.app.dto.mapper.TrainingReportMapper;
import com.epam.laboratory.app.util.RequestIdHolder;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TrainingReportClientImpl implements TrainingReportClient {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    private final RestTemplate restTemplate;
    private final TrainingReportMapper reportMapper;

    @Value("${services.statistics.url}")
    private String statisticsServiceUrl;

    @Logging(Level.INFO)
    @Override
    public void sendTrainingReportAdd(Training training) {
        sendRequest(training, TrainingReport.ActionType.ADD);
    }

    @Logging(Level.INFO)
    @Override
    public void sendTrainingReportDelete(Training training) {
        sendRequest(training, TrainingReport.ActionType.DELETE);
    }

    private void sendRequest(Training training, TrainingReport.ActionType action) {
        var report = createTrainingReport(training, action);
        setRequestIdHeader();
        var uri = UriComponentsBuilder.fromUriString(statisticsServiceUrl)
                .path("/training")
                .build()
                .toUri();
        restTemplate.postForEntity(uri, reportMapper.toDto(report), Void.class);
    }

    private void setRequestIdHeader() {
        var requestId = RequestIdHolder.getRequestId() !=  null ? RequestIdHolder.getRequestId() : "N/A";
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add(REQUEST_ID_HEADER, requestId);
            return execution.execute(request, body);
        });
    }

    private static TrainingReport createTrainingReport(Training training, TrainingReport.ActionType actionType) {
        var report = new TrainingReport();
        report.setTrainerUsername(training.getTrainer().getUsername());
        report.setTrainerFirsName(training.getTrainer().getFirstName());
        report.setTrainerLastName(training.getTrainer().getLastName());
        report.setIsActive(training.getTrainer().getActive());
        report.setTrainingDate(training.getTrainingDate());
        report.setTrainingDuration(training.getTrainingDuration());
        report.setActionType(actionType);
        return report;
    }
}
