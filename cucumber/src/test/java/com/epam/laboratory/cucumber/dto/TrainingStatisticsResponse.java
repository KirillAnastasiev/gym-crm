package com.epam.laboratory.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Duration;
import java.time.Month;
import java.time.Year;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"trainerUsername", "trainingSummary"})
public record TrainingStatisticsResponse(

        @JsonProperty(value = "trainerUsername", required = true)
        String trainerUsername,

        @JsonProperty(value = "trainingSummary", required = true)
        Map<Year, Map<Month, Duration>> trainingSummary
) {}
