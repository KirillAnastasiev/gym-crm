package com.epam.laboratory.app.client;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.TrainingStatistics;
import com.epam.laboratory.app.dto.TrainingStatisticsDto;
import com.epam.laboratory.app.dto.mapper.TrainingStatisticsMapper;
import com.epam.laboratory.app.util.RequestIdHolder;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TrainingStatisticsClientImpl implements TrainingStatisticsClient {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    private final RestTemplate restTemplate;
    private final TrainingStatisticsMapper statsMapper;

    @Value("${services.statistics.url}")
    private String statisticsServiceUrl;

    @Logging(Level.INFO)
    @Override
    public TrainingStatistics getTrainingStatisticsForTrainerInPeriod(String trainingUsername, LocalDate fromDate, LocalDate toDate) {
        return sendRequest(trainingUsername, createQueryParams(fromDate, toDate));
    }

    private MultiValueMap<String, String> createQueryParams(LocalDate fromDate, LocalDate toDate) {
        if  (fromDate == null || toDate == null) {
            return null;
        }
        var queryParams = new LinkedMultiValueMap<String, String>();
        queryParams.add("fromDate", fromDate.toString());
        queryParams.add("toDate", toDate.toString());
        return queryParams;
    }

    private TrainingStatistics sendRequest(String trainerUsername, MultiValueMap<String, String> queryParams) {
        setRequestIdHeader();
        var uri = UriComponentsBuilder.fromUriString(statisticsServiceUrl)
                .path("/statistics/{username}")
                .queryParams(queryParams)
                .buildAndExpand(trainerUsername)
                .toUri();
        var statisticsDto = restTemplate.getForObject(uri, TrainingStatisticsDto.class);
        return statsMapper.toEntity(statisticsDto);
    }

    private void setRequestIdHeader() {
        var requestId = RequestIdHolder.getRequestId() !=  null ? RequestIdHolder.getRequestId() : "N/A";
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add(REQUEST_ID_HEADER, requestId);
            return execution.execute(request, body);
        });
    }

}
