package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.domain.ActionType;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.dto.TrainingRequestDto;
import com.epam.laboratory.app.dto.mapper.TrainingRequestMapper;
import com.epam.laboratory.app.exception.ApplicationException;
import com.epam.laboratory.app.service.TrainingService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/training")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class TrainingController {

    private final TrainingRequestMapper trainingRequestMapper;
    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<Void> newTrainingRequest(@RequestBody @Valid TrainingRequestDto trainingRequestDto,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            throw new ValidationException("Validation failed for training request: %s".formatted(errors));
        }

        log.debug("Received training request {}", trainingRequestDto);
        var training = trainingRequestMapper.toEntity(trainingRequestDto);
        var requestType = trainingRequestDto.actionType();
        doRequestHandling(training, requestType);
        return ResponseEntity.ok(null);
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
