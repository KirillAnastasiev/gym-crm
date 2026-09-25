package com.kirill.projects.gymcrm.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"firstName", "lastName", "username", "dateOfBirth", "address", "active"})
public record Trainee(

        @JsonProperty("firstName")
        String firstName,

        @JsonProperty("lastName")
        String lastName,

        @JsonProperty("username")
        String username,

        @JsonProperty("dateOfBirth")
        LocalDate dateOfBirth,

        @JsonProperty("address")
        String address,

        @JsonProperty("active")
        Boolean active
) {}
