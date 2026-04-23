package com.epam.laboratory.app.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "trainee", "trainer", "trainingName", "trainingType", "trainingDate", "trainingDuration"})
public class Training implements  Entity {
    @JsonProperty(value = "id", required = true)
    private Long id;

    @JsonProperty(value = "trainee", required = true)
    private Trainee trainee;

    @JsonProperty(value = "trainer", required = true)
    private Trainer trainer;

    @JsonProperty(value = "trainingName", required = true)
    private String trainingName;

    @JsonProperty(value = "trainingType", required = true)
    private TrainingType trainingType;

    @JsonProperty(value = "trainingDate", required = true)
    private LocalDateTime trainingDate;

    @JsonProperty(value = "trainingDuration", required = true)
    private Duration trainingDuration;
}
