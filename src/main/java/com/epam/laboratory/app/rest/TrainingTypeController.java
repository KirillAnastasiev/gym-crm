package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.aspect.annotation.RestCallLogging;
import com.epam.laboratory.app.dto.TrainingTypeDto;
import com.epam.laboratory.app.dto.mapper.TrainingTypeMapper;
import com.epam.laboratory.app.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/training-types")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;
    private final TrainingTypeMapper trainingTypeMapper;

    @GetMapping(produces = "application/json")
    @RestCallLogging
    Collection<TrainingTypeDto> getAllTrainingTypes() {
        var trainingTypes = trainingTypeService.selectAll();
        return trainingTypes.stream()
                .map(trainingTypeMapper::toDto)
                .toList();
    }

}
