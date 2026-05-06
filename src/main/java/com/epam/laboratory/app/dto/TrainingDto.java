package com.epam.laboratory.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Duration;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "trainee", "trainer", "trainingName", "trainingType", "trainingDate", "trainingDuration"})
public record TrainingDto(
        @JsonProperty("id") Long id,
        @JsonProperty("trainee") @JsonManagedReference TraineeDto trainee,
        @JsonProperty("trainer") @JsonManagedReference TrainerDto trainer,
        @JsonProperty("trainingName") String trainingName,
        @JsonProperty("trainingType") TrainingTypeDto trainingType,
        @JsonProperty("trainingDate") LocalDateTime trainingDate,
        @JsonProperty("trainingDuration") Duration trainingDuration
) {}