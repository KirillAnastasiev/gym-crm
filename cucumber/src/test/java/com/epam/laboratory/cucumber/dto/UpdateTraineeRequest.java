package com.epam.laboratory.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@JsonInclude(Include.NON_EMPTY)
@JsonPropertyOrder({"firstName", "lastName", "dateOfBirth", "address", "active"})
public record UpdateTraineeRequest(

        @JsonProperty("firstName")
        String firstName,

        @JsonProperty("lastName")
        String lastName,

        @JsonProperty("dateOfBirth")
        LocalDate dateOfBirth,

        @JsonProperty("address")
        String address,

        @JsonProperty("active")
        Boolean active
) {}
