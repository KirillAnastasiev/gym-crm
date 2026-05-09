package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.dto.TrainingTypeDto;
import com.epam.laboratory.app.dto.mapper.TrainingTypeMapper;
import com.epam.laboratory.app.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/training-types")
@RequiredArgsConstructor
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;
    private final TrainingTypeMapper trainingTypeMapper;

    @GetMapping(produces = "application/json")
    Collection<TrainingTypeDto> getAllTrainingTypes() {
        var trainingTypes = trainingTypeService.selectAll();
        return trainingTypes.stream()
                .map(trainingTypeMapper::toDto)
                .toList();
    }

}
