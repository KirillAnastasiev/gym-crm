package com.epam.laboratory.app.dto;

import com.epam.laboratory.app.dto.annotation.Required;
import com.epam.laboratory.app.dto.annotation.Sensitive;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;
import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "firstName", "lastName", "username", "password", "dateOfBirth", "address", "trainers", "active"})
public record TraineeDto(
        @JsonProperty("id") Long id,
        @Required @JsonProperty("firstName") String firstName,
        @Required @JsonProperty("lastName") String lastName,
        @JsonProperty("username") String username,
        @JsonProperty("password") @Sensitive String password,
        @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
        @JsonProperty("address") String address,
        @JsonProperty("trainers") Collection<TrainerDto> trainers,
        @JsonProperty("active") boolean active
) {}
