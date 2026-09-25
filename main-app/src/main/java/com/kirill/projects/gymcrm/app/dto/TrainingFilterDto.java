package com.kirill.projects.gymcrm.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"traineeUsername", "trainerUsername", "trainingName", "trainingTypeName", "periodFrom", "periodTo"})
@Schema(description = "Filter criteria for searching training sessions in the Gym CRM system")
public record TrainingFilterDto(
        @JsonProperty("traineeUsername")
        @Schema(
                description = "Username of the trainee in the training session",
                example = "John.Doe"
        )
        String traineeUsername,

        @JsonProperty("trainerUsername")
        @Schema(
                description = "Username of the trainer in the training session",
                example = "Sarah.Davis"
        )
        String trainerUsername,

        @JsonProperty("trainingName")
        @Schema(
                description = "Name of the training session",
                example = "Weekend Crossfit"
        )
        String trainingName,

        @JsonProperty("trainingTypeName")
        @Schema(
                description = "Name of the training type",
                example = "Crossfit"
        )
        String trainingTypeName,

        @JsonProperty("periodFrom")
        @Schema(
                description = "Start of the period for filtering training sessions in ISO format (YYYY-MM-DDTHH:MM:SS)",
                example = "2024-07-05T12:30:00"
        )
        LocalDateTime periodFrom,

        @JsonProperty("periodTo")
        @Schema(
                description = "End of the period for filtering training sessions in ISO format (YYYY-MM-DDTHH:MM:SS)",
                example = "2024-07-06T12:30:00"
        )
        LocalDateTime periodTo
) {}
