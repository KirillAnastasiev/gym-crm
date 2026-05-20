package com.epam.laboratory.app.dto;

import com.epam.laboratory.app.dto.annotation.Required;
import com.epam.laboratory.app.dto.annotation.Sensitive;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.OptBoolean;

import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "firstName", "lastName", "username", "password", "specialization", "trainees", "active"})
public record TrainerDto(
        @JsonProperty("id") Long id,
        @Required @JsonProperty("firstName") String firstName,
        @Required @JsonProperty("lastName") String lastName,
        @JsonProperty("username") String username,
        @JsonProperty("password") @Sensitive String password,
        @Required @JsonProperty("specialization") TrainingTypeDto specialization,
        @JsonProperty("trainees") Collection<TraineeDto> trainees,
        @JsonProperty("active") Boolean active
) {}
