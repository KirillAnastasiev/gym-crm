package com.kirill.projects.gymcrm.app.dto;

import com.kirill.projects.gymcrm.app.dto.annotation.Required;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;
import java.time.LocalDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "traineeUsername", "trainerUsername", "trainingName", "trainingType", "trainingDate", "trainingDuration"})
@Schema(description = "Training session in the Gym CRM system")
public record TrainingDto(
        @JsonProperty("id")
        @Schema(
                description = "Unique identifier of the training session",
                example = "1"
        )
        Long id,

        @Required
        @JsonProperty("traineeUsername")
        @Schema(
                description = "Username of the trainee in the training session",
                example = "John.Doe",
                requiredMode = RequiredMode.REQUIRED
        )
        String traineeUsername,

        @Required
        @JsonProperty("trainerUsername")
        @Schema(
                description = "Username of the trainer in the training session",
                example = "Sarah.Davis",
                requiredMode = RequiredMode.REQUIRED
        )
        String trainerUsername,

        @Required
        @JsonProperty("trainingName")
        @Schema(
                description = "Name of the training session",
                example = "Morning Fitness",
                requiredMode = RequiredMode.REQUIRED
        )
        String trainingName,

        @Required
        @JsonProperty("trainingType")
        @Schema(
                description = "Type of the training session",
                example = """
                        {
                            "id": 1,
                            "trainingTypeName": "Fitness"
                        }""",
                requiredMode = RequiredMode.REQUIRED
        )
        TrainingTypeDto trainingType,

        @Required
        @JsonProperty("trainingDate")
        @Schema(
                description = "Date and time of the training session in ISO format (YYYY-MM-DDTHH:MM:SS)",
                example = "2024-07-01T08:00:00",
                requiredMode = RequiredMode.REQUIRED
        )
        LocalDateTime trainingDate,

        @Required
        @JsonProperty("trainingDuration")
        @Schema(
                description = "Duration of the training session in ISO-8601 format (PT1H30M for 1 hour and 30 minutes)",
                example = "PT1H30M",
                requiredMode = RequiredMode.REQUIRED
        )
        Duration trainingDuration

) {}