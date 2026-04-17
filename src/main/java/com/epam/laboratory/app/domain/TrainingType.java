package com.epam.laboratory.app.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public enum TrainingType {
    FITNESS,
    YOGA,
    ZUMBA,
    STRETCHING,
    RESISTANCE
}
