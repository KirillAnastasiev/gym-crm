package com.epam.laboratory.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@JsonInclude(Include.NON_EMPTY)
@JsonPropertyOrder({"traineeUsername", "trainerUsername", "trainingName", "trainingType", "trainingDate", "trainingDuration"})
public record Training(
        @JsonProperty(value = "traineeUsername", required = true)
        String traineeUsername,

        @JsonProperty(value = "trainerUsername", required = true)
        String trainerUsername,

        @JsonProperty(value = "trainingName", required = true)
        String trainingName,

        @JsonProperty(value = "trainingType", required = true)
        TrainingType trainingType,

        @JsonProperty(value = "trainingDate", required = true)
        LocalDateTime trainingDate,

        @JsonProperty(value = "trainingDuration", required = true)
        Duration trainingDuration
) {}
