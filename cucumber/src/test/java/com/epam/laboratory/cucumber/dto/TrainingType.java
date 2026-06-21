package com.epam.laboratory.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@JsonInclude(Include.NON_EMPTY)
@JsonPropertyOrder({"id", "trainingTypeName"})
public record TrainingType(
        @JsonProperty(value = "id", required = true)
        Long id,

        @JsonProperty(value = "trainingTypeName", required = true)
        String trainingTypeName
) {}
