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
public record TrainingReportRequestDto(

        @NotBlank(message = "Trainer username must not be null or blank")
        @JsonProperty("trainerUsername")
        String trainerUsername,

        @NotBlank(message = "Trainer first name must not be null or blank")
        @JsonProperty("trainerFirstName")
        String trainerFirsName,

        @NotBlank(message = "Trainer last name must not be null or blank")
        @JsonProperty("trainerLastName")
        String trainerLastName,

        @NotNull(message = "Active status must not be null")
        @JsonProperty("trainerActive")
        Boolean isActive,

        @NotNull(message = "Training date must not be null")
        @JsonProperty("trainingDate")
        LocalDateTime trainingDate,

        @NotNull(message = "Training duration must not be null")
        @JsonProperty("trainingDuration")
        Duration trainingDuration,

        @NotNull(message = "Action type must not be null")
        @JsonProperty("actionType")
        String actionType
) {}
