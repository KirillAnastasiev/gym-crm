package com.epam.laboratory.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.Month;
import java.time.Year;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"trainerUsername", "trainingSummary"})
public record TrainingStatisticsDto(

        @NotBlank(message = "Trainer username must not be null")
        @JsonProperty(value = "trainerUsername", required = true)
        String trainerUsername,

        @NotNull(message = "Training summary must not be null")
        @JsonProperty(value = "trainingSummary", required = true)
        Map<Year, Map<Month, Duration>> trainingSummary
) {}
