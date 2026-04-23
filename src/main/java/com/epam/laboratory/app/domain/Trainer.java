package com.epam.laboratory.app.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = "password")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"specialization"})
public class Trainer extends User {
    @JsonProperty(value = "specialization", required = true)
    private TrainingType specialization;
}
