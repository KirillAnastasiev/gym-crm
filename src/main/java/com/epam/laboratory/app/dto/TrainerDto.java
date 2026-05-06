package com.epam.laboratory.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "firstName", "lastName", "username", "password", "specialization", "trainees", "active"})
public record TrainerDto(
        @JsonProperty("id") Long id,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("username") String username,
        @JsonProperty("password") String password,
        @JsonProperty("specialization") TrainingTypeDto specialization,
        @JsonProperty("trainees") Collection<TraineeDto> trainees,
        @JsonProperty("active") boolean active
) {}
