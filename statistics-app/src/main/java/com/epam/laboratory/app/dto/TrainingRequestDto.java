package com.epam.laboratory.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"trainerUsername", "trainerFirstName", "trainerLastName", "isActive", "trainingDate", "trainingDuration", "actionType"})
public record TrainingRequestDto(

        @NotBlank(message = "Trainer username must not be null or blank")
        @JsonProperty(value = "trainerUsername", required = true)
        String trainerUsername,

        @NotBlank(message = "Trainer first name must not be null or blank")
        @JsonProperty(value = "trainerFirstName", required = true)
        String trainerFirstName,

        @NotBlank(message = "Trainer last name must not be null or blank")
        @JsonProperty(value = "trainerLastName", required = true)
        String trainerLastName,

        @NotNull(message = "Active status must not be null")
        @JsonProperty(value = "trainerActive", required = true)
        Boolean isActive,

        @NotNull(message = "Training date must not be null")
        @JsonProperty(value = "trainingDate", required = true)
        LocalDateTime trainingDate,

        @NotNull(message = "Training duration must not be null")
        @JsonProperty(value = "trainingDuration", required = true)
        Duration trainingDuration

) {}
