package com.kirill.projects.gymcrm.app.dto;

import com.kirill.projects.gymcrm.app.dto.annotation.Required;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "trainingTypeName"})
@Schema(description = "Training type in the Gym CRM system")
public record TrainingTypeDto(
        @Required
        @JsonProperty("id")
        @Schema(
                description = "Unique identifier of the training type",
                example = "1",
                requiredMode = RequiredMode.REQUIRED
        )
        Long id,

        @Required
        @JsonProperty("trainingTypeName")
        @Schema(
                description = "Name of the training type",
                example = "Fitness",
                requiredMode = RequiredMode.REQUIRED
        )
        String trainingTypeName
) {}
