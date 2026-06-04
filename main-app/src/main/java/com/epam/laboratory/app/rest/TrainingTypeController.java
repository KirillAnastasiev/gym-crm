package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.aspect.annotation.RestCallLogging;
import com.epam.laboratory.app.dto.TrainingTypeDto;
import com.epam.laboratory.app.dto.mapper.TrainingTypeMapper;
import com.epam.laboratory.app.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/training-types")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Training Type Management", description = "Endpoints for managing training types")
public class TrainingTypeController implements Controller {

    private final TrainingTypeService trainingTypeService;
    private final TrainingTypeMapper trainingTypeMapper;


    // ==================== GET MAPPINGS ====================

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RestCallLogging(Level.TRACE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            description = "Retrieve a list of all training types",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "List of training types retrieved successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = TrainingTypeDto.class)
                                    )
                            )
                    )
            }
    )
    ResponseEntity<Collection<TrainingTypeDto>> getAllTrainingTypes(@RequestHeader(REQUEST_ID_HEADER) String requestId) {
        return performRequest(requestId, () -> {
            var trainingTypes = trainingTypeService.selectAll();
            return trainingTypes.stream()
                                .map(trainingTypeMapper::toDto)
                                .toList();
        }, HttpStatus.OK);
    }

}
