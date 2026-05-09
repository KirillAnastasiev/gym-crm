package com.epam.laboratory.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Duration;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "traineeUsername", "trainerUsername", "trainingName", "trainingTypeName", "trainingDate", "trainingDuration"})
public record TrainingDto(
        @JsonProperty("id") Long id,
        @JsonProperty(value = "traineeUsername", required = true) String traineeUsername,
        @JsonProperty(value = "trainerUsername", required = true) String trainerUsername,
        @JsonProperty(value = "trainingName", required = true) String trainingName,
        @JsonProperty(value = "trainingTypeName", required = true) String trainingTypeName,
        @JsonProperty(value = "trainingDate", required = true) LocalDateTime trainingDate,
        @JsonProperty(value = "trainingDuration", required = true) Duration trainingDuration
) {}