package com.epam.laboratory.app.dto;

import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.dto.annotation.Required;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Duration;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "traineeUsername", "trainerUsername", "trainingName", "trainingType", "trainingDate", "trainingDuration"})
public record TrainingDto(
        @JsonProperty("id") Long id,
        @Required @JsonProperty("traineeUsername") String traineeUsername,
        @Required @JsonProperty("trainerUsername") String trainerUsername,
        @Required @JsonProperty("trainingName") String trainingName,
        @Required @JsonProperty("trainingType") TrainingTypeDto trainingType,
        @Required @JsonProperty("trainingDate") LocalDateTime trainingDate,
        @Required @JsonProperty("trainingDuration") Duration trainingDuration
) {}