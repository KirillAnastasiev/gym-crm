package com.epam.laboratory.app.dto;

import com.epam.laboratory.app.dto.annotation.Required;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"active"})
public record ChangeStatusRequestDto(
        @Required @JsonProperty("active") boolean active
) {}
