package com.epam.laboratory.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "trainingTypeName"})
public record TrainingTypeDto(
        @JsonProperty("id") Long id,
        @JsonProperty("trainingTypeName") String trainingTypeName
) {}
