package com.epam.laboratory.app.dto;

import com.epam.laboratory.app.dto.annotation.Required;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"traineeUsername", "trainerUsername", "trainingName", "trainingTypeName", "periodFrom", "periodTo"})
public record TrainingFilterDto(
        @JsonProperty("traineeUsername") String traineeUsername,
        @JsonProperty("trainerUsername") String trainerUsername,
        @JsonProperty("trainingName") String trainingName,
        @JsonProperty("trainingTypeName") String trainingTypeName,
        @JsonProperty("periodFrom") LocalDateTime periodFrom,
        @JsonProperty("periodTo") LocalDateTime periodTo
) {}
