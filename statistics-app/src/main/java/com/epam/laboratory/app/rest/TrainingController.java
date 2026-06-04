package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.domain.ActionType;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.dto.TrainingRequestDto;
import com.epam.laboratory.app.dto.mapper.TrainingRequestMapper;
import com.epam.laboratory.app.exception.ApplicationException;
import com.epam.laboratory.app.service.TrainingService;
import com.epam.laboratory.app.util.RequestIdHolder;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/training")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class TrainingController implements Controller {

    private final TrainingRequestMapper trainingRequestMapper;
    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<Void> newTrainingRequest(@RequestBody @Valid TrainingRequestDto trainingRequestDto,
                                                   @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        return performRequest(requestId, new Object[] {trainingRequestDto}, () -> {
            var training = trainingRequestMapper.toEntity(trainingRequestDto);
            var requestType = trainingRequestDto.actionType();
            doRequestHandling(training, requestType);
            return null;
        }, HttpStatus.CREATED, log);
    }

    private void doRequestHandling(Training training, String requestType) {
        try {
            var actionType = ActionType.valueOf(requestType);
            switch (actionType) {
                case ADD -> trainingService.addTraining(training);
                case DELETE -> trainingService.deleteTraining(training);
            }
        } catch (IllegalArgumentException e) {
            throw new ApplicationException("Unsupported training report action type: %s".formatted(requestType));
        }
    }
}
